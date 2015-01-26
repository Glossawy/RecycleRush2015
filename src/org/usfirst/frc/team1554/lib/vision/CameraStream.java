package org.usfirst.frc.team1554.lib.vision;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Deque;

import org.usfirst.frc.team1554.lib.io.Console;
import org.usfirst.frc.team1554.lib.util.RoboUtils;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.ImageType;
import com.ni.vision.VisionException;
import com.ni.vision.NIVision.CompressionType;
import com.ni.vision.NIVision.FlattenType;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.RawData;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.image.ColorImage;

// TODO Rewrite a More Efficient Camera Stream impl. This is currently slightly optimized port of CameraServer but is loosely coupled to the Camera API
/**
 * An Alternative to {@link CameraServer} that makes use of the Camera API.
 * @author Matthew
 *
 */
public enum CameraStream {
	
	INSTANCE;
	
	private static class Data { 
		
		RawData data;
		int start;
		
		Data(RawData data, int s) {
			this.data = data;
			this.start = s;
		}
		
	}
	
	public static final int PORT = 1180;
	public static final byte[] MAGIC_NUMBERS = {0x01, 0x00, 0x00, 0x00};
	public static final int hwCompression = -1;
	public static final int MAX_SIZE = 200_000;
	
	private Thread serverThread;
	private Camera camera = null;
	private Data cameraData = null;
	private int quality = 50;
	private Deque<ByteBuffer> dataPool = new ArrayDeque<>();
	
	private boolean captureStarted;
	private boolean hwClient = true;
	
	private CameraStream() {
		dataPool.addLast(ByteBuffer.allocateDirect(MAX_SIZE));
		
		serverThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					serveStream();
				} catch(Exception e) {
					Console.exception(e);
				}
			}
		});
		serverThread.setName("CameraStream-Out");
		serverThread.start();
	}
	
	private synchronized void setImageData(RawData data, int start) {
		if(cameraData != null && cameraData.data != null) {
			cameraData.data.free();
			if(cameraData.data.getBuffer() != null) 
				dataPool.addLast(cameraData.data.getBuffer());
			
			cameraData = null;
		}
		
		cameraData = new Data(data, start);
		notifyAll();
	}
	
	public void setImage(ColorImage image) {
		setImage(image.image);
	}
	
	public void setImage(Image image) {
		RawData data = NIVision.imaqFlatten(image, FlattenType.FLATTEN_IMAGE, CompressionType.COMPRESSION_JPEG, 10 * quality);
		ByteBuffer buffer = data.getBuffer();
		
		boolean hwClient;
		synchronized(this) {
			hwClient = this.hwClient;
		}
		
		int index = 0;
		if(hwClient) 
			for(;index < buffer.limit() - 1 && !((buffer.get(index) & 0xff) == 0xff && (buffer.get(index +1) & 0xff) == 0xd8); index++);
		
		if(buffer.limit() - index - 1 <= 2)
			throw new VisionException("Data size of flattened image is less than 2. Try another camera!");
		
		setImageData(data, index);
	}
	
	public void startAutomaticCapture(Camera camera) {
		if(this.captureStarted) return;
		
		camera.open();
		this.captureStarted = true;
		this.camera = camera;
		
		this.camera.startCapture();
		
		Thread captureThread = new Thread(new Runnable() {
			@Override
			public void run() {
				runCapture();
			}
		});
		
		captureThread.setName("CameraStream Capture");
		captureThread.start();
	}
	
	private void serveStream() throws IOException, InterruptedException {
		try(ServerSocket socket = new ServerSocket()){
			socket.setReuseAddress(true);
			socket.bind(new InetSocketAddress(PORT));
			while(true) {
				try {
					Socket s = socket.accept();
					
					DataInputStream is = new DataInputStream(s.getInputStream());
					DataOutputStream os = new DataOutputStream(s.getOutputStream());
					
					int fps = is.readInt();
					int compression = is.readInt();
					@SuppressWarnings("unused")
					int size = is.readInt();
					
					if(compression != hwCompression) {
						RoboUtils.writeToDS("Choose \"USB Camera HW\" on the dashboard!");
						s.close();
						continue;
					}
					
					synchronized(this) {
						Console.debug("Waiting for Camera to be ready...");
						if(camera == null)
							wait();
						
						hwClient = compression == hwCompression;
						if(!hwClient) 
							setQuality(100 - compression);
					}
					
					long period = (long)(1000/(1.0 * fps));
					while(true) {
						long t0 = System.currentTimeMillis();
						Data imgData = null;
						synchronized(this) {
							wait();
							imgData = cameraData;
							cameraData = null;
						}
						
						if(imgData == null)
							continue;
						
						/* Set buffer position to start of data,
						 * and then create a new wrapper for the data at
						 * that position								 */
						imgData.data.getBuffer().position(imgData.start);
						byte[] imgArr = new byte[imgData.data.getBuffer().remaining()];
						imgData.data.getBuffer().get(imgArr, 0, imgData.data.getBuffer().remaining());
						
						try { 
							os.write(MAGIC_NUMBERS);
							os.writeInt(imgArr.length);
							os.write(imgArr);
							os.flush();
							long dt = System.currentTimeMillis() - t0;
							
							if(dt < period) {
								Thread.sleep(period - dt);
							}
						} catch(IOException | UnsupportedOperationException e) {
							RoboUtils.exceptionToDS(e);
							break;
						} finally {
							imgData.data.free();
							if(imgData.data.getBuffer() != null) {
								synchronized(this) {
									dataPool.addLast(imgData.data.getBuffer());
								}
							}
						}
					}
				} catch (IOException e) {
					RoboUtils.exceptionToDS(e);
					continue;
				}
			}
		}
	}
	
	private void runCapture() {
		Image frame = NIVision.imaqCreateImage(ImageType.IMAGE_RGB, 0);
		while(true) {
			boolean hwClient;
			ByteBuffer dataBuffer = null;
			
			synchronized(this) {
				hwClient = this.hwClient;
				if(hwClient)
					dataBuffer = dataPool.removeLast();
			}
			
			try {
				if(hwClient && dataBuffer != null) {
					dataBuffer.limit(dataBuffer.capacity() - 1);
					camera.getImageData(dataBuffer);
					setImageData(new RawData(dataBuffer), 0);
				} else {
					camera._frameGrab(frame);
					setImage(frame);
				}
			} catch (VisionException e) {
				RoboUtils.exceptionToDS(e);
				if(dataBuffer != null) {
					synchronized(this) {
						dataPool.addLast(dataBuffer);
						Timer.delay(.1);
					}
				}
			}
		}
	}
	
	public synchronized boolean isCaptureStarted() {
		return captureStarted;
	}
	
	public synchronized void setSize(CameraSize size) {
		if(camera == null) return;
		
		camera.setSize(size);
	}
	
	public synchronized CameraSize getSize() {
		if(camera == null) return CameraSize.MEDIUM;
		
		return camera.getSize();
	}
	
	public synchronized void setQuality(CameraQuality quality) {
		this.quality = quality.kCompression;
		
		if(camera != null)
			camera.setQuality(quality);
	}
	
	private synchronized void setQuality(int quality) {
		this.quality = quality;
	}
	
	public synchronized int getQuality() {
		return quality;
	}
}
