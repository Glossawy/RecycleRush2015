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

	/**
	 * Get Current Camera Image Data Used internally by CameraStream to get NIVision
	 * Image Data.
	 */
	boolean _frameGrab(Image image);

	/** Get Current Camera Image Data */
	void getImageData(ByteBuffer buffer);

	/**
	 * From a {@link ByteBuffer} of JPEG Image Data this method will determine the
	 * JPEG size. <br />
	 * <br />
	 * This makes use of markers defined in the JPEG Standard: <br />
	 * <a href="http://en.wikipedia.org/wiki/JPEG#Syntax_and_structure">Wikipedia:
	 * JPEG -- Syntax and Structure</a>
	 * 
	 * @param data
	 * @return
	 */
	public static int getJpegSize(ByteBuffer data) {
		if ((data.get(0) != (byte) 0xff) || (data.get(1) != (byte) 0xd8)) throw new VisionException("Invalid Image");
		int pos = 2;

		/*
		 * We are going to make use of the Markers defined in JPEG to calculate the
		 * size. Already we have the SOI [Start of Image] Bytes.
		 * 
		 * The Protocol Follows: RST [Restart] --> Skip the Marker, pos + 2 DQT
		 * [Quant. Tbl.] --> NIVision does not produce these, throw exception SOS
		 * [Start of Scan]--> Skip to end and find next marked, pos + len +
		 * len2marked + 2 EOI [End Of Image] --> Skip the two end bytes and return,
		 * pos + 2 Others --> Get Length and Skip, pos + len + 2
		 * 
		 * We add 2 to each because each start of the marker is marked by 0xFF and
		 * some marker byte. Therefore we add two for start bytes.
		 * 
		 * Other accounts for: APP [Application Specific Markers] COM [Comments] DRI
		 * [Define Restart Interval] DHT [Define Huffman Table] SOF0 [Start of Frame
		 * Baseline] SOF2 [Start of Frame Progressive]
		 * 
		 * Which all follow: 0xFF [some byte] [len byte 1] [len byte 2] [content]
		 */

		while (true) {
			try {
				// Every JPEG Marker starts with 0xFF
				int b = data.get(pos) & 0xFF; // Gets the Unsigned Value of a Signed
				// Byte
				if (b != 0xFF) throw new VisionException("invalid image at pos " + pos + " (" + data.get(pos) + ")");

				b = data.get(pos + 1) & 0xFF;
				// RST Marker [Restart]
				if ((b == 0x01) || ((b >= 0xD0) && (b <= 0xD7))) {
					pos += 2;
					// EOI Marker [End of Image]
				} else if (b == 0xD9)
					return pos + 2;
				// DQT Marker [Define Quantization Table]
				else if (b == 0xD8)
					throw new VisionException("Invalid Image");
				// SOS Marker [Start of Scan]
				else if (b == 0xDA) {
					// Get Length Values from Proceeding 2 Bytes and move to the end
					final int len = ((data.get(pos + 2) & 0xff) << 8) | (data.get(pos + 3) & 0xff);
					pos += len + 2;

					// Find Next Marked, Skip Escaped and RST
					while ((data.get(pos) != (byte) 0xFF) || (data.get(pos + 1) == (byte) 0x00) || ((data.get(pos + 1) >= (byte) 0xD0) && (data.get(pos + 1) <= (byte) 0xD7))) {
						pos += 1;
					}
				} else {
					// Get Length Bytes
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
