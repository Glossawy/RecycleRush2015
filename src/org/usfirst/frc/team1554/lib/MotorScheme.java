package org.usfirst.frc.team1554.lib;

import static edu.wpi.first.wpilibj.RobotDrive.MotorType.kFrontLeft;
import static edu.wpi.first.wpilibj.RobotDrive.MotorType.kFrontRight;
import static edu.wpi.first.wpilibj.RobotDrive.MotorType.kRearLeft;
import static edu.wpi.first.wpilibj.RobotDrive.MotorType.kRearRight;

import java.util.Map;

import org.usfirst.frc.team1554.lib.collect.IntMap;
import org.usfirst.frc.team1554.lib.collect.Maps;
import org.usfirst.frc.team1554.lib.collect.IntMap.Values;
import org.usfirst.frc.team1554.lib.util.IBuilder;

import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SensorBase;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Talon;

// TODO is this a case of trying to do too much in one interface?
/**
 * A Code representation of what is basically a Motor Schematic. This class is mostly
 * used for managing Drive Motors though there is the optional functionality of
 * centralizing all motors and abstracting them to just Channel IDs. This abstraction
 * will force them to their bare {@link SpeedController}, and tangentially their
 * {@link PWM}, representations. <br />
 * All Motors in a MotorScheme must be {@link PWM PWMs} and {@link SpeedController
 * SpeedControllers} to behave as expected. <br />
 * <br />
 * The Only Exception to the Name/ID System are {@link MotorGroup MotorGroups} which
 * are used for motor synchronization. Each of the individual motors are registered
 * by Channel ID but not by Name; The MotorGroup is registered by Name but not by
 * Channel ID. i.e. You can get the individual motors by their Channel ID while you
 * can get the entire MotorGroup by Name but not the other way around.<br />
 * <br />
 * That exception was included out of a desire to have Synchronized SpeedController
 * Groups be a part of a MotorScheme... but MotorGroups have no way of having a
 * Channel ID to act as an ID. This was an odd solution to that problem.
 * 
 * @author Matthew
 * @see Builder
 * @see SynchronizedMotor
 */
public interface MotorScheme extends Disposable{

	/**
	 * Simple Enumeration to define RobotDrive type and possibly switching between
	 * them. <br />
	 * <br />
	 * Takes the {@link RobotDrive} and {@link JoystickControl} associated with a
	 * Robot and updates the RobotDrive motors appropriately for a drive type. e.g.
	 * Using Polar Methods for {@value #MECANUM_POLAR} and using Left-Right Sided
	 * Stick Driving for {@value #TANK}.
	 * 
	 * @author Matthew
	 */
	public enum DriveManager {

		// TODO Sensors/Analog
		/**
		 * Drive the Robot by Mecanum Cartesian controls. Using X, Y, Theta and Gyro
		 * Theta. <br />
		 * <br />
		 * If a Gyro is available then the robot is driven relative to the Field, not
		 * the Robot.
		 */
		MECANUM_CARTESIAN {
			@Override
			void updateDrive(RobotDrive drive, JoystickControl c, BasicSense sense) {
				drive.mecanumDrive_Cartesian(c.getX(), c.getY(), c.getTwist(), sense.getAngle());
			}
		},
		/**
		 * Drive the Robot by Mecanum Polar controls. Using Magnitude, Direction and
		 * Theta. <br />
		 * This is always relative to the driver.
		 */
		MECANUM_POLAR {
			@Override
			void updateDrive(RobotDrive drive, JoystickControl c, BasicSense sense) {
				drive.mecanumDrive_Polar(c.getMagnitude(), c.getDirectionDegrees(), c.getTwist());
			}
		},
		// TODO JoystickControl Hints
		/**
		 * Drive the Robot with One Stick only. Uses Up Down for movement and Left
		 * Right for rotation.
		 */
		ARCADE {
			@Override
			void updateDrive(RobotDrive drive, JoystickControl c, BasicSense s) {
				drive.arcadeDrive(c.rightJoystick(), c.dampenOutputs());
			}
		},
		/**
		 * Drive the Robot with Two Sticks. One controlling the left side of the
		 * robot, the other controlling the right side.
		 */
		TANK {
			@Override
			void updateDrive(RobotDrive drive, JoystickControl c, BasicSense sense) {
				drive.tankDrive(c.leftJoystick(), c.rightJoystick(), c.dampenOutputs());
			}
		};

		/**
		 * Update RobotDrive motor speeds according to the provided Joystick
		 * measurements based on selected DriveManager.
		 * 
		 * @param drive
		 * @param c
		 */
		abstract void updateDrive(RobotDrive drive, JoystickControl c, BasicSense sense);
	}

