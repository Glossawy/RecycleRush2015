package org.usfirst.frc.team1554.lib.installer;

/**
 * Needs Documentation
 *
 * @author Matthew
 *         Created 3/8/2015 at 4:43 PM
 */
final class GUIRef {

    static final String TITLE = "RoboLib Installer";
    static final String RES_PACKAGE = "org/usfirst/frc/team1554/lib/installer/res/";
    static final String LIB_INST_CLASS = "org.usfirst.frc.team1554.lib.installer.LibInstaller";

    static final String getCaller() {
        return Thread.currentThread().getStackTrace()[4].getClassName();
    }

    static final Class<?> getCallerClass() throws ClassNotFoundException {
        return Class.forName(Thread.currentThread().getStackTrace()[4].getClassName());
    }

}
