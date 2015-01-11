package org.usfirst.frc.team1554.lib;

import org.usfirst.frc.team1554.lib.util.IBuilder;

import edu.wpi.first.wpilibj.Joystick;

// Should we do this?
public abstract class DriveControl {

	private final JoystickControl controlScheme;
	private final MotorScheme motorScheme;

	private DriveControl(JoystickControl controlScheme, MotorScheme motorScheme) {
		this.controlScheme = controlScheme;
		this.motorScheme = motorScheme;
	}

	public JoystickControl getJoystickControl() {
		return this.controlScheme;
	}

	public MotorScheme getMotorScheme() {
		return this.motorScheme;
	}

	public static class Builder implements IBuilder<DriveControl> {

		private JoystickControl control;
		private DriveTrain type;
		private MotorScheme scheme;

		public static DriveControl.Builder create(JoystickControl controls, DriveTrain type, MotorScheme scheme) {
			final Builder builder = new Builder();
			builder.control = controls;
			builder.type = type;
			builder.scheme = scheme;

			return builder;
		}

		public static DriveControl.Builder create(Joystick stick, DriveTrain type, MotorScheme scheme) {
			return create(new SingleJoystickControl(stick), type, scheme);
		}

		public static DriveControl.Builder create(Joystick left, Joystick right, DriveTrain type, MotorScheme scheme) {
			return create(new DualJoystickControl(left, right), type, scheme);
		}

		@Override
		public DriveControl build() {
			if (this.type == DriveTrain.MECANUM)
				return new MecanumDriveControl(this.control, this.scheme);
			else
				return new TankDriveControl(this.control, this.scheme);
		}

	}

	private static class MecanumDriveControl extends DriveControl {

		private MecanumDriveControl(JoystickControl control, MotorScheme scheme) {
			super(control, scheme);
		}

	}

	private static class TankDriveControl extends DriveControl {

		private TankDriveControl(JoystickControl control, MotorScheme scheme) {
			super(control, scheme);
		}

	}

}
