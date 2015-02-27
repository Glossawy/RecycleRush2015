package org.usfirst.frc.team1554.lib.net;

public class ServerSocketParams {

    /**
     * Maximum Number of Awaiting Connections (via accept()) allowed.
     * 0 means uses System Default. <br />
     * <br />
     * Default: 4
     */
    public int backlog = 4;

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
     * <br />
     * Default: 0
     */
    public int performaceConnectionTime = 0;
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
     * <br />
     * Default: 1
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
     * <br />
     * Default: 0
     */
    public int performanceBandwidth = 0;

    /**
     * Time, in millis, to await for a connection when accept() is
     * called.<br />
     * <br />
     * Default: 5000
     */
    public int acceptTimeout = 5000;
    /**
     * Receive Buffer Size. <br/>
     * <tt>Default: 4096</tt>
     */
    public int receiveBuffer = 4096;

    /**
     * SO_RESUSEADDR Flag. <br/>
     * <tt>Default: true</tt>
     */
    public boolean reuseAddress = true;
}
