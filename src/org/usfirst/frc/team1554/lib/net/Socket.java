package org.usfirst.frc.team1554.lib.net;

import java.io.InputStream;
import java.io.OutputStream;

import org.usfirst.frc.team1554.lib.Disposable;

public interface Socket extends Disposable {

	InputStream input();

	OutputStream output();

	boolean isConnected();

	String getRemoteAddress();

	@Override
	void dispose();
}
