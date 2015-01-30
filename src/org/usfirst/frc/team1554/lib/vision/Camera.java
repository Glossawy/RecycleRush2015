package org.usfirst.frc.team1554.lib.vision;

import java.nio.ByteBuffer;

import org.usfirst.frc.team1554.lib.meta.Beta;
import org.usfirst.frc.team1554.lib.meta.Noteworthy;

import com.ni.vision.NIVision.Image;
import com.ni.vision.VisionException;

import edu.wpi.first.wpilibj.image.ColorImage;
import edu.wpi.first.wpilibj.image.NIVisionException;

/**
 * Defines minimal requirements of a Camera so that WPI's AxisCamera and USBCamera
 * can be interchanged and used in generalized interfaces with other Camera
 * Implementations.
 * 
 * @author Matthew
 *
 */
@Beta
@Noteworthy("This Interface is subject to modification while in @Beta")
public interface Camera {

	/** Open Camera */
	void open();

	/** Close Camera */
	void close();

	/** Start Capture */
	void startCapture();

	/** End Capture */
	void stopCapture();

	/** Set FPS to a Pre-defined Setting */
	void setFPS(CameraFPS fps);

	/** Set Size to a Pre-defined Picture Size */
	void setSize(CameraSize size);

	/** Set Quality to a Pre-defined Resolution */
	void setQuality(CameraQuality qual);

	/** Set Brightness in the range [0, 100] */
	void setBrightness(int brightness);

	/** Communicate with Camera to Update Settings */
	void updateSettings();

	/** Get Current FPS Setting */
	CameraFPS getFPS();

	/** Get Current Size Setting */
	CameraSize getSize();

	/** Get Current Quality Setting */
	CameraQuality getQuality();

	/** Get Current Brightness Setting */
	int getBrightness();

	/**
	 * Typically a Camera is connected using NIVision IMAQdx. If it is, this should
	 * return that ID.
	 */
	int getID_IMAQdx();

	/** Get Current Camera Image */
	ColorImage getImage() throws NIVisionException;

	/** Get Current Camera Image Data */
	boolean _frameGrab(Image image);

	/** Get Current Camera Image Data */
	void getImageData(ByteBuffer buffer);

	/**
	 * From a {@link ByteBuffer} of JPEG Image Data this method will determing the
	 * JPEG size.
	 * 
	 * @param data
	 * @return
	 */
	public static int getJpegSize(ByteBuffer data) {
		if (data.get(0) != (byte) 0xff || data.get(1) != (byte) 0xd8) throw new VisionException("Invalid Image");
		int pos = 2;
		while (true) {
			try {
				int b = data.get(pos) & 0xff;
				if (b != 0xff) throw new VisionException("invalid image at pos " + pos + " (" + data.get(pos) + ")");

				b = data.get(pos + 1) & 0xff;
				if ((b == 0x01) || ((b >= 0xd0) && (b <= 0xd7))) {
					pos += 2;
				} else if (b == 0xd9)
					return pos + 2;
				else if (b == 0xd8)
					throw new VisionException("Invalid Image");
				else if (b == 0xda) {
					final int len = ((data.get(pos + 2) & 0xff) << 8) | (data.get(pos + 3) & 0xff);
					pos += len + 2;

					// Find Next Marked, Skip Escaped and RST
					while ((data.get(pos) != (byte) 0xff) || (data.get(pos + 1) == (byte) 0x00) || ((data.get(pos + 1) >= (byte) 0xd0) && (data.get(pos + 1) <= (byte) 0xd7))) {
						pos += 1;
					}
				} else {
					final int len = ((data.get(pos + 2) & 0xff) << 8) | (data.get(pos + 3) & 0xff);
					pos += len + 2;
				}
			} catch (final IndexOutOfBoundsException ex) {
				throw new VisionException("Invalid Image: Could Not Find JPEG End Byte -> " + ex.getMessage());
			}
		}
	}

	/**
	 * If you have multiple Cameras, this method is a convenience method for creating
	 * names. Typically the naming convention is equivalent to
	 * <code>"cam" + index</code>
	 * 
	 * @param camIndex
	 * @return
	 */
	public static String nameOf(int camIndex) {
		return "cam" + camIndex;
	}
}
