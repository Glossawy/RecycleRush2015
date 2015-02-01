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
import com.ni.vision.NIVision.CompressionType;
import com.ni.vision.NIVision.FlattenType;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ImageType;
import com.ni.vision.NIVision.RawData;
import com.ni.vision.VisionException;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.image.ImageBase;

// FIXME Rewrite a More Efficient Camera Stream impl. This is currently slightly
// optimized port of CameraServer but is loosely coupled to the Camera API
/**
 * An Alternative to {@link CameraServer} that makes use of the Camera API.
 * 
 * @author Matthew
 *
 */
public enum CameraStream {

	INSTANCE;

	/**
	 * Data Store class for containing Image Data Buffer metadata. <br />
	 * <br />
	 * Typically this is just the NIVision {@link RawData} and buffer start position.
	 * 
	 * @author Matthew
	 *
	 */
	private static class Data {

		RawData data;
		int start;

		Data(RawData data, int s) {
			this.data = data;
			this.start = s;
		}

	}

	public static final int PORT = 1180;

	// WPILib UID
	// They didn't use JPEG's (0xFF, 0xD8, 0xFF) for reasons. 1, 0, 0, 0 is fine!
	public static final byte[] MAGIC_NUMBERS = { 0x01, 0x00, 0x00, 0x00 };
	public static final int hwCompression = -1;
	public static final int MAX_SIZE = 200_000;

	private Thread serverThread;
	private Camera camera = null;
	private Data cameraData = null;
	private int quality = 50;
	private final Deque<ByteBuffer> dataPool = new ArrayDeque<>(3);

	private boolean captureStarted;
	private boolean hwClient = true;

	private CameraStream() {

		// Loop Unwinding, a 3 iteration for loop is unnecessary.
		// Establish Data Buffers available for Image I/O
		//
		// These are DirectByteBuffer's and MUST be freed manually
		// using buffer.free() since they use JNI and Native Resources!
		this.dataPool.addLast(ByteBuffer.allocateDirect(MAX_SIZE));
		this.dataPool.addLast(ByteBuffer.allocateDirect(MAX_SIZE));
		this.dataPool.addLast(ByteBuffer.allocateDirect(MAX_SIZE));

		// Create Serving Thread
		this.serverThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					serveStream();
				} catch (final Exception e) {
					Console.exception(e);
				}
			}
		});
		this.serverThread.setName("CameraStream-Out");
		this.serverThread.start();
	}

	private synchronized void setImageData(RawData data, int start) {

		/*
		 * Set the Camera Data being sent to the new Camera Data.
		 * 
		 * Remember that Data objects hold onto the buffers that originate from our
		 * Data Pool. This means that we may handle MANY Data objects but only use 3
		 * Buffers at a time.
		 * 
		 * The addLast() call at the end just makes the buffer available to the
		 * serving thread.
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
	 * {@link #setImage(ImageBase) setImage} essentially calls this method with the
	 * {@link ImageBase Image's} underlying NIVision Image Object.
	 * 
	 * @param image
	 */
	public void setImage(Image image) {

		// Flatten Image to JPEG and get JPEG Data Buffer
		final RawData data = NIVision.imaqFlatten(image, FlattenType.FLATTEN_IMAGE, CompressionType.COMPRESSION_JPEG, 10 * this.quality);
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
		if ((buffer.limit() - index - 1) <= 2) throw new VisionException("Data size of flattened image is less than 2. Try another camera!");

		setImageData(data, index);
	}

	/**
	 * Set the Camera and Automatically Capture images in a separate thread. This is
	 * useful if you don't wish to use {@link #setImage(ImageBase)} in your loop
	 * (typically for on-roboRIO image processing) or wish to use the DriverStation
	 * for Image Processing.
	 * 
	 * @param camera
	 */
	public void startAutomaticCapture(Camera camera) {
		if (this.captureStarted) return;

		camera.open();
		this.captureStarted = true;
		this.camera = camera;

		this.camera.startCapture();

		final Thread captureThread = new Thread(new Runnable() {
			@Override
			public void run() {
				runCapture();
			}
		});

		captureThread.setName("CameraStream-Capture");
		captureThread.start();
	}

	private void serveStream() throws IOException, InterruptedException {
		try (ServerSocket socket = new ServerSocket()) {
			socket.setReuseAddress(true);
			socket.bind(new InetSocketAddress(PORT));
			while (true) {
				try {
					final Socket s = socket.accept();

					final DataInputStream is = new DataInputStream(s.getInputStream());
					final DataOutputStream os = new DataOutputStream(s.getOutputStream());

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
							// FIXME It May be necessary to put wait() here. Please Test!
							imgData = this.cameraData;
							this.cameraData = null;
						}

						if (imgData == null) {
							continue;
						}

						/*
						 * Set buffer position to start of data, and then create a
						 * new wrapper for the data at that position
						 */
						imgData.data.getBuffer().position(imgData.start);
						final byte[] imgArr = new byte[imgData.data.getBuffer().remaining()];
						imgData.data.getBuffer().get(imgArr, 0, imgData.data.getBuffer().remaining());

						try {
							// Networking UID
							os.write(MAGIC_NUMBERS);
							os.writeInt(imgArr.length);
							os.write(imgArr);
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
					continue;
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
		this.quality = quality.kCompression;

		if (this.camera != null) {
			this.camera.setQuality(quality);
		}
	}

	private synchronized void setQuality(int quality) {
		this.quality = quality;
	}

	public synchronized int getQuality() {
		return this.quality;
	}

}
