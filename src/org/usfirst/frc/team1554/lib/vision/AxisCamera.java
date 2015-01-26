package org.usfirst.frc.team1554.lib.vision;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.usfirst.frc.team1554.lib.io.Console;
import org.usfirst.frc.team1554.lib.util.IOUtils;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.Image;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.image.ColorImage;
import edu.wpi.first.wpilibj.image.HSLImage;
import edu.wpi.first.wpilibj.image.NIVisionException;

/**
 * Implementation of WPILib's AxisCamera. Comes with quite a few optimizations
 * and modifications to fit into this API and to use a few modern features.
 * 
 * @author Matthew
 */
public class AxisCamera implements Camera{

	private static final int IMAGE_BUFFER_INCREMENT = 1000;
	
	private String host;
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
	private WhiteBalance wbalance = WhiteBalance.AUTO;
	private Exposure exposure = Exposure.AUTO;
	
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
		camThread = new Thread(new Runnable() {
			@Override
			public void run() {
				int errors = 0;
				
				while(!done) {
					String request = "GET /mjpg/video.mjpg HTTP/1.1\n" +
									 "User-Agent: HTTPStreamClient\n" +
									 "Connection: Keep-Alive\n"+
									 "Cache-Control: no-cache\n" +
									 "Authorization: Basic R1JDOkZSQw==\n\n";
					
					try {
						socket = createSocket(request);
						readImages();
						
						errors = 0;
					} catch (IOException e) {
						errors++;
						
						if(errors > 5)
							Console.exception(e);
					}
					
					Timer.delay(0.5);
				}
			}
		});
	}

	@Override
	public void startCapture() {
		camThread.start();
	}

	@Override
	public void stopCapture() {
		done = true;
	}

	@Override
	public void close() {
		IOUtils.closeSilently(socket);
	}

	@Override
	public void setFPS(CameraFPS fps) {
		if(fps == this.fps) return;
		
		parameterLock.lock();
		try{ 
			this.fps = fps;
			dirtyParam = true;
			dirtyStream = true;
		} finally {
			parameterLock.unlock();
		}
	}

	@Override
	public void setSize(CameraSize size) {
		if(size == this.resolution) return;
		
		parameterLock.lock();
		try {
			this.resolution = size;
			dirtyParam = true;
			dirtyStream = true;
		} finally {
			parameterLock.unlock();
		}
	}

	@Override
	public void setQuality(CameraQuality qual) {
		if(qual == this.quality) return;
		
		parameterLock.lock();
		try {
			this.quality = qual;
			dirtyParam = true;
			dirtyStream = true;
		} finally {
			parameterLock.unlock();
		}
	}
	
	@Override
	public void setBrightness(int brightness) {
		if(brightness == this.brightness) return;
		
		parameterLock.lock();
		try {
			this.brightness = Math.max(Math.min(brightness, 100), 0);
			dirtyParam = true;
			dirtyStream = true;
		} finally {
			parameterLock.unlock();
		}
	}
	
	public void setColorLevel(int colorLevel) {
		if(colorLevel == this.colorLevel) return;
		
		parameterLock.lock();
		try{
			this.colorLevel = Math.max(Math.min(colorLevel, 100), 0);
			dirtyParam = true;
			dirtyStream = true;
		} finally {
			parameterLock.unlock();
		}
	}
	
	public void setExposurePriority(int priority) {
		if(priority == this.exposurePriority) return;
		
		parameterLock.lock();
		try {
			this.exposurePriority = Math.max(Math.min(priority, 100), 0);
			dirtyParam = true;
			dirtyStream = true;
		} finally {
			parameterLock.unlock();
		}
	}

	@Override
	public void updateSettings() {
		writeParameters();
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
	
	public int getColorLevel() {
		return colorLevel;
	}
	
	public int getExposurePriority() {
		return exposurePriority;
	}
	
	@Override
	public ColorImage getImage() throws NIVisionException {
		HSLImage img = new HSLImage();
		
		if(imageData.limit() == 0)
			return img;
		
		imageDataLock.lock();
		try {
			NIVision.Priv_ReadJPEGString_C(img.image, imageData.array());
		} finally {
			imageDataLock.unlock();
		}
		
		freshImage = false;
		return img;
	}
	
	public boolean getImage(ColorImage img) throws NIVisionException {
		if(imageData.limit() == 0)
			return false;
		
		imageDataLock.lock();
		try {
			NIVision.Priv_ReadJPEGString_C(img.image, imageData.array());
		} finally {
			imageDataLock.unlock();
		}
		
		return true;
	}
	
	@Override
	public boolean _frameGrab(Image img) {
		if(imageData.limit() == 0)
			return false;
		
		imageDataLock.lock();
		try{
			NIVision.Priv_ReadJPEGString_C(img, imageData.array());
		} finally {
			imageDataLock.unlock();
		}
		
		return true;
	}

	@Override
	public void getImageData(ByteBuffer buffer) {
		imageDataLock.lock();
		try {
			buffer.put(imageData);
		} finally {
			imageDataLock.unlock();
		}
	}
	
	public boolean isFreshImage() {
		return freshImage;
	}
	
	private boolean writeParameters() {
		if(dirtyParam) {
			StringBuilder request = new StringBuilder("GET /axis-cgi/admin/param.cgi?action=update");
			
			parameterLock.lock();
			try{
				request.append("&ImageSource.I0.Sensor.Brightness=").append(brightness);
				request.append("&ImageSource.I0.Sensor.WhiteBalance=").append(wbalance.ethernetName());
				request.append("&ImageSource.I0.Sensor.ColorLevel=").append(colorLevel);
				request.append("&ImageSource.I0.Sensor.Exposure=").append(exposure.ethernetName());
				request.append("&ImageSource.I0.Sensor.ExposurePriority=").append(exposurePriority);
				request.append("&Image.I0.Stream.FPS=").append(fps.kFPS);
				request.append("&Image.I0.Appearance.Resolution=").append(resolution.WIDTH).append('x').append(resolution.HEIGHT);
				request.append("&Image.I0.Appearance.Compression=").append(quality.kCompression);
				request.append("&Image.I0.Appearance.Rotation=").append(flipImage ? "180" : "0");
			} finally {
				parameterLock.unlock();
			}
			
			request.append(" HTTP/1.1\n")
				   .append("User-Agent: HTTPStreamClient\n")
				   .append("Connection: Keep-Alive\n")
				   .append("Cache-Control: no-cache\n")
				   .append("Authorization: Basic R1JDOkZSQw==\n\n");
			
			try {
				Socket socket = createSocket(request.toString());
				socket.close();
				
				dirtyParam = false;
				
				if(dirtyStream) {
					dirtyStream = false;
					return true;
				} else {
					return false;
				}
			} catch(IOException | NullPointerException e) {
				return false;
			}
		}
		
		return false;
	}
	
	private void readImages() throws IOException {
		DataInputStream camInput = new DataInputStream(socket.getInputStream());
		BufferedReader reader = new BufferedReader(new InputStreamReader(camInput));
		
		while(!done) {
			String line = reader.readLine();
			
			if(line.startsWith("Content-Length: ")) {
				int length = Integer.parseInt(line.substring(16));
				
				reader.readLine();
				length -= 4;
				
				byte[] data = new byte[length];
				camInput.readFully(data);
				
				imageDataLock.lock();
				try {
					if(imageData.capacity() < data.length) 
						imageData = ByteBuffer.allocate(data.length + AxisCamera.IMAGE_BUFFER_INCREMENT);
					
					imageData.clear();
					imageData.limit(length);
					imageData.put(data);
					
					freshImage = true;
				} finally {
					imageDataLock.unlock();
				}
				
				if(writeParameters())
					break;
				
				reader.readLine();
				reader.readLine();
			}
		}
		
		socket.close();
	}
	
	private Socket createSocket(String request) throws IOException {
		Socket socket = new Socket();
		socket.connect(new InetSocketAddress(host, 80), 5000);
		
		OutputStream out = socket.getOutputStream();
		out.write(request.getBytes());
		
		return socket;
	}

	@Override
	public int getID_IMAQdx() {
		return -1;
	}

}
