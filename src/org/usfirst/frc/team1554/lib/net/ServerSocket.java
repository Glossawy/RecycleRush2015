package org.usfirst.frc.team1554.lib.net;

import java.io.Closeable;

import org.usfirst.frc.team1554.lib.Disposable;

public interface ServerSocket extends Disposable, Closeable {

	public enum Protocol {
		TCP;
	}

	Protocol protocol();

	Socket accept(SocketParams params);

	@Override
	void dispose();

	@Override
	default void close() {
		dispose();
	}
}
