package org.usfirst.frc.team1554.lib.vision;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.Image;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.image.ColorImage;
import edu.wpi.first.wpilibj.image.HSLImage;
import edu.wpi.first.wpilibj.image.NIVisionException;
import org.usfirst.frc.team1554.lib.common.Console;
import org.usfirst.frc.team1554.lib.util.IOUtils;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Implementation of WPILib's AxisCamera. Comes with quite a few
 * optimizations and modifications to fit into this API and to use a
 * few modern features.
 *
 * @author Matthew
 */
public class AxisCamera implements Camera {

    private static final int IMAGE_BUFFER_INCREMENT = 1000;

    private final String host;
    private Socket socket;

    private ByteBuffer imageData = ByteBuffer.allocate(5000);
    private final Lock imageDataLock = new ReentrantLock(true);
    private final Lock parameterLock = new ReentrantLock(true);

    private boolean freshImage = false;
    private int brightness = 50;
    private int colorLevel = 50;
    private int exposurePriority = 50;
    private CameraFPS fps = CameraFPS.NORMAL;
    private CameraSize resolution = CameraSize.LARGE;
    private CameraQuality quality = CameraQuality.MEDIUM;
    private final WhiteBalance wbalance = WhiteBalance.AUTO;
    private final Exposure exposure = Exposure.AUTO;

    private boolean flipImage = false;
    private boolean dirtyParam = true;
    private boolean dirtyStream = true;
    private boolean done = false;

    private Thread camThread;

    public AxisCamera(String host) {
        this.host = host;
        open();
    }

    @Override
    public void open() {
        this.camThread = new Thread(() -> {
            int errors = 0;

            while (!AxisCamera.this.done) {
                final String request = "GET /mjpg/video.mjpg HTTP/1.1\n" + "User-Agent: HTTPStreamClient\n" + "Connection: Keep-Alive\n" + "Cache-Control: no-cache\n" + "Authorization: Basic R1JDOkZSQw==\n\n";

                try {
                    AxisCamera.this.socket = createSocket(request);
                    readImages();

                    errors = 0;
                } catch (final IOException e) {
                    errors++;

                    if (errors > 5) {
                        Console.exception(e);
                    }
                }

                Timer.delay(0.5);
            }
        });
    }

    @Override
    public void startCapture() {
        this.camThread.start();
    }

    @Override
    public void stopCapture() {
        this.done = true;
    }

    @Override
    public void close() {
        IOUtils.closeSilently(this.socket);
    }

    @Override
    public void setFPS(CameraFPS fps) {
        if (fps == this.fps) return;

        this.parameterLock.lock();
        try {
            this.fps = fps;
            this.dirtyParam = true;
            this.dirtyStream = true;
        } finally {
            this.parameterLock.unlock();
        }
    }

    @Override
    public void setSize(CameraSize size) {
        if (size == this.resolution) return;

        this.parameterLock.lock();
        try {
            this.resolution = size;
            this.dirtyParam = true;
            this.dirtyStream = true;
        } finally {
            this.parameterLock.unlock();
        }
    }

    @Override
    public void setQuality(CameraQuality qual) {
        if (qual == this.quality) return;

        this.parameterLock.lock();
        try {
            this.quality = qual;
            this.dirtyParam = true;
            this.dirtyStream = true;
        } finally {
            this.parameterLock.unlock();
        }
    }

    @Override
    public void setBrightness(int brightness) {
        if (brightness == this.brightness) return;

        this.parameterLock.lock();
        try {
            this.brightness = Math.max(Math.min(brightness, 100), 0);
            this.dirtyParam = true;
            this.dirtyStream = true;
        } finally {
            this.parameterLock.unlock();
        }
    }

    public void setColorLevel(int colorLevel) {
        if (colorLevel == this.colorLevel) return;

        this.parameterLock.lock();
        try {
            this.colorLevel = Math.max(Math.min(colorLevel, 100), 0);
            this.dirtyParam = true;
            this.dirtyStream = true;
        } finally {
            this.parameterLock.unlock();
        }
    }

    public void setExposurePriority(int priority) {
        if (priority == this.exposurePriority) return;

        this.parameterLock.lock();
        try {
            this.exposurePriority = Math.max(Math.min(priority, 100), 0);
            this.dirtyParam = true;
            this.dirtyStream = true;
        } finally {
            this.parameterLock.unlock();
        }
    }

    @Override
    public void updateSettings() {
        writeParameters();
    }

    @Override
    public CameraFPS getFPS() {
        return this.fps;
    }

    @Override
    public CameraSize getSize() {
        return this.resolution;
    }

    @Override
    public CameraQuality getQuality() {
        return this.quality;
    }

