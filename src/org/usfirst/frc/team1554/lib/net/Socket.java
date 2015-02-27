package org.usfirst.frc.team1554.lib.net;

import org.usfirst.frc.team1554.lib.common.Disposable;

import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;

public interface Socket extends Disposable, Closeable {

    InputStream input();

    OutputStream output();

    boolean isConnected();

    String getRemoteAddress();

    String getInetAddress();

    int getPort();

    @Override
    void dispose();

    @Override
    default void close() {
        dispose();
    }
}
