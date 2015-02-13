package org.usfirst.frc.team1554.lib.meta;

import org.usfirst.frc.team1554.lib.LibVersion;

public class OutOfDateException extends RobotExecutionException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8350304045145582352L;

	public OutOfDateException() {
		super("Running Version " + LibVersion.VERSION + " of " + LibVersion.NAME);
	}

	public OutOfDateException(String msg) {
		super(LibVersion.NAME + " " + LibVersion.VERSION + " - " + msg);
	}

	public OutOfDateException(String msg, Throwable cause) {
		super(LibVersion.NAME + " " + LibVersion.VERSION + " - " + msg, cause);
	}
}
