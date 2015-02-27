package org.usfirst.frc.team1554.lib.net;

public class SocketParams {

    public static final int TRAFFIC_LOW_COST = 0b00010;
    public static final int TRAFFIC_RELIABLE = 0b00100;
    public static final int TRAFFIC_THROUGHPUT = 0b01000;
    public static final int TRAFFIC_LOWDELAY = 0b10000;

    /**
     * Connection Timeout in Millis. <br/>
     * <tt>Default: 5000 ms</tt>
     */
    public int connectionTimeout = 5000;

    /**
     * Performance is determined by 3 integer variables. These
     * indicate relative importance of short connection time, low
     * latency and high bandwidth respectively. e.g. <br />
     * <br/>
     * Quick Connection, Don't Care for Latency or Bandwidth: (1, 0,
     * 0) <br />
     * Don't Care for Connection, Low Latency, but Bandwidth more
     * important: (0, 1, 2) <br />
     * <br />
     * and so on.<br />
     * <tt>Default: 0</tt>
     */
    public int performanceConnectionTime = 0;
    /**
     * Performance is determined by 3 integer variables. These
     * indicate relative importance of short connection time, low
     * latency and high bandwidth respectively. e.g. <br />
     * <br/>
     * Quick Connection, Don't Care for Latency or Bandwidth: (1, 0,
     * 0) <br />
     * Don't Care for Connection, Low Latency, but Bandwidth more
     * important: (0, 1, 2) <br />
     * <br />
     * and so on. <br />
     * <tt>Default: 1</tt>
     */
    public int performanceLatency = 1;
    /**
     * Performance is determined by 3 integer variables. These
     * indicate relative importance of short connection time, low
     * latency and high bandwidth respectively. e.g. <br />
     * <br/>
     * Quick Connection, Don't Care for Latency or Bandwidth: (1, 0,
     * 0) <br />
     * Don't Care for Connection, Low Latency, but Bandwidth more
     * important: (0, 1, 2) <br />
     * <br />
     * and so on.<br />
     * <tt>Default: 0</tt>
     */
    public int performanceBandwidth = 0;

    /**
     * Determines the Traffic to go through the channel. Described as
     * follows: <br />
     * <ul>
     * <li>Low Cost (0x02) - Computationally Cheap Connection</li>
     * <li>Reliable (0x04) - Reliable Connection</li>
     * <li>Throughput(0x08) - High Capacity Connection</li>
     * <li>Low Delay (0x10) - Low Delay Connection</li>
     * </ul>
     * <br />
     * These can be combined to some degreeing by Bitwise OR. <br />
     * By default this is 0x14, Reliable and Low Delay
     */
    public int trafficClass = 0x14;

    /**
     * SO_KEEPALIVE Flag. <br/>
     * <tt>Default: true</tt>
     */
    public boolean keepAlive = true;
    /**
     * TCP_NODELAY Flag. <br/>
     * <tt>Default: true</tt>
     */
    public boolean noDelay_TCP = true;
    /**
     * SO_LINGER Flag. <br/>
     * <tt>Default: false</tt>
     */
    public boolean linger = false;
    /**
     * SO_REUSEADDR Flag. <br/>
     * <tt>Default: false</tt>
     */
    public boolean reuseAddress = false;

    /**
     * SO_SNDBUF Flag - Size in Bytes. <br/>
     * <tt>Default: 4096</tt>
     */
    public int sendBuffer = 4096;
    /**
     * SO_RCVBUF Flag - Size in Bytes <br/>
     * <tt>Default: 4096</tt>
     */
    public int receiverBuffer = 4096;

    /**
     * Socket Linger Time in <i><b><u>Seconds!</u></b></i>
     */
    public int lingerDuration = 0;
    /**
     * Enable/Disable SO_TIMEOUT with the given timeout time in
     * millis. a read() operation on a Socket's input stream will only
     * block for this time.
     */
    public int socketTimeout = 0;
}
