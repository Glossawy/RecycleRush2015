package org.usfirst.frc.team1554.lib.vision;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.*;
import com.ni.vision.VisionException;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.image.ImageBase;
import edu.wpi.first.wpilibj.tables.ITable;
import org.usfirst.frc.team1554.lib.common.Console;
import org.usfirst.frc.team1554.lib.net.*;
import org.usfirst.frc.team1554.lib.util.BufferUtils;
import org.usfirst.frc.team1554.lib.util.IOUtils;
import org.usfirst.frc.team1554.lib.util.RoboUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Deque;

import static org.usfirst.frc.team1554.lib.net.ServerSocket.Protocol.TCP;
import static org.usfirst.frc.team1554.lib.net.SocketParams.*;

//@off
// FIXME Rewrite a More Efficient Camera Stream impl. This is currently slightly optimized port of CameraServer but is loosely coupled to the Camera API
//@on

/**
 * An Alternative to {@link edu.wpi.first.wpilibj.CameraServer} that makes use of the Camera
 * API. <br />
 * <br />
 * This is currently a port of CameraServer with several
 * optimizations.
 *
 * @author Matthew
 * @since v1.0
 */
@SuppressWarnings({"InfiniteLoopStatement", "WeakerAccess"})
public enum CameraStream implements Sendable {

    INSTANCE;

    /**
     * Data Store class for containing Image Data Buffer metadata. <br
     * />
     * <br />
     * Typically this is just the NIVision {@link com.ni.vision.NIVision.RawData} and buffer
     * start position.
     *
     * @author Matthew
     */
    private static class Data {

        final RawData data;
        final int start;

        Data(RawData data, int s) {
            this.data = data;
            this.start = s;
        }

    }

    public static final int PORT = 1180;

    // WPILib UID
    public static final byte[] MAGIC_NUMBERS = {0x01, 0x00, 0x00, 0x00};
    public static final int hwCompression = -1;
    public static final int MAX_SIZE = 200_000;

    private Camera camera = null;
    private Data cameraData = null;

    private final Deque<ByteBuffer> dataPool = new ArrayDeque<>(3);

    private ITable paramTable;
    private boolean captureStarted;
    private boolean hwClient = true;

    private CameraStream() {

        // Loop Unwinding, a 3 iteration for loop is unnecessary.
        // Establish Data Buffers available for Image I/O
        //
        // These are DirectByteBuffer's and MUST be freed manually
        // using BufferUtils.disposeUnsafeByteBuffer since they use JNI and Native Resources!
        this.dataPool.addLast(BufferUtils.newUnsafeByteBuffer(MAX_SIZE));
        this.dataPool.addLast(BufferUtils.newUnsafeByteBuffer(MAX_SIZE));
        this.dataPool.addLast(BufferUtils.newUnsafeByteBuffer(MAX_SIZE));

        // Create Serving Thread
        Thread serverThread = new Thread(() -> {
            try {
                serveStream();
            } catch (final Exception e) {
                Console.exception(e);
            }
        });
        serverThread.setName("CameraStream-Out");
        serverThread.start();
    }

    private synchronized void setImageData(RawData data, int start) {

		/*
         * Set the Camera Data being sent to the new Camera Data.
		 * 
		 * Remember that Data objects hold onto the buffers that originate from our Data Pool. This means that we may handle MANY Data objects but only use 3 Buffers at a time.
		 * 
		 * The addLast() call at the end just makes the buffer available to the serving thread.
		 */
        if ((this.cameraData != null) && (this.cameraData.data != null)) {
            this.cameraData.data.free();
            if (this.cameraData.data.getBuffer() != null) {
                this.dataPool.addLast(this.cameraData.data.getBuffer());
            }

            this.cameraData = null;
        }

        this.cameraData = new Data(data, start);
        notifyAll();
        // Let the Server Thread know we have an avaiable image.
    }

    /**
     * Set the Image Currently being Streamed to the Client
     *
     * @param image
     */
    public void setImage(ImageBase image) {
        setImage(image.image);
    }

    /**
     * Set the Image Currently being Streamed to the Client <br />
     * <br />
     * {@link #setImage(edu.wpi.first.wpilibj.image.ImageBase) setImage} essentially calls this
     * method with the {@link edu.wpi.first.wpilibj.image.ImageBase Image's} underlying NIVision
     * Image Object.
     *
     * @param image
     */
    public void setImage(Image image) {

        // Flatten Image to JPEG and get JPEG Data Buffer
        final RawData data = NIVision.imaqFlatten(image, FlattenType.FLATTEN_IMAGE, CompressionType.COMPRESSION_JPEG, 10 * this.camera.getQuality().kCompression);
        final ByteBuffer buffer = data.getBuffer();

        boolean hwClient;
        synchronized (this) {
            hwClient = this.hwClient;
        }

        // Find the start of the image, skips any possible junk data.
        // 0xFF and 0xD8 are the Start Bytes described by the JPEG Specification
        int index = 0;
        if (hwClient) {
            while (index < (buffer.limit() - 1)) {
                if (((buffer.get(index) & 0xFF) == 0xFF) && ((buffer.get(index + 1) & 0xFF) == 0xD8)) {
                    break;
                }

                index++;
            }
        }

        // If there are only the start bytes then we have a problem.
        if ((buffer.limit() - index - 1) <= 2)
            throw new VisionException("Data size of flattened image is less than 2. Try another camera!");

        setImageData(data, index);
    }

