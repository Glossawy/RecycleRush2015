package org.usfirst.frc.team1554.lib.util;

import org.usfirst.frc.team1554.lib.MotorScheme;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SpeedController;

public final class RoboUtils {

	private RoboUtils() {
	}

	public static final RobotDrive makeRobotDrive(MotorScheme scheme) {
		final SpeedController[] motors = scheme.getDriveMotors();
		if (scheme.isDualChannelDrive())
			return new RobotDrive(motors[0], motors[1]);
		else
			return new RobotDrive(motors[0], motors[1], motors[2], motors[3]);
	}

}
