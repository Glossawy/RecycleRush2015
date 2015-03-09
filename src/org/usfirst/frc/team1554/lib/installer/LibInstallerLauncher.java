package org.usfirst.frc.team1554.lib.installer;

/**
 * Entry Point for Library Installation
 *
 * @author Matthew
 *         Created 3/8/2015 at 5:18 PM
 */
class LibInstallerLauncher {

    public static void main(String[] args) throws IllegalAccessException {
        try {
            if (Class.forName("javafx.application.Application") == null)
                throw null; // NPE Catch-all
        } catch (Exception e) {
            throw new IllegalStateException("You cannot execute LibInstaller in an embedded environment!");
        }

        LibInstaller.startInstallation(args);
    }

}
