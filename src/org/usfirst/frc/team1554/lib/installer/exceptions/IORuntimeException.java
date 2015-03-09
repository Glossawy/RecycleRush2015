package org.usfirst.frc.team1554.lib.installer.exceptions;

/**
 * Needs Documentation
 *
 * @author Matthew
 *         Created 3/8/2015 at 10:38 PM
 */
public class IORuntimeException extends RuntimeException {

    public IORuntimeException(String desc) {
        super(desc);
    }

    public IORuntimeException(String desc, Throwable t) {
        super(desc, t);
    }

}