    /**
     * Set the Camera and Automatically Capture images in a separate
     * thread. This is useful if you don't wish to use {@link
     * #setImage(edu.wpi.first.wpilibj.image.ImageBase)} in your loop (typically for on-roboRIO
     * image processing) or wish to use the DriverStation for Image
     * Processing.
     *
     * @param camera
     */
    public void startAutomaticCapture(Camera camera) {
        if (this.captureStarted) return;

        camera.open();
        this.captureStarted = true;
        this.camera = camera;

        this.camera.startCapture();

        final Thread captureThread = new Thread(() -> runCapture());

        captureThread.setName("CameraStream-Capture");
        captureThread.start();
    }

    private void serveStream() throws InterruptedException {
        final ServerSocketParams params = new ServerSocketParams();
        params.acceptTimeout = 0;

        final SocketParams clientParams = new SocketParams();
        clientParams.connectionTimeout = 0;
        clientParams.trafficClass = TRAFFIC_LOWDELAY | TRAFFIC_RELIABLE | TRAFFIC_THROUGHPUT;

        try (ServerSocket socket = new RoboServerSocket(TCP, PORT, params)) {
            while (true) {
                try {
                    final Socket s = socket.accept(clientParams);

                    final DataInputStream is = new DataInputStream(s.input());
                    final DataOutputStream os = new DataOutputStream(s.output());

                    // We must take the data but we do not use Compression or Quality
                    final int fps = is.readInt();
                    final int compression = is.readInt();
                    is.readInt();

                    // We must be in HW Compression mode. As per WPILib Specification
                    if (compression != hwCompression) {
                        RoboUtils.writeToDS("Choose \"USB Camera HW\" on the dashboard!");
                        s.close();
                        continue;
                    }

                    synchronized (this) {
                        Console.debug("Waiting for Camera to be ready...");
                        if (this.camera == null) {
                            wait();
                        }

                        this.hwClient = compression == hwCompression;
                        if (!this.hwClient) {
                            setQuality(100 - compression);
                        }
                    }

                    // Period between frames (keeps it smooth, like a Delta Time)
                    // 1/fps = seconds per frame
                    final long period = (long) (1000 / (1.0 * fps));
                    while (true) {
                        // Start Time (T-0)
                        final long t0 = System.currentTimeMillis();

                        // Get current Image Data (Set in a previous setImage call
                        // or by the Capture Thread.
                        // Will wait until notified of new Capture Data via
                        // notifyAll()
                        Data imgData = null;
                        synchronized (this) {
                            imgData = this.cameraData;
                            this.cameraData = null;
                        }

                        if (imgData == null) {
                            continue;
                        }

						/*
                         * Set buffer position to start of data, and then createMethodCall a new wrapper for the data at that position
						 */
                        imgData.data.getBuffer().position(imgData.start);
                        final byte[] imgArr = new byte[imgData.data.getBuffer().remaining()];
                        imgData.data.getBuffer().get(imgArr, 0, imgData.data.getBuffer().remaining());

                        try {
                            // Networking UID
                            os.write(MAGIC_NUMBERS);
                            os.writeInt(imgArr.length);
                            os.write(repair(imgArr));
                            os.flush();

                            // Calculate Delta Time [Time Used to Send Image]
                            // Sleep For the remainder of the period to keep constant
                            // frame rate.
                            final long dt = System.currentTimeMillis() - t0;
                            if (dt < period) {
                                Thread.sleep(period - dt);
                            }
                        } catch (IOException | UnsupportedOperationException e) {
                            // Write to DS and Console
                            RoboUtils.exceptionToDS(e);
                            Console.exception(e);
                            break;
                        } finally {
                            // Free Buffer Data (It is a Native DirectByteBuffer)
                            // and give it back to the Buffer Pool for re-use.
                            imgData.data.free();
                            if (imgData.data.getBuffer() != null) {
                                synchronized (this) {
                                    this.dataPool.addLast(imgData.data.getBuffer());
                                }
                            }
                        }
                    }
                } catch (final IOException e) {
                    RoboUtils.exceptionToDS(e);
                    Console.exception(e);
                }
            }
        }
    }