	/**
	 * Get Motors delegated solely to managing the Drive Train.
	 * 
	 * @return
	 */
	SpeedController[] getDriveMotors();

	/**
	 * Check if this MotorScheme uses a Two-Channel (Two-Motor) Drive Train
	 * configuration.
	 * 
	 * @return
	 */
	boolean isDualChannelDrive();

	/**
	 * Return the mapping of each registered motor to it's channel.
	 * 
	 * @return
	 */
	IntMap<SpeedController> pidMap();

	/**
	 * Return the mapping of each registered motor it to it's registered name. This
	 * is not reliable since in certain scenarios a name may not be provided at all!
	 * 
	 * @return
	 */
	Map<String, SpeedController> nameMap();

	void updateDrive(RobotDrive drive, JoystickControl control, BasicSense sense);

	void setDriveManagement(DriveManager manager);

	DriveManager getDriveManagement();

	RobotDrive getRobotDrive();

	/**
	 * Get a Motor by Name. Equivalent to nameMap().get(name);
	 * 
	 * @param name
	 * @return
	 */
	default SpeedController getMotor(String name) {
		return nameMap().get(name);
	}

	/**
	 * Get a Motor by Channel. Equivalent to pidMap().get(channel);
	 * 
	 * @param channel
	 * @return
	 */
	default SpeedController getMotor(int channel) {
		return pidMap().get(channel, null);
	}

	/**
	 * Set the Raw Value of this motor in the range [0, 255]. Any input will be
	 * bounded to this range. e.g. 330000 will be bounded to 255 and -231 will be
	 * bounded to 0. <br />
	 * 
	 * @param pwm
	 * @param value
	 */
	default void setRaw(int pwm, int value) {
		final SpeedController motor = pidMap().get(pwm, null);

		if (motor == null) throw new NullPointerException("No PWM Found at id of " + pwm);
		if (!(motor instanceof PWM)) throw new IllegalArgumentException("Motor of ID " + pwm + " is NOT a PWM!");

		((PWM) motor).setRaw(Math.max(0, Math.min(255, value)));
	}

	/**
	 * Set the Speed Value of this motor in the range [-1.0, 1.0]. Any input will be
	 * bounded to this range. e.g. -3123 will be bounded to -1.0 and 323 will be
	 * bounded to 1.0.
	 * 
	 * @param pwm
	 * @param speed
	 */
	default void setSpeed(int pwm, double speed) {
		final SpeedController motor = pidMap().get(pwm, null);

		if (motor == null) throw new NullPointerException("No PWM Found at id of " + pwm);
		motor.set(speed);
	}

	/**
	 * Set the Speed Value of this motor in the range [-1.0, 1.0]. Any input will be
	 * bounded to this range. e.g. -3123 will be bounded to -1.0 and 323 will be
	 * bounded to 1.0.
	 * 
	 * @param name
	 * @param speed
	 */
	default void setSpeed(String name, double speed) {
		final SpeedController motor = nameMap().get(name);

		if (motor == null) throw new NullPointerException("No PWM Found at name of " + name);
		motor.set(speed);
	}

	/**
	 * Get the Raw Value of this PWM. You must be certain this object is a subclass
	 * of PWM!
	 * 
	 * @param pwm
	 * @return
	 */
	default int getRaw(int pwm) {
		final SpeedController motor = pidMap().get(pwm, null);

		if (motor == null) throw new NullPointerException("No PWM Found at id of " + pwm);
		if (!(motor instanceof PWM)) throw new IllegalArgumentException("Motor of ID " + pwm + " is NOT a PWM!");

		return ((PWM) motor).getRaw();
	}

	/**
	 * Get the Speed Value of this SpeedController.
	 * 
	 * @param pwm
	 * @return
	 */
	default double getSpeed(int pwm) {
		final SpeedController motor = pidMap().get(pwm, null);

		if (motor == null) throw new NullPointerException("No PWM Found at id of " + pwm);
		return motor.get();
	}

	/**
	 * Get the Speed Value of this SpeedController.
	 * 
	 * @param name
	 * @return
	 */
	default double getSpeed(String name) {
		final SpeedController motor = nameMap().get(name);

		if (motor == null) throw new NullPointerException("No PWM Found at name of " + name);
		return motor.get();
	}
	
	@Override
	default void dispose() {
		final Values<SpeedController> vals = pidMap().values();

		for (SpeedController sc = vals.next(); vals.hasNext; sc = vals.next())
			if (sc instanceof PWM) {
				((PWM) sc).free();
			} else if (sc instanceof SensorBase) {
				((SensorBase) sc).free();
			}
	}

