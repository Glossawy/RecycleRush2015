package org.usfirst.frc.team1554.lib.net;

import org.usfirst.frc.team1554.lib.Disposable;

public interface ServerSocket extends Disposable {

	public enum Protocol {
		TCP;
	}

	Protocol protocol();

	Socket accept(SocketParams params);

	@Override
	void dispose();

}
