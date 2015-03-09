package org.usfirst.frc.team1554.lib.installer.exceptions;

/**
 * Needs Documentation
 *
 * @author Matthew
 *         Created 3/8/2015 at 10:47 PM
 */
public class RuntimeParsingException extends IORuntimeException {

    public RuntimeParsingException(String desc) {
        super(desc);
    }

    public RuntimeParsingException(String desc, Throwable t) {
        super(desc, t);
    }
}
