package org.usfirst.frc.team1554.lib.vision;

import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.usfirst.frc.team1554.lib.io.Console;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.IMAQdxBufferNumberMode;
import com.ni.vision.NIVision.IMAQdxCameraControlMode;
import com.ni.vision.NIVision.IMAQdxEnumItem;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.dxEnumerateVideoModesResult;
import com.ni.vision.VisionException;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.image.ColorImage;
import edu.wpi.first.wpilibj.image.HSLImage;
import edu.wpi.first.wpilibj.image.NIVisionException;

/**
 * Generic Implementation of Ethernet Camera that is nearly identical to {@link USBCamera}
 * but does an additional search for connected Ethernet Devices. <br />
 * <br />
 * This class is in Beta and is not guaranteed to stay or function correctly. Improvements
 * will be made as needed since this is not guaranteed to work and will change if more NIVision
 * functions become available or if Ethernet specific Functions need to be used. <br />
 * <br />
 * This class will likely become more specific over time.
 * @author Matthew
 *
 */
public class EthernetCamera implements Camera {

	public static final String DEFAULT_NAME = "cam0";
	
	public static final String AUTO = "Auto";
	public static final String MANUAL = "Manual";
	
	private Pattern reMode = Pattern.compile("(?<width>[0-9]+)\\s*x\\s*(?<height>[0-9]+)\\s+(?<format>.*?)\\s+(?<fps>[0-9.]+)\\s*fps");
	
	private static final String ATTR_VIDEO_MODE = "AcquisitionAttributes::VideoMode";
	private static final String ATTR_WB_MODE = "CameraAttributes::WhiteBalance::Mode";
	private static final String ATTR_WB_VALUE = "CameraAttributes::WhiteBalance::Value";
	private static final String ATTR_EX_MODE = "CameraAttributes::Exposure::Mode";
	private static final String ATTR_EX_VALUE = "CameraAttributes::Exposure::Value";
	private static final String ATTR_BR_MODE = "CameraAttributes::Brightness::Mode";
	private static final String ATTR_BR_VALUE = "CameraAttributes::Brightness::Value";
	
	public final String name;
	public final String ethernetAddress;
	private int vid = -1;
	
	private CameraFPS fps = CameraFPS.NORMAL;
	private CameraSize resolution = CameraSize.LARGE;
	private CameraQuality quality = CameraQuality.MEDIUM;
	private WhiteBalance wbalance = WhiteBalance.AUTO;
	private Exposure exposure = Exposure.AUTO;
	private int brightness = 50;
	
	private boolean active = false;
	private boolean update = true;
	private boolean jpeg = true;
	
	public EthernetCamera(String ethernetAddress, String name) {
		this.name = name;
		this.ethernetAddress = ethernetAddress;
		open();
	}
	
	@Override
	public synchronized void open() {
		if(vid != -1) return;
		
		NIVision.IMAQdxDiscoverEthernetCameras(ethernetAddress, 500);
		
		for(int i = 0; i < 3; i++) {
			try {
				vid = NIVision.IMAQdxOpenCamera(name, IMAQdxCameraControlMode.CameraControlModeController);
			} catch(VisionException e) {
				if(i==2)
					throw e;
				
				Timer.delay(2);
				continue;
			}
			break;
		}
	}

	@Override
	public void close() {
		if(vid == -1) return;
		
		NIVision.IMAQdxCloseCamera(vid);
		vid = -1;
	}
	
	@Override
	public void startCapture() {
		if(vid == -1 || active) return;
		
		NIVision.IMAQdxConfigureGrab(vid);
		NIVision.IMAQdxStartAcquisition(vid);
		active = true;
	}

	@Override
	public void stopCapture() {
		if(vid == -1 || !active) return;
		
		NIVision.IMAQdxStopAcquisition(vid);
		NIVision.IMAQdxUnconfigureAcquisition(vid);
		active = false;
	}

	@Override
	public synchronized void setFPS(CameraFPS fps) {
		if(fps != this.fps) {
			update = true;
			this.fps = fps;
		}
	}

	@Override
	public synchronized void setSize(CameraSize size) {
		if(size != this.resolution) {
			update = true;
			this.resolution = size;
		}
	}

	@Override
	public synchronized void setQuality(CameraQuality qual) {
		if(qual != this.quality) {
			update = true;
			this.quality = qual;
		}
	}

	@Override
	public synchronized void setBrightness(int brightness) {
		if(brightness != this.brightness) {
			update = true;
			this.brightness = brightness;
		}
	}

