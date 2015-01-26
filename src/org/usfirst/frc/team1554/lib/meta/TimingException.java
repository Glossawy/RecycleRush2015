package org.usfirst.frc.team1554.lib.meta;

public class TimingException extends RobotExecutionException {

	public TimingException(){
		super();
	}
	
	public TimingException(String msg) {
		super(msg);
	}
	
	public TimingException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
}
