package org.usfirst.frc.team1554.lib.installer.exceptions;

/**
 * Needs Documentation
 *
 * @author Matthew
 *         Created 3/8/2015 at 10:40 PM
 */
public class MissingRequirementException extends IllegalStateException {

    public MissingRequirementException(String desc) {
        super(desc);
    }

    public MissingRequirementException(String desc, Throwable t) {
        super(desc, t);
    }
}
