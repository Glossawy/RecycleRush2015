package org.usfirst.frc.team1554.lib.net;

import org.usfirst.frc.team1554.lib.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class RoboSocket implements Socket {

    private java.net.Socket socket;

    public RoboSocket(String host, int port, SocketParams params) {
        try {
            this.socket = new java.net.Socket();
            apply(params);

            final InetSocketAddress address = new InetSocketAddress(host, port);
            if (params != null) {
                this.socket.connect(address, params.connectionTimeout);
            } else {
                this.socket.connect(address);
            }
        } catch (final Exception e) {
            throw new IllegalStateException("Failed to Create Robo Socket!", e);
        }
    }

    public RoboSocket(java.net.Socket socket, SocketParams params) {
        this.socket = socket;
        apply(params);
    }

    private void apply(SocketParams params) {
        if (params == null) return;

        try {
            this.socket.setPerformancePreferences(params.performanceConnectionTime, params.performanceLatency, params.performanceBandwidth);
            this.socket.setTrafficClass(params.trafficClass);
            this.socket.setTcpNoDelay(params.noDelay_TCP);
            this.socket.setKeepAlive(params.keepAlive);
            this.socket.setReuseAddress(params.reuseAddress);
            this.socket.setSendBufferSize(params.sendBuffer);
            this.socket.setReceiveBufferSize(params.receiverBuffer);
            this.socket.setSoLinger(params.linger, params.lingerDuration);
            this.socket.setSoTimeout(params.socketTimeout);
        } catch (final Exception e) {
            throw new IllegalStateException("Failed to Set Socket Parameters!", e);
        }
    }

    @Override
    public InputStream input() {
        try {
            return this.socket.getInputStream();
        } catch (final IOException e) {
            throw new RuntimeException("Error Getting Socket InputStream", e);
        }
    }

    @Override
    public OutputStream output() {
        try {
            return this.socket.getOutputStream();
        } catch (final IOException e) {
            throw new RuntimeException("Error Getting Socket OutputStream", e);
        }
    }

    @Override
    public boolean isConnected() {
        if (this.socket == null) return false;

        return this.socket.isConnected();
    }

    @Override
    public String getRemoteAddress() {
        return this.socket.getRemoteSocketAddress().toString();
    }

    @Override
    public String getInetAddress() {
        return this.socket.getInetAddress().toString();
    }

    @Override
    public int getPort() {
        return this.socket.getPort();
    }

    @Override
    public void dispose() {
        if (this.socket != null) {
            IOUtils.closeSilently(this.socket);
            this.socket = null;
        }
    }

}
