package org.usfirst.frc.team1554;

import static org.usfirst.frc.team1554.Ref.Buttons.ID_DISABLE_TWIST;
import static org.usfirst.frc.team1554.Ref.Buttons.ID_SWAP_JOYSTICKS;
import static org.usfirst.frc.team1554.Ref.Buttons.ID_TURBO_DRIVE;
import static org.usfirst.frc.team1554.Ref.Channels.FL_DMOTOR;
import static org.usfirst.frc.team1554.Ref.Channels.FR_DMOTOR;
import static org.usfirst.frc.team1554.Ref.Channels.RL_DMOTOR;
import static org.usfirst.frc.team1554.Ref.Channels.RR_DMOTOR;
import static org.usfirst.frc.team1554.Ref.Ports.JOYSTICK_LEFT;
import static org.usfirst.frc.team1554.Ref.Ports.JOYSTICK_RIGHT;
import static org.usfirst.frc.team1554.Ref.Values.DRIVE_SCALE_FACTOR;
import static org.usfirst.frc.team1554.Ref.Values.MAG_STICK_DEADBAND;
import static org.usfirst.frc.team1554.Ref.Values.TWIST_STICK_DEADBAND;

import org.usfirst.frc.team1554.lib.DualJoystickControl;
import org.usfirst.frc.team1554.lib.EnhancedIterativeRobot;
import org.usfirst.frc.team1554.lib.JoystickControl;
import org.usfirst.frc.team1554.lib.JoystickControl.Hand;
import org.usfirst.frc.team1554.lib.MotorScheme;
import org.usfirst.frc.team1554.lib.MotorScheme.DriveManager;
import org.usfirst.frc.team1554.lib.io.Console;

import edu.wpi.first.wpilibj.RobotDrive;

/**
 * The VM is configured to automatically run this class, and to call the functions
 * corresponding to each mode, as described in the IterativeRobot documentation. If
 * you change the name of this class or the package after creating this project, you
 * must also update the manifest file in the resource directory.
 */
public class Robot extends EnhancedIterativeRobot {

	private final DualJoystickControl control;
	private final MotorScheme motors;

	public Robot() {
		super();

		Console.debug("Creating and Initializing Controls/Motor Scheme...");
		this.control = new DualJoystickControl(JOYSTICK_LEFT, JOYSTICK_RIGHT);
		this.control.setMagnitudeThreshold(MAG_STICK_DEADBAND);
		this.control.setTwistThreshold(TWIST_STICK_DEADBAND);
		this.motors = MotorScheme.Builder.newFourMotorDrive(FL_DMOTOR, RL_DMOTOR, FR_DMOTOR, RR_DMOTOR).setInverted(false, true).setDriveManager(DriveManager.MECANUM_POLAR).build();
		this.motors.getRobotDrive().setMaxOutput(DRIVE_SCALE_FACTOR);

		Console.debug("Initializing Button Actions...");
		this.control.putButtonAction(ID_TURBO_DRIVE, () -> getDrive().setLeftRightMotorOutputs(1.0, -1.0), Hand.RIGHT);
		this.control.putButtonAction(ID_SWAP_JOYSTICKS, () -> this.control.swapJoysticks(), Hand.RIGHT);
		this.control.putButtonAction(ID_DISABLE_TWIST, () -> this.control.setDisableTwistAxis(Hand.LEFT, !this.control.getDisableTwistAxis(Hand.LEFT)), Hand.LEFT);
		this.control.putButtonAction(ID_DISABLE_TWIST, () -> this.control.setDisableTwistAxis(Hand.RIGHT, !this.control.getDisableTwistAxis(Hand.RIGHT)), Hand.RIGHT);
	}

	@Override
	public void onInitialization() {
		Console.debug("Initialization Complete!");
	}

	@Override
	public void preDisabled() {

	}

	@Override
	public void onDisabled() {
		// TODO Auto-generated method stub
	}

	@Override
	public void preAutonomous() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAutonomous() {
		// TODO Auto-generated method stub

	}

	@Override
	public void preTeleop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTeleop() {
		this.control.update();
		updateDrive();
	}

	@Override
	public void preTest() {

	}

	@Override
	public void onTest() {
		// TODO Auto-generated method stub

	}

	@Override
	public JoystickControl getJoysticks() {
		return this.control;
	}

	@Override
	public MotorScheme getMotorScheme() {
		return this.motors;
	}

	@Override
	public RobotDrive getDrive() {
		return this.motors.getRobotDrive();
	}

	@Override
	public void dispose() {
		Console.debug("Disposing Resources...");
	}

}