	/**
	 * Use to build instances of MotorScheme. Allows for easy creation of Two Motor
	 * and Four Motor Drives while also allowing you to add arbitrary motors. These
	 * arbitrary motors are mapped to their channels, to retrieve a motor just
	 * provide either a Name or a Channel.
	 * 
	 * @author Matthew
	 */
	public static class Builder implements IBuilder<MotorScheme> { 

		private static RobotDriveFactory<RobotDrive> driveFactory = RobotDriveFactory.DEFAULT;

		private boolean isDualChannel;
		private SpeedController[] driveMotors;
		private final boolean[] inverted = { false, false, false, false };
		private DriveManager dManager = DriveManager.TANK;
		private final IntMap<SpeedController> additionalMotors = Maps.newIntMap(8);
		private final Map<String, SpeedController> additionalMotorNames = Maps.newHashMap();

		/**
		 * Create a MotorScheme based on a Two Channel Drive System. This defaults to
		 * a Tank Drive {@link DriveManager} and uses the two {@link SpeedController
		 * SpeedControllers} given for control.
		 * 
		 * @param left
		 *            - Left Channel Motor
		 * @param right
		 *            - Right Channel Motor
		 * @return
		 */
		public static final Builder newTwoChannelDrive(SpeedController left, SpeedController right) {
			final Builder builder = new Builder();
			builder.isDualChannel = true;
			builder.driveMotors = new SpeedController[] { left, right };

			for (final SpeedController sc : builder.driveMotors) {
				builder.additionalMotors.put(((PWM) sc).getChannel(), sc);
			}

			return builder;
		}

		/**
		 * Create a MotorScheme based on a Two Channel Drive System. This defaults to
		 * a Tank Drive {@link DriveManager} and uses the two {@link SpeedController
		 * SpeedControllers} given for control. <br />
		 * <br />
		 * This method creates two {@link Talon Talons} from the two given Channel
		 * IDs and delegates to
		 * {@link #newTwoChannelDrive(SpeedController, SpeedController)
		 * newTwoChannelDrive}
		 * 
		 * @param leftChannel
		 *            - Left Channel ID
		 * @param rightChannel
		 *            - Right Channel ID
		 * @return
		 */
		public static final Builder newTwoChannelDrive(int leftChannel, int rightChannel) {
			return newTwoChannelDrive(new Talon(leftChannel), new Talon(rightChannel));
		}

		/**
		 * Create a MotorScheme based on a Four Motor Drive System. This is the
		 * maximum accounted for in the {@link RobotDrive} class. This defaults to a
		 * Tank Drive {@link DriveManager} and uses the four {@link SpeedController
		 * SpeedControllers} provided for control.
		 * 
		 * @param frontLeft
		 *            - Front Left Wheel Motor
		 * @param rearLeft
		 *            - Rear Left Wheel Motor
		 * @param frontRight
		 *            - Front Right Wheel Motor
		 * @param rearRight
		 *            - Rear Right Wheel Motor
		 * @return
		 */
		public static final Builder newFourMotorDrive(SpeedController frontLeft, SpeedController rearLeft, SpeedController frontRight, SpeedController rearRight) {
			final Builder builder = new Builder();
			builder.isDualChannel = false;
			builder.driveMotors = new SpeedController[] { frontLeft, rearLeft, frontRight, rearRight };

			for (final SpeedController sc : builder.driveMotors) {
				builder.additionalMotors.put(((PWM) sc).getChannel(), sc);
			}

			return builder;
		}

		/**
		 * Create a MotorScheme based on a Four Motor Drive System. This is the
		 * maximum accounted for in the {@link RobotDrive} class. This defaults to a
		 * Tank Drive {@link DriveManager} and uses the four {@link SpeedController
		 * SpeedControllers} provided for control. <br />
		 * <br />
		 * This method creates four {@link Talon Talons} from the four given Channel
		 * IDs and delegates to
		 * {@link #newFourMotorDrive(SpeedController, SpeedController, SpeedController, SpeedController)
		 * newFourMotorDrive}.
		 * 
		 * @param frontLeftMotor
		 *            - Front Left Wheel Channel
		 * @param rearLeftMotor
		 *            - Rear Left Wheel Channel
		 * @param frontRightMotor
		 *            - Front Right Wheel Channel
		 * @param rearRightMotor
		 *            - Rear Right Wheel Channel
		 * @return
		 */
		public static final Builder newFourMotorDrive(int frontLeftMotor, int rearLeftMotor, int frontRightMotor, int rearRightMotor) {
			return newFourMotorDrive(new Talon(frontLeftMotor), new Talon(rearLeftMotor), new Talon(frontRightMotor), new Talon(rearRightMotor));
		}

		/**
		 * Set the {@link DriveManager} used by this MotorScheme. Default:
		 * {@value DriveManager#TANK Tank Drive}.
		 * 
		 * @param manager
		 * @return
		 */
		public Builder setDriveManager(DriveManager manager) {
			this.dManager = manager;

			return this;
		}