	@Override
	public synchronized void updateSettings() {
		boolean wasActive = active;
		
		if(wasActive)
			stopCapture();
		if(vid != -1)
			close();
		open();
		
		dxEnumerateVideoModesResult enumerated = NIVision.IMAQdxEnumerateVideoModes(vid);
		IMAQdxEnumItem foundItem = null;
		int foundFPS = 1000;
		for(IMAQdxEnumItem mode : enumerated.videoModeArray) {
			Matcher matcher = reMode.matcher(mode.Name);
			
			if(!matcher.matches())
				continue;
			if(Integer.parseInt(matcher.group("width")) != resolution.WIDTH) // Incorrect Width
				continue;
			if(Integer.parseInt(matcher.group("height")) != resolution.HEIGHT) // Incorrect Height
				continue;
			
			// Check if FPS is Valid for current params
			double frames = Double.parseDouble(matcher.group("fps"));
			if(frames < fps.kFPS || frames > foundFPS)
				continue;
			
			// Check JPEG Format
			String fmt = matcher.group("format");
			boolean jpg = fmt.equalsIgnoreCase("jpeg");
			
			if (jpeg && !jpg || !jpeg && jpg)
				continue;
			
			foundItem = mode;
			foundFPS = (int) frames;
		}
		
		// Set to Found Mode
		if(foundItem != null) {
			Console.debug("Found Mode " + foundItem.Value + ": " + foundItem.Name);
			if(foundItem.Value != enumerated.currentMode)
				NIVision.IMAQdxSetAttributeU32(vid, ATTR_VIDEO_MODE, foundItem.Value);
		}
		
		// White Balance Set
		if (wbalance.name().equalsIgnoreCase("auto"))
			NIVision.IMAQdxSetAttributeString(vid, ATTR_WB_MODE, AUTO);
		else {
			NIVision.IMAQdxSetAttributeString(vid, ATTR_WB_MODE, MANUAL);
			if(wbalance.getWhiteBalance() != -1)
				NIVision.IMAQdxSetAttributeI64(vid, ATTR_WB_VALUE, wbalance.getWhiteBalance());
		}
		
		// Exposure Set
		if (exposure.name().equalsIgnoreCase("auto"))
			NIVision.IMAQdxSetAttributeString(vid, ATTR_EX_MODE, AUTO);
		else {
			NIVision.IMAQdxSetAttributeString(vid, ATTR_EX_MODE, MANUAL);
			
			// Exposure has an offset Minimum
			if(exposure.getExposure() != -1) {
				long exp = exposure.getExposure();
				long max = NIVision.IMAQdxGetAttributeMaximumI64(vid, ATTR_EX_VALUE);
				long min = NIVision.IMAQdxGetAttributeMinimumI64(vid, ATTR_EX_VALUE);
				double rangedVal = (double)(max - min) * ((double) exp / 100.0);
				
				long val = min + Math.round(rangedVal);
				NIVision.IMAQdxSetAttributeI64(vid, ATTR_EX_VALUE, val);
			}
		}
		
		// Brightness
		// Brightness has an offset min
		NIVision.IMAQdxSetAttributeString(vid, ATTR_BR_MODE, MANUAL);
		long max = NIVision.IMAQdxGetAttributeMaximumI64(vid, ATTR_BR_VALUE);
		long min = NIVision.IMAQdxGetAttributeMinimumI64(vid, ATTR_BR_VALUE);
		double rangedVal = (double)(max - min) * ((double)brightness/100.0);
		
		long val = min + Math.round(rangedVal);
		NIVision.IMAQdxSetAttributeI64(vid, ATTR_BR_VALUE, val);
		
		// If it was active, restart capture
		if(wasActive)
			startCapture();
	}

	@Override
	public CameraFPS getFPS() {
		return fps;
	}

	@Override
	public CameraSize getSize() {
		return resolution;
	}

	@Override
	public CameraQuality getQuality() {
		return quality;
	}

	@Override
	public int getBrightness() {
		return brightness;
	}

	@Override
	public synchronized ColorImage getImage() throws NIVisionException {
		if(update || jpeg) {
			update = false;
			jpeg = false;
			updateSettings();
		}
		
		HSLImage img = new HSLImage();
		NIVision.IMAQdxGrab(vid, img.image, 1);
		return img;
	}
	
	@Override
	public synchronized boolean _frameGrab(Image image) {
		if(update || jpeg) {
			update = false;
			jpeg = false;
			updateSettings();
		}
		
		NIVision.IMAQdxGrab(vid, image, 1);
		return true;
	}

	@Override
	public synchronized void getImageData(ByteBuffer buffer) {
		if(update || !jpeg){
			update = false;
			jpeg = false;
			updateSettings();
		}
		
		NIVision.IMAQdxGetImageData(vid, buffer, IMAQdxBufferNumberMode.BufferNumberModeLast, 0);
		buffer.limit(buffer.capacity() - 1);
		buffer.limit(Camera.getJpegSize(buffer));
	}

	@Override
	public int getID_IMAQdx() {
		return vid;
	}

}
