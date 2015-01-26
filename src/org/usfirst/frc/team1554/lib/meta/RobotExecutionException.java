package org.usfirst.frc.team1554.lib.meta;

public class RobotExecutionException extends RuntimeException {

	private static final long serialVersionUID = -8118879964921602813L;

	RobotExecutionException() {
		super();
	}

	public RobotExecutionException(String message) {
		super(message);
	}

	public RobotExecutionException(String message, Throwable cause) {
		super(message, cause);
	}

}