    @Override
    public int getBrightness() {
        return this.brightness;
    }

    public int getColorLevel() {
        return this.colorLevel;
    }

    public int getExposurePriority() {
        return this.exposurePriority;
    }

    @Override
    public ColorImage getImage() throws NIVisionException {
        final HSLImage img = new HSLImage();

        if (this.imageData.limit() == 0) return img;

        this.imageDataLock.lock();
        try {
            NIVision.Priv_ReadJPEGString_C(img.image, this.imageData.array());
        } finally {
            this.imageDataLock.unlock();
        }

        this.freshImage = false;
        return img;
    }

    public boolean getImage(ColorImage img) {
        if (this.imageData.limit() == 0) return false;

        this.imageDataLock.lock();
        try {
            NIVision.Priv_ReadJPEGString_C(img.image, this.imageData.array());
        } finally {
            this.imageDataLock.unlock();
        }

        return true;
    }

    @Override
    public boolean _frameGrab(Image img) {
        if (this.imageData.limit() == 0) return false;

        this.imageDataLock.lock();
        try {
            NIVision.Priv_ReadJPEGString_C(img, this.imageData.array());
        } finally {
            this.imageDataLock.unlock();
        }

        return true;
    }

    @Override
    public void getImageData(ByteBuffer buffer) {
        this.imageDataLock.lock();
        try {
            buffer.put(this.imageData);
        } finally {
            this.imageDataLock.unlock();
        }
    }

    public boolean isFreshImage() {
        return this.freshImage;
    }

    private boolean writeParameters() {
        if (this.dirtyParam) {
            final StringBuilder request = new StringBuilder("GET /axis-cgi/admin/param.cgi?action=update");

            this.parameterLock.lock();
            try {
                request.append("&ImageSource.I0.Sensor.Brightness=").append(this.brightness);
                request.append("&ImageSource.I0.Sensor.WhiteBalance=").append(this.wbalance.ethernetName());
                request.append("&ImageSource.I0.Sensor.ColorLevel=").append(this.colorLevel);
                request.append("&ImageSource.I0.Sensor.Exposure=").append(this.exposure.ethernetName());
                request.append("&ImageSource.I0.Sensor.ExposurePriority=").append(this.exposurePriority);
                request.append("&Image.I0.Stream.FPS=").append(this.fps.kFPS);
                request.append("&Image.I0.Appearance.Resolution=").append(this.resolution.WIDTH).append('x').append(this.resolution.HEIGHT);
                request.append("&Image.I0.Appearance.Compression=").append(this.quality.kCompression);
                request.append("&Image.I0.Appearance.Rotation=").append(flipImage ? "180" : "0");
            } finally {
                this.parameterLock.unlock();
            }

            request.append(" HTTP/1.1\n").append("User-Agent: HTTPStreamClient\n").append("Connection: Keep-Alive\n").append("Cache-Control: no-cache\n").append("Authorization: Basic R1JDOkZSQw==\n\n");

            try {
                final Socket socket = createSocket(request.toString());
                socket.close();

                this.dirtyParam = false;

                if (this.dirtyStream) {
                    this.dirtyStream = false;
                    return true;
                } else
                    return false;
            } catch (IOException | NullPointerException e) {
                return false;
            }
        }

        return false;
    }

    @SuppressWarnings("deprecation")
    private void readImages() throws IOException {
        // TODO Use Reader? Nulls may kill it.
        final DataInputStream camInput = new DataInputStream(this.socket.getInputStream());

        while (!this.done) {
            final String line = camInput.readLine();

            if (line.startsWith("Content-Length: ")) {
                int length = Integer.parseInt(line.substring(16));

                camInput.readLine();
                length -= 4;

                final byte[] data = new byte[length];
                camInput.readFully(data);

                this.imageDataLock.lock();
                try {
                    if (this.imageData.capacity() < data.length) {
                        this.imageData = ByteBuffer.allocate(data.length + AxisCamera.IMAGE_BUFFER_INCREMENT);
                    }

                    this.imageData.clear();
                    this.imageData.limit(length);
                    this.imageData.put(data);

                    this.freshImage = true;
                } finally {
                    this.imageDataLock.unlock();
                }

                if (writeParameters()) {
                    break;
                }

                camInput.readLine();
                camInput.readLine();
            }
        }

        this.socket.close();
    }

    private Socket createSocket(String request) throws IOException {
        final Socket socket = new Socket();
        socket.connect(new InetSocketAddress(this.host, 80), 5000);

        final OutputStream out = socket.getOutputStream();
        out.write(request.getBytes());

        return socket;
    }

    @Override
    public int getID_IMAQdx() {
        return -1;
    }

}
