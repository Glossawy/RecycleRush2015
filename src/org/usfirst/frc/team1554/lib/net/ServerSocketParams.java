package org.usfirst.frc.team1554.lib.net;

public class ServerSocketParams {

	/**
	 * Maximum Number of Awaiting Connections (via accept()) allowed. 0 means uses
	 * System Default.
	 */
	public int backlog = 4;

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
	public int performaceConnectionTime = 0;
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
	 * Time, in millis, to await for a connection when accept() is called.
	 */
	public int acceptTimeout = 5000;
	/** Receive Buffer Size */
	public int receiveBuffer = 4096;

	/** SO_RESUSEADDR Flag */
	public boolean reuseAddress = true;
}
