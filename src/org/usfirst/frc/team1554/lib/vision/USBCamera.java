package org.usfirst.frc.team1554.lib.vision;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.*;
import com.ni.vision.VisionException;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.image.ColorImage;
import edu.wpi.first.wpilibj.image.HSLImage;
import edu.wpi.first.wpilibj.image.NIVisionException;
import org.usfirst.frc.team1554.lib.Console;

import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of WPILib's USB Camera with the Camera API. Comes with some optimizations and modifications (some to fit with the new Enum based values).
 *
 * @author Matthew
 */
public class USBCamera implements Camera {

    public static final String DEFAULT_NAME = "cam0";
    public static final String AUTO = "Auto";
    public static final String MANUAL = "Manual";

    private static final String ATTR_VIDEO_MODE = "AcquisitionAttributes::VideoMode";
    private static final String ATTR_WB_MODE = "CameraAttributes::WhiteBalance::Mode";
    private static final String ATTR_WB_VALUE = "CameraAttributes::WhiteBalance::Value";
    private static final String ATTR_EX_MODE = "CameraAttributes::Exposure::Mode";
    private static final String ATTR_EX_VALUE = "CameraAttributes::Exposure::Value";
    private static final String ATTR_BR_MODE = "CameraAttributes::Brightness::Mode";
    private static final String ATTR_BR_VALUE = "CameraAttributes::Brightness::Value";

    private final Pattern reMode = Pattern.compile("(?<width>[0-9]+)\\s*x\\s*(?<height>[0-9]+)\\s+(?<format>.*?)\\s+(?<fps>[0-9.]+)\\s*fps");

    private boolean update = true;
    private boolean active = false;
    private boolean jpeg = true;
    private CameraQuality quality = CameraQuality.MEDIUM;
    private CameraSize dimensions = CameraSize.MEDIUM;
    private CameraFPS fps = CameraFPS.NORMAL;
    private final WhiteBalance wbalance = WhiteBalance.AUTO;
    private final Exposure exposure = Exposure.AUTO;
    private int brightness = 50;

    private final String name;
    private int vid = -1;

    public USBCamera(String name) {
        this.name = name;
        open();
    }

    public USBCamera() {
        this(DEFAULT_NAME);
    }

    @Override
    public synchronized void open() {
        if (this.vid != -1) return;

        for (int i = 0; i < 3; i++) {
            try {
                this.vid = NIVision.IMAQdxOpenCamera(this.name, IMAQdxCameraControlMode.CameraControlModeController);
            } catch (final VisionException e) {
                if (i == 2) throw e;
                Timer.delay(2);
                continue;
            }
            break;
        }
    }

    @Override
    public synchronized void close() {
        if (this.vid == -1) return;

        NIVision.IMAQdxCloseCamera(this.vid);
        this.vid = -1;
    }

    @Override
    public synchronized void startCapture() {
        if ((this.vid == -1) || this.active) return;

        NIVision.IMAQdxConfigureGrab(this.vid);
        NIVision.IMAQdxStartAcquisition(this.vid);
        this.active = true;
    }

    @Override
    public synchronized void stopCapture() {
        if ((this.vid == -1) || !this.active) return;

        NIVision.IMAQdxStopAcquisition(this.vid);
        NIVision.IMAQdxUnconfigureAcquisition(this.vid);
        this.active = false;
    }

    @Override
    public synchronized void setFPS(CameraFPS newfps) {
        if (newfps != this.fps) {
            this.update = true;
            this.fps = newfps;
        }
    }

    @Override
    public synchronized void setSize(CameraSize newsize) {
        if (newsize != this.dimensions) {
            this.update = true;
            this.dimensions = newsize;
        }
    }

    @Override
    public synchronized void setQuality(CameraQuality qual) {
        if (qual != this.quality) {
            this.quality = qual;
        }
    }

    @Override
    public synchronized void setBrightness(int brightness) {
        if (brightness == this.brightness) return;

        this.brightness = Math.max(Math.min(brightness, 100), 0);
    }

