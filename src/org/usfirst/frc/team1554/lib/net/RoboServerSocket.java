package org.usfirst.frc.team1554.lib.net;

import org.usfirst.frc.team1554.lib.common.ex.RobotIOException;
import org.usfirst.frc.team1554.lib.util.IOUtils;

import java.io.IOException;
import java.net.InetSocketAddress;

public class RoboServerSocket implements ServerSocket {

    private final Protocol protocol;
    private java.net.ServerSocket server;

    public RoboServerSocket(Protocol protocol, int port, ServerSocketParams params) {
        this.protocol = protocol;

        try {
            this.server = new java.net.ServerSocket();

            if (params != null) {
                this.server.setPerformancePreferences(params.performaceConnectionTime, params.performanceLatency, params.performanceBandwidth);
                this.server.setReuseAddress(params.reuseAddress);
                this.server.setSoTimeout(params.acceptTimeout);
                this.server.setReceiveBufferSize(params.receiveBuffer);
            }

            final InetSocketAddress address = new InetSocketAddress(port);
            if (params != null) {
                this.server.bind(address, params.backlog);
            } else {
                this.server.bind(address);
            }
        } catch (final IOException e) {
            throw new IllegalStateException("Failed to Initialize RoboServerSocket!", e);
        }
    }

    @Override
    public Protocol protocol() {
        return this.protocol;
    }

    @Override
    public Socket accept(SocketParams params) {
        try {
            return new RoboSocket(this.server.accept(), params);
        } catch (final IOException e) {
            throw new RobotIOException("Error Accepting Socket", e);
        }
    }

    @Override
    public void dispose() {
        if (this.server == null) return;

        IOUtils.closeSilently(this.server);
        this.server = null;
    }

}