    private void runCapture() {

        final Image frame = NIVision.imaqCreateImage(ImageType.IMAGE_RGB, 0);
        while (true) {
            boolean hwClient;
            ByteBuffer dataBuffer = null;

            // Retrieve usable Buffer from Buffer Pool
            synchronized (this) {
                hwClient = this.hwClient;
                if (hwClient) {
                    dataBuffer = this.dataPool.pollLast();
                }
            }

            try {
                // Retrieve the current frame's data.
                if (hwClient && (dataBuffer != null)) {
                    dataBuffer.limit(dataBuffer.capacity() - 1);
                    this.camera.getImageData(dataBuffer);
                    setImageData(new RawData(dataBuffer), 0);
                } else {
                    this.camera._frameGrab(frame);
                    setImage(frame);
                }
            } catch (final VisionException e) {
                RoboUtils.exceptionToDS(e);
                Console.exception(e);
                // Free Data Buffer if we have it still
                if (dataBuffer != null) {
                    synchronized (this) {
                        this.dataPool.addLast(dataBuffer);
                        Timer.delay(.1);
                    }
                }
            }
        }
    }

    public synchronized boolean isCaptureStarted() {
        return this.captureStarted;
    }

    public synchronized void setSize(CameraSize size) {
        if (this.camera == null) return;

        this.camera.setSize(size);
    }

    public synchronized CameraSize getSize() {
        if (this.camera == null) return CameraSize.MEDIUM;

        return this.camera.getSize();
    }

    public synchronized void setQuality(CameraQuality quality) {
        if (this.camera != null) {
            this.camera.setQuality(quality);
        }
    }

    private synchronized void setQuality(int quality) {
        setQuality(CameraQuality.getBestFor(quality));
    }

    public synchronized CameraQuality getQuality() {
        return this.camera.getQuality();
    }

    @Override
    public void initTable(ITable subtable) {
        this.paramTable = subtable;
        updateNetworkTable();
    }

    public void updateNetworkTable() {
        if (this.paramTable == null) return;

        this.paramTable.putNumber("Compression", (double) this.camera.getQuality().kCompression / 100);
        this.paramTable.putString("Camera Size", String.format("%dx%d", this.camera.getSize().WIDTH, this.camera.getSize().HEIGHT));
        this.paramTable.putNumber("Camera FPS", this.camera.getFPS().kFPS);
    }

    @Override
    public ITable getTable() {
        return this.paramTable;
    }

    @Override
    public String getSmartDashboardType() {
        return "Camera Stream";
    }

    // JPEG Repair. Mostly Mimicking http://www.mail-archive.com/linux-uvc-devel@lists.berlios.de/msg02255/mjpeg2jpeg.py
    // Even to the point of using Byte Streams for a more human readable implemnentation.