    @Override
    public synchronized void updateSettings() {
        final boolean wasActive = this.active;

        if (wasActive) {
            stopCapture();
        }
        if (this.vid != -1) {
            close();
        }
        open();

        // Video Mode
        final dxEnumerateVideoModesResult enumerated = NIVision.IMAQdxEnumerateVideoModes(this.vid);
        IMAQdxEnumItem found = null; // The Mode we Found
        int foundFps = 1000; // Impossible Initial FPS
        for (final IMAQdxEnumItem mode : enumerated.videoModeArray) {
            final Matcher matcher = this.reMode.matcher(mode.Name);

            if (!matcher.matches()) {
                continue;
            }
            if (Integer.parseInt(matcher.group("width")) != this.dimensions.WIDTH) {
                continue;
            }
            if (Integer.parseInt(matcher.group("height")) != this.dimensions.HEIGHT) {
                continue;
            }

            // Check if FPS is Valid for current params
            final double frames = Double.parseDouble(matcher.group("fps"));
            if ((frames < this.fps.kFPS) || (frames > foundFps)) {
                continue;
            }

            // Check JPEG Format
            final String fmt = matcher.group("format");
            final boolean jpg = fmt.equalsIgnoreCase("jpeg");

            if ((this.jpeg && !jpg) || (!this.jpeg && jpg)) {
                continue;
            }

            found = mode;
            foundFps = (int) frames;
        }

        // Set to Found Mode
        if (found != null) {
            Console.debug("Found Mode " + found.Value + ": " + found.Name);
            if (found.Value != enumerated.currentMode) {
                NIVision.IMAQdxSetAttributeU32(this.vid, ATTR_VIDEO_MODE, found.Value);
            }
        }

        // White Balance Set
        if (this.wbalance.name().equalsIgnoreCase("auto")) {
            NIVision.IMAQdxSetAttributeString(this.vid, ATTR_WB_MODE, AUTO);
        } else {
            NIVision.IMAQdxSetAttributeString(this.vid, ATTR_WB_MODE, MANUAL);
            if (this.wbalance.getWhiteBalance() != -1) {
                NIVision.IMAQdxSetAttributeI64(this.vid, ATTR_WB_VALUE, this.wbalance.getWhiteBalance());
            }
        }

        // Exposure Set
        if (this.exposure.name().equalsIgnoreCase("auto")) {
            NIVision.IMAQdxSetAttributeString(this.vid, ATTR_EX_MODE, "AutoAperaturePriority");
        } else {
            NIVision.IMAQdxSetAttributeString(this.vid, ATTR_EX_MODE, MANUAL);

            // Exposure has an offset Minimum
            if (this.exposure.getExposure() != -1) {
                final long exp = this.exposure.getExposure();
                final long max = NIVision.IMAQdxGetAttributeMaximumI64(this.vid, ATTR_EX_VALUE);
                final long min = NIVision.IMAQdxGetAttributeMinimumI64(this.vid, ATTR_EX_VALUE);
                final double rangedVal = (max - min) * (exp / 100.0);

                final long val = min + Math.round(rangedVal);
                NIVision.IMAQdxSetAttributeI64(this.vid, ATTR_EX_VALUE, val);
            }
        }

        // Brightness
        // Brightness has an offset min
        NIVision.IMAQdxSetAttributeString(this.vid, ATTR_BR_MODE, MANUAL);
        final long max = NIVision.IMAQdxGetAttributeMaximumI64(this.vid, ATTR_BR_VALUE);
        final long min = NIVision.IMAQdxGetAttributeMinimumI64(this.vid, ATTR_BR_VALUE);
        final double rangedVal = (max - min) * (this.brightness / 100.0);

        final long val = min + Math.round(rangedVal);
        NIVision.IMAQdxSetAttributeI64(this.vid, ATTR_BR_VALUE, val);

        // If it was active, restart capture
        if (wasActive) {
            startCapture();
        }
    }

    @Override
    public synchronized CameraFPS getFPS() {
        return this.fps;
    }

    @Override
    public synchronized CameraSize getSize() {
        return this.dimensions;
    }

    @Override
    public synchronized CameraQuality getQuality() {
        return this.quality;
    }

    @Override
    public synchronized int getBrightness() {
        return this.brightness;
    }

    @Override
    public synchronized ColorImage getImage() throws NIVisionException {
        if (this.update || this.jpeg) {
            this.update = false;
            this.jpeg = false;
            updateSettings();
        }

        final HSLImage img = new HSLImage();
        NIVision.IMAQdxGrab(this.vid, img.image, 1);
        return img;
    }

    @Override
    public synchronized boolean _frameGrab(Image image) {
        if (this.update || this.jpeg) {
            this.update = false;
            this.jpeg = false;
            updateSettings();
        }

        NIVision.IMAQdxGrab(this.vid, image, 1);
        return true;
    }

    @Override
    public synchronized void getImageData(ByteBuffer buffer) {
        if (this.update || !this.jpeg) {
            this.update = false;
            this.jpeg = true;
            updateSettings();
        }

        NIVision.IMAQdxGetImageData(this.vid, buffer, IMAQdxBufferNumberMode.BufferNumberModeLast, 0);
        buffer.limit(buffer.capacity() - 1);
        buffer.limit(Camera.getJpegSize(buffer));
    }

    @Override
    public int getID_IMAQdx() {
        return this.vid;
    }

}