		/**
		 * Add a Valid Motor (Extends {@link PWM} and implements
		 * {@link SpeedController}) to the Motor Scheme.
		 * 
		 * @param motor
		 * @param name
		 * @return
		 */
		public <T extends PWM & SpeedController> Builder addMotor(T motor, String name) {
			additionalMotors.put(motor.getChannel(), motor);
			additionalMotorNames.put(name, motor);

			return this;
		}

		/**
		 * Add a Valid Motor (Extends {@link PWM} and implements
		 * {@link SpeedController}) to the Motor Scheme. <br />
		 * <br />
		 * Creates a {@link Talon} from the given Channel ID.
		 * 
		 * @param motor
		 * @param name
		 * @return
		 */
		public Builder addMotor(int channel, String name) {
			return addMotor(new Talon(channel), name);
		}

		/**
		 * Registers the entire {@link MotorGroup} by name and each of it's
		 * individual motors by channel.
		 * 
		 * @param group
		 * @param groupName
		 * @return
		 */
		public <T extends PWM & SpeedController> Builder addMotorGroup(MotorGroup<T> group, String groupName) {
			for (final T sc : group.getMotors()) {
				additionalMotors.put(sc.getChannel(), new SynchronizedMotor<T>(sc, group));
			}

			additionalMotorNames.put(groupName, group);
			return this;
		}

		public Builder setInverted(boolean frontLeft, boolean rearLeft, boolean frontRight, boolean rearRight) {
			inverted[0] = frontLeft;
			inverted[1] = rearLeft;
			inverted[2] = frontRight;
			inverted[3] = rearRight;

			return this;
		}

		public Builder setInverted(boolean leftMotors, boolean rightMotors) {
			return setInverted(leftMotors, leftMotors, rightMotors, rightMotors);
		}

		public static <T extends RobotDrive> void setDriveFactory(RobotDriveFactory<RobotDrive> factory) {
			Builder.driveFactory = factory;
		}

		/**
		 * Create the MotorScheme from this Builder's options.
		 */
		@Override
		public MotorScheme build() {
			final SpeedController[] m = driveMotors;
			final RobotDrive drive;
			if (isDualChannel) {
				drive = Builder.driveFactory.createForTwoChannels(m[0], m[1]);
			} else {
				drive = Builder.driveFactory.createForFourChannels(m[0], m[1], m[2], m[3]);
			}

			drive.setInvertedMotor(kFrontLeft, inverted[0]);
			drive.setInvertedMotor(kRearLeft, inverted[1]);
			drive.setInvertedMotor(kFrontRight, inverted[2]);
			drive.setInvertedMotor(kRearRight, inverted[3]);

			return new MotorScheme() { 

				@Override
				public IntMap<SpeedController> pidMap() {
					return additionalMotors;
				}

				@Override
				public Map<String, SpeedController> nameMap() {
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

				@Override
				public void updateDrive(RobotDrive drive, JoystickControl control, BasicSense sense) {
					dManager.updateDrive(drive, control, sense);
				}

				@Override
				public void setDriveManagement(DriveManager manager) {
					dManager = manager;
				}

				@Override
				public DriveManager getDriveManagement() {
					return dManager;
				}

				@Override
				public RobotDrive getRobotDrive() {
					return drive;
				}
			};
		}
	}

	/**
	 * A Simple Wrapper Class for Motors that exist in MotorGroups but follow the odd
	 * exception to {@link MotorScheme} rules for {@link MotorGroup MotorGroups}.
	 * This ensures that any attempt to change the individual motor will be delegated
	 * to it's respective MotorGroup, providing continued synchronization. <br />
	 * <br />
	 * In the special case it is necessary, this type cna be determined and the Motor
	 * retrieved as either a {@link PWM} or a {@link SpeedController}.
	 * 
	 * @author Matthew
	 * @param <T>
	 */
	public static class SynchronizedMotor<T extends PWM & SpeedController> implements SpeedController {

		public final MotorGroup<T> group;
		public final T pwm;

		public SynchronizedMotor(T motor, MotorGroup<T> group) {
			this.group = group;
			this.pwm = motor;
		}

		// Delegate all proper SpeedController methods to the MotorGroup

		@Override
		public void pidWrite(double output) {
			group.pidWrite(output);
		}

		@Override
		public double get() {
			return group.get();
		}

		@Override
		public void set(double speed, byte syncGroup) {
			group.set(speed, syncGroup);
		}

		@Override
		public void set(double speed) {
			group.set(speed);
		}

		@Override
		public void disable() {
			group.disable();
		}

	}

}
