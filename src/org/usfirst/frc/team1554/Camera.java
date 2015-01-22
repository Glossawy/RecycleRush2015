package org.usfirst.frc.team1554;

import java.nio.ByteBuffer;

import com.ni.vision.NIVision;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.image.HSLImage;
import edu.wpi.first.wpilibj.image.NIVisionException;
import edu.wpi.first.wpilibj.image.RGBImage;
import edu.wpi.first.wpilibj.vision.USBCamera;

public final class Camera {
	
	public enum CameraSize {
		SMALL(0, 160, 120),
		MEDIUM(1, 320, 240),
		LARGE(2, 640, 480);
		
		public final int kSize;
		public final int WIDTH;
		public final int HEIGHT;
		
		private CameraSize(int i, int width, int height) {
			this.kSize = i;
			this.WIDTH = width;
			this.HEIGHT = height;
		}
		
		@Override
		public String toString() {
			return String.format("cam_size(%s - %s | %sx%s)", kSize, name(), WIDTH, HEIGHT);
		}
		
	}
	
	public enum CameraResolution {
		
		VERY_LOW(10),
		LOW(25),
		MEDIUM(50),
		HIGH(75),
		VERY_HIGH(90),
		EXTREME(100);
		
		public final int kResolution;
		
		private CameraResolution(int quality) {
			this.kResolution = quality;
		}
		
		@Override
		public String toString() {
			return String.format("cam_res(%s: %s%%)", name(), kResolution);
		}
		
	}
	
	public enum CameraFPS {
		MINIMAL(5),
		LOW(15),
		NORMAL(30),
		HIGH(60);
		
		public final int kFPS;
		
		private CameraFPS(int fps) {
			this.kFPS = fps;
		}
		
		@Override
		public String toString() {
			return String.format("cam_fps(%s: %s FPS)", name(), kFPS);
		}
	}
	
	private static final ByteBuffer imgData = ByteBuffer.allocate(5000);
	private static final USBCamera cam = new USBCamera(Ref.CAM_NAME);
	private static final CameraServer server = CameraServer.getInstance();
	
	static {
		cam.setExposureAuto();
		cam.setWhiteBalanceAuto();
		cam.setFPS(Ref.Values.CAM_FPS.kFPS);
		
		server.startAutomaticCapture(cam);
		server.setSize(Ref.Values.CAM_SIZE.kSize);
		server.setQuality(Ref.Values.CAM_RES.kResolution);
	}
	
	public static void setFPS(CameraFPS fps) {
		cam.setFPS(fps.kFPS);
	}
	
	public static void setQuality(CameraResolution res) {
		server.setQuality(res.kResolution);
	}
	
	public static void setSize(CameraSize size) {
		server.setSize(size.kSize);
	}
	
	public static RGBImage getImageRGB() throws NIVisionException {
		RGBImage image = new RGBImage();
		NIVision.Priv_ReadJPEGString_C(image.image, imgData.array());
		return image;
	}
	
	public static HSLImage getImageHSL() throws NIVisionException {
		HSLImage image = new HSLImage();
		NIVision.Priv_ReadJPEGString_C(image.image, imgData.array());
		return image;
	}
	
}
