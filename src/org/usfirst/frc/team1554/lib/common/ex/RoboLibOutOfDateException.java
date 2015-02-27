package org.usfirst.frc.team1554.lib.common.ex;

import org.usfirst.frc.team1554.lib.meta.LibVersion;

public class RoboLibOutOfDateException extends RobotExecutionException {

    private static final long serialVersionUID = -8350304045145582352L;

    public RoboLibOutOfDateException() {
        super("Running Version " + LibVersion.VERSION + " of " + LibVersion.NAME);
    }

    public RoboLibOutOfDateException(String msg) {
        super(LibVersion.NAME + " " + LibVersion.VERSION + " - " + msg);
    }

    public RoboLibOutOfDateException(String msg, Throwable cause) {
        super(LibVersion.NAME + " " + LibVersion.VERSION + " - " + msg, cause);
    }
}
