package org.usfirst.frc.team1554.lib.net;

public class SocketParams {

	/** Connection Timeout in Millis. */
	public int connectionTimeout = 5000;

	/**
	 * Preformance is determined by 3 integer variables. These indicate relative
	 * importance of short connection time, low latency and high bandwidth
	 * respectively. e.g.
	 * 
	 * Quick Connection, Don't Care for Latency or Bandwidth: (1, 0, 0) Don't Care
	 * for Connection, Low Latency, but Bandwidth more important: (0, 1, 2)
	 * 
	 * and so on.
	 */
	public int performanceConnectionTime = 0;
	/**
	 * Preformance is determined by 3 integer variables. These indicate relative
	 * importance of short connection time, low latency and high bandwidth
	 * respectively. e.g.
	 * 
	 * Quick Connection, Don't Care for Latency or Bandwidth: (1, 0, 0) Don't Care
	 * for Connection, Low Latency, but Bandwidth more important: (0, 1, 2)
	 * 
	 * and so on.
	 */
	public int performanceLatency = 1;
	/**
	 * Preformance is determined by 3 integer variables. These indicate relative
	 * importance of short connection time, low latency and high bandwidth
	 * respectively. e.g.
	 * 
	 * Quick Connection, Don't Care for Latency or Bandwidth: (1, 0, 0) Don't Care
	 * for Connection, Low Latency, but Bandwidth more important: (0, 1, 2)
	 * 
	 * and so on.
	 */
	public int performanceBandwidth = 0;

	/**
	 * Determines the Traffic to go through the channel. Described as follows: <br />
	 * <ul>
	 * <li>Low Cost (0x02) - Computationally Cheap Connection</li>
	 * <li>Reliable (0x04) - Reliable Connection</li>
	 * <li>Throughput(0x08) - High Capacity Connection</li>
	 * <li>Low Delay (0x10) - Low Delay Connection</li>
	 * </ul>
	 * <br />
	 * These can be combined to some degreeing by Bitwise OR. e.g.: <br />
	 * 0x02 | 0x08 = 0x10 or <br />
	 * 0x02 | 0x08 | 0x04 = 0x14 <br />
	 * By default this is 0x14, Reliable and Low Delay
	 */
	public int trafficClass = 0x14;

	/** SO_KEEPALIVE Flag */
	public boolean keepAlive = true;
	/** TCP_NODELAY Flag */
	public boolean noDelay_TCP = true;
	/** SO_LINGER Flag */
	public boolean linger = false;
	public boolean reuseAddress = false;

	/** SO_SNDBUF Flag - Size in Bytes */
	public int sendBuffer = 4096;
	/** SO_RCVBUF Flag - Size in Bytes */
	public int receiverBuffer = 4096;

	/** Socket Linger Time in <i><b><u>Seconds!</u></b></i> */
	public int lingerDuration = 0;
	/**
	 * Enable/Disable SO_TIMEOUT with the given timeout time in millis. a read()
	 * operation on a Socket's input stream will only block for this time.
	 */
	public int socketTimeout = 0;
}
