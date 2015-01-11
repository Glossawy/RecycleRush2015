package org.usfirst.frc.team1554.lib;

import java.util.Map;

import org.usfirst.frc.team1554.lib.collect.IntMap;
import org.usfirst.frc.team1554.lib.collect.Maps;
import org.usfirst.frc.team1554.lib.util.IBuilder;

import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Talon;

public interface MotorScheme {

	IntMap<PWM> pidMap();

	Map<String, PWM> nameMap();

	SpeedController[] getDriveMotors();

	boolean isDualChannelDrive();

	public static class Builder implements IBuilder<MotorScheme> {

		private boolean isDualChannel;
		private SpeedController[] driveMotors;
		private final IntMap<PWM> additionalMotors = Maps.newIntMap(8);
		private final Map<String, PWM> additionalMotorNames = Maps.newHashMap();

		public static final Builder newTwoChannelDrive(SpeedController left, SpeedController right) {
			final Builder builder = new Builder();
			builder.isDualChannel = true;
			builder.driveMotors = new SpeedController[] { left, right };

			return builder;
		}

		public static final Builder newTwoChannelDrive(int leftChannel, int rightChannel) {
			return newTwoChannelDrive(new Talon(leftChannel), new Talon(rightChannel));
		}

		public static final Builder newFourMotorDrive(SpeedController frontLeft, SpeedController rearLeft, SpeedController frontRight, SpeedController rearRight) {
			final Builder builder = new Builder();
			builder.isDualChannel = false;
			builder.driveMotors = new SpeedController[] { frontLeft, rearLeft, frontRight, rearRight };

			return builder;
		}

		public static final Builder newFourMotorDrive(int frontLeftMotor, int rearLeftMotor, int frontRightMotor, int rearRightMotor) {
			return newFourMotorDrive(new Talon(frontLeftMotor), new Talon(rearLeftMotor), new Talon(frontRightMotor), new Talon(rearRightMotor));
		}

		public Builder addMotor(PWM motor, String name) {
			additionalMotors.put(motor.getChannel(), motor);
			additionalMotorNames.put(name, motor);

			return this;
		}

		public Builder addMotor(int channel, String name) {
			return addMotor(new Talon(channel), name);
		}

		@Override
		public MotorScheme build() {
			return new MotorScheme() {

				@Override
				public IntMap<PWM> pidMap() {
					return additionalMotors;
				}

				@Override
				public Map<String, PWM> nameMap() {
					return additionalMotorNames;
				}

				@Override
				public SpeedController[] getDriveMotors() {
					return driveMotors;
				}

				@Override
				public boolean isDualChannelDrive() {
					return isDualChannel;
				}

			};
		}
	}

}