    /**
     * Int Table Representation of Huffman Table. Is Converted to
     * Byte[] during initialization.
     */
    private static final int[] huffmanTableTemp = new int[]{
            0xFF, 0xC4, 0x01, 0xA2, 0x00, 0x00, 0x01, 0x05, 0x01, 0x01,
            0x01, 0x01, 0x01, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
            0x07, 0x08, 0x09, 0x0A, 0x0B, 0x01, 0x00, 0x03, 0x01,
            0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x02, 0x03, 0x04,
            0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x10, 0x00,
            0x02, 0x01, 0x03, 0x03, 0x02, 0x04, 0x03, 0x05, 0x05,
            0x04, 0x04, 0x00, 0x00, 0x01, 0x7D, 0x01, 0x02, 0x03,
            0x00, 0x04, 0x11, 0x05, 0x12, 0x21, 0x31, 0x41, 0x06,
            0x13, 0x51, 0x61, 0x07, 0x22, 0x71, 0x14, 0x32, 0x81,
            0x91, 0xA1, 0x08, 0x23, 0x42, 0xB1, 0xC1, 0x15, 0x52,
            0xD1, 0xF0, 0x24, 0x33, 0x62, 0x72, 0x82, 0x09, 0x0A,
            0x16, 0x17, 0x18, 0x19, 0x1A, 0x25, 0x26, 0x27, 0x28,
            0x29, 0x2A, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3A,
            0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49, 0x4A, 0x53,
            0x54, 0x55, 0x56, 0x57, 0x58, 0x59, 0x5A, 0x63, 0x64,
            0x65, 0x66, 0x67, 0x68, 0x69, 0x6A, 0x73, 0x74, 0x75,
            0x76, 0x77, 0x78, 0x79, 0x7A, 0x83, 0x84, 0x85, 0x86,
            0x87, 0x88, 0x89, 0x8A, 0x92, 0x93, 0x94, 0x95, 0x96,
            0x97, 0x98, 0x99, 0x9A, 0xA2, 0xA3, 0xA4, 0xA5, 0xA6,
            0xA7, 0xA8, 0xA9, 0xAA, 0xB2, 0xB3, 0xB4, 0xB5, 0xB6,
            0xB7, 0xB8, 0xB9, 0xBA, 0xC2, 0xC3, 0xC4, 0xC5, 0xC6,
            0xC7, 0xC8, 0xC9, 0xCA, 0xD2, 0xD3, 0xD4, 0xD5, 0xD6,
            0xD7, 0xD8, 0xD9, 0xDA, 0xE1, 0xE2, 0xE3, 0xE4, 0xE5,
            0xE6, 0xE7, 0xE8, 0xE9, 0xEA, 0xF1, 0xF2, 0xF3, 0xF4,
            0xF5, 0xF6, 0xF7, 0xF8, 0xF9, 0xFA, 0x11, 0x00, 0x02,
            0x01, 0x02, 0x04, 0x04, 0x03, 0x04, 0x07, 0x05, 0x04,
            0x04, 0x00, 0x01, 0x02, 0x77, 0x00, 0x01, 0x02, 0x03,
            0x11, 0x04, 0x05, 0x21, 0x31, 0x06, 0x12, 0x41, 0x51,
            0x07, 0x61, 0x71, 0x13, 0x22, 0x32, 0x81, 0x08, 0x14,
            0x42, 0x91, 0xA1, 0xB1, 0xC1, 0x09, 0x23, 0x33, 0x52,
            0xF0, 0x15, 0x62, 0x72, 0xD1, 0x0A, 0x16, 0x24, 0x34,
            0xE1, 0x25, 0xF1, 0x17, 0x18, 0x19, 0x1A, 0x26, 0x27,
            0x28, 0x29, 0x2A, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3A,
            0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49, 0x4A, 0x53,
            0x54, 0x55, 0x56, 0x57, 0x58, 0x59, 0x5A, 0x63, 0x64,
            0x65, 0x66, 0x67, 0x68, 0x69, 0x6A, 0x73, 0x74, 0x75,
            0x76, 0x77, 0x78, 0x79, 0x7A, 0x82, 0x83, 0x84, 0x85,
            0x86, 0x87, 0x88, 0x89, 0x8A, 0x92, 0x93, 0x94, 0x95,
            0x96, 0x97, 0x98, 0x99, 0x9A, 0xA2, 0xA3, 0xA4, 0xA5,
            0xA6, 0xA7, 0xA8, 0xA9, 0xAA, 0xB2, 0xB3, 0xB4, 0xB5,
            0xB6, 0xB7, 0xB8, 0xB9, 0xBA, 0xC2, 0xC3, 0xC4, 0xC5,
            0xC6, 0xC7, 0xC8, 0xC9, 0xCA, 0xD2, 0xD3, 0xD4, 0xD5,
            0xD6, 0xD7, 0xD8, 0xD9, 0xDA, 0xE2, 0xE3, 0xE4, 0xE5,
            0xE6, 0xE7, 0xE8, 0xE9, 0xEA, 0xF2, 0xF3, 0xF4, 0xF5,
            0xF6, 0xF7, 0xF8, 0xF9, 0xFA
    };

    /**
     * Fully Defined Huffman Table to repair some MJPEG Streams
     */
    private static final byte[] huffmanTable;

    static {
        // This was copy and pasted from a Python Solution
        // having to (byte) cast all those numbers individually would be painful.
        //
        // I might at a later date, just to avoid a for loop
        // But that may be being a bit too pedantic with execution time and memory.
        huffmanTable = new byte[huffmanTableTemp.length];

        for (int i = 0; i < huffmanTable.length; i++)
            huffmanTable[i] = (byte) huffmanTableTemp[i];
    }

    private byte[] repair(byte[] img) {
        boolean dht = false;
        byte[] res = img;

        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            is = new ByteArrayInputStream(img);
            os = new ByteArrayOutputStream(img.length + huffmanTable.length);

            if (is.skip(2) != 2)
                throw new IllegalStateException("MJPEG had size < 2 bytes!");


            // See JPEG Format Spec or getJpegSize in Camera.java for a summary of Byte Headers/Indicators.
            byte[] bits = new byte[4];
            int pos = 2;
            for (int c = is.read(bits); c != -1 && !dht; c = is.read(bits)) {
                if (bits[0] != 0xFF)
                    return img;

                if (bits[1] == 0xC4)
                    dht = true;
                else if (bits[1] == 0xDA)
                    break;

                os.write(bits, 0, c);
                pos += ((bits[2] & 0xFF) << 8) + (bits[3] & 0xFF) + 2;
            }

            if (!dht) {
                os.write(huffmanTable);
                os.write(img, pos, img.length - pos);
            }

            res = os.toByteArray();
        } catch (IOException e) {
            Console.exception(e);
        } finally {
            IOUtils.closeSilently(os);
            IOUtils.closeSilently(is);
        }

        return res;
    }

}
