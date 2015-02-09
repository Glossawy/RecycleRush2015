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
import static org.usfirst.frc.team1554.Ref.Values.CONCURRENCY;
import static org.usfirst.frc.team1554.Ref.Values.DRIVE_SCALE_FACTOR;
import static org.usfirst.frc.team1554.Ref.Values.MAG_STICK_DEADBAND;
import static org.usfirst.frc.team1554.Ref.Values.TWIST_STICK_DEADBAND;

import java.util.concurrent.Callable;

import org.usfirst.frc.team1554.lib.BasicSense;
import org.usfirst.frc.team1554.lib.DualJoystickControl;
import org.usfirst.frc.team1554.lib.EnhancedIterativeRobot;
import org.usfirst.frc.team1554.lib.JoystickControl;
import org.usfirst.frc.team1554.lib.JoystickControl.Hand;
import org.usfirst.frc.team1554.lib.MotorScheme;
import org.usfirst.frc.team1554.lib.MotorScheme.DriveManager;
import org.usfirst.frc.team1554.lib.concurrent.AsyncExecutor;
import org.usfirst.frc.team1554.lib.concurrent.AsyncResult;
import org.usfirst.frc.team1554.lib.concurrent.AsyncTask;
import org.usfirst.frc.team1554.lib.io.Console;
import org.usfirst.frc.team1554.lib.io.FileHandle;
import org.usfirst.frc.team1554.lib.vision.CameraSize;
import org.usfirst.frc.team1554.lib.vision.CameraStream;
import org.usfirst.frc.team1554.lib.vision.USBCamera;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.interfaces.Accelerometer.Range;

// TODO bind value methods and change listeners

/**
 * The VM is configured to automatically run this class, and to call the functions
 * corresponding to each mode, as described in the IterativeRobot documentation. If
 * you change the name of this class or the package after creating this project, you
 * must also update the manifest file in the resource directory.
 */
public class Robot extends EnhancedIterativeRobot {

	private static final AsyncExecutor asyncHub = new AsyncExecutor(CONCURRENCY);

	private final DualJoystickControl control;
	private final MotorScheme motors;
	private final BasicSense senses;
	private final USBCamera camera;

	private SpeedController winchMotor;

	public Robot() {
		super("Oceanside Sailors", 0x612);

		Console.debug("Creating and Initializing Controls/Motor Scheme/Senses...");
		this.control = new DualJoystickControl(JOYSTICK_LEFT, JOYSTICK_RIGHT);
		this.control.setMagnitudeThreshold(MAG_STICK_DEADBAND);
		this.control.setTwistThreshold(TWIST_STICK_DEADBAND);
		this.motors = MotorScheme.Builder.newFourMotorDrive(FL_DMOTOR, RL_DMOTOR, FR_DMOTOR, RR_DMOTOR).setInverted(false, true).setDriveManager(DriveManager.MECANUM_POLAR).addMotor(7, Names.WINCH_MOTOR).build();
		this.motors.getRobotDrive().setMaxOutput(DRIVE_SCALE_FACTOR);
		this.senses = BasicSense.makeBuiltInSense(Range.k4G);

		Console.debug("Initializing Button Actions...");
		this.control.putButtonAction(ID_TURBO_DRIVE, () -> getDrive().setLeftRightMotorOutputs(1.0, -1.0), Hand.RIGHT);
		this.control.putButtonAction(ID_SWAP_JOYSTICKS, () -> this.control.swapJoysticks(), Hand.RIGHT);
		this.control.putButtonAction(ID_DISABLE_TWIST, () -> this.control.setDisableTwistAxis(Hand.LEFT, !this.control.getDisableTwistAxis(Hand.LEFT)), Hand.LEFT);
		this.control.putButtonAction(ID_DISABLE_TWIST, () -> this.control.setDisableTwistAxis(Hand.RIGHT, !this.control.getDisableTwistAxis(Hand.RIGHT)), Hand.RIGHT);

		this.camera = new USBCamera();
		this.camera.setSize(CameraSize.MEDIUM);
		CameraStream.INSTANCE.startAutomaticCapture(this.camera);

	}

	@Override
	public void onInitialization() {
		final FileHandle testFile = new FileHandle("Sweet.txt");
		Console.info(testFile.path() + ": " + testFile.create());

		this.winchMotor = this.motors.getMotor(Names.WINCH_MOTOR);

		Console.debug("Initialization Complete!");
	}

	@Override
	public void preDisabled() {
		this.winchMotor.set(0.0);
	}

	@Override
	public void onDisabled() {
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

		final int pov = this.control.getPOV(Hand.RIGHT);
		this.winchMotor.set(pov == 0 ? 0.1 : pov == 180 ? -0.1 : 0);
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
	public BasicSense getBasicSenses() {
		return this.senses;
	}

	@Override
	public void dispose() {
		Console.debug("Disposing Resources...");
		if (!asyncHub.isShutdown()) {
			asyncHub.dispose();
		}
	}

	public static final <T> AsyncResult<T> addAsyncTask(AsyncTask<T> task) {
		return asyncHub.submit(task);
	}

	public static final <T> AsyncResult<T> addAsyncTask(Callable<T> task) {
		return asyncHub.submit(new AsyncTask<T>() {
			@Override
			public T call() throws Exception {
				return task.call();
			}
		});
	}

	public static final AsyncResult<Void> addAsyncTask(Runnable task) {
		return asyncHub.submit(new AsyncTask<Void>() {

			@Override
			public Void call() throws Exception {
				task.run();
				return null;
			}
		});
	}

}
