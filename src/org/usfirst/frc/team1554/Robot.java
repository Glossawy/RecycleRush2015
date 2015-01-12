package org.usfirst.frc.team1554;

import static org.usfirst.frc.team1554.Ref.Channels.FL_DMOTOR;
import static org.usfirst.frc.team1554.Ref.Channels.FR_DMOTOR;
import static org.usfirst.frc.team1554.Ref.Channels.RL_DMOTOR;
import static org.usfirst.frc.team1554.Ref.Channels.RR_DMOTOR;
import static org.usfirst.frc.team1554.Ref.Ports.JOYSTICK_LEFT;
import static org.usfirst.frc.team1554.Ref.Ports.JOYSTICK_RIGHT;

import org.usfirst.frc.team1554.lib.DualJoystickControl;
import org.usfirst.frc.team1554.lib.EnhancedIterativeRobot;
import org.usfirst.frc.team1554.lib.JoystickControl;
import org.usfirst.frc.team1554.lib.MotorScheme;

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

		this.control = new DualJoystickControl(JOYSTICK_LEFT, JOYSTICK_RIGHT);
		this.motors = MotorScheme.Builder.newFourMotorDrive(FL_DMOTOR, RL_DMOTOR, FR_DMOTOR, RR_DMOTOR).build();
	}

	@Override
	public void onInitialization() {
	}

	@Override
	public void preTeleop() {

	}

	@Override
	public void preAutonomous() {
		updateDrive();
	}

	@Override
	public void onDisabled() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTest() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTeleop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAutonomous() {
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

}
