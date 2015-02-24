package org.usfirst.frc.team1554.lib.util;

import edu.wpi.first.wpilibj.RobotDrive;
import org.usfirst.frc.team1554.lib.Console;
import org.usfirst.frc.team1554.lib.MotorScheme;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;

public final class RoboUtils {

    private static final boolean roborio = Files.exists(Paths.get("home", "lvuser"), LinkOption.NOFOLLOW_LINKS);

    private RoboUtils() {
    }

    /**
     * Previously Built a RobotDrive, now delegates to {@link MotorScheme#getRobotDrive() MotorScheme}.
     *
     * @param scheme
     * @return
     */
    public static final RobotDrive makeRobotDrive(MotorScheme scheme) {
        return scheme.getRobotDrive();
    }

    public static final boolean isUserButtonPressed() {
        return HALRoboUtils.userButton();
    }

    public static final void writeToDS(String message) {
        if (isCodeOnRobot()) {
            HALRoboUtils.toDS(message);
        } else {
            Console.info(message);
        }
    }

    public static boolean isCodeOnRobot() {
        return roborio;
    }

    public static final void clearDS() {
        writeToDS("");
    }

    public static final void exceptionToDS(Throwable t) {
        clearDS();
        final StackTraceElement[] stackTrace = t.getStackTrace();
        final StringBuilder message = new StringBuilder();
        final String separator = "===\n";
        final Throwable cause = t.getCause();

        message.append("Exception of type ").append(t.getClass().getName()).append('\n');
        message.append("Message: ").append(t.getMessage()).append('\n');
        message.append(separator);
        message.append("   ").append(stackTrace[0]).append('\n');

        for (int i = 1; i < stackTrace.length; i++) {
            message.append(" \t").append(stackTrace[i]).append('\n');
        }

        if (cause != null) {
            final StackTraceElement[] causeTrace = cause.getStackTrace();
            message.append(" \t\t").append("Caused by ").append(cause.getClass().getName()).append('\n');
            message.append(" \t\t").append("Because: ").append(cause.getMessage()).append('\n');
            message.append(" \t\t   ").append(causeTrace[0]).append('\n');
            message.append(" \t\t \t").append(causeTrace[2]).append('\n');
            message.append(" \t\t \t").append(causeTrace[3]);
        }

        writeToDS(message.toString());
    }

}
