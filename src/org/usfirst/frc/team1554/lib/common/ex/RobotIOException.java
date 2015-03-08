package org.usfirst.frc.team1554.lib.common.ex;

/**
 * Needs Documentation
 *
 * @author Matthew
 *         Created 3/7/2015 at 11:57 PM
 */
public class RobotIOException extends RobotExecutionException {

    public RobotIOException() {
        super();
    }

    public RobotIOException(String desc) {
        super(desc);
    }

    public RobotIOException(String desc, Throwable t) {
        super(desc, t);
    }

}
