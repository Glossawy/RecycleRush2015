package org.usfirst.frc.team1554;

import static edu.wpi.first.wpilibj.smartdashboard.SmartDashboard.getBoolean;
import static edu.wpi.first.wpilibj.smartdashboard.SmartDashboard.getNumber;
import static edu.wpi.first.wpilibj.smartdashboard.SmartDashboard.putBoolean;
import static edu.wpi.first.wpilibj.smartdashboard.SmartDashboard.putData;
import static edu.wpi.first.wpilibj.smartdashboard.SmartDashboard.putNumber;
import static org.usfirst.frc.team1554.Ref.Buttons.ID_DISABLE_TWIST;
import static org.usfirst.frc.team1554.Ref.Buttons.ID_SWAP_JOYSTICKS;
import static org.usfirst.frc.team1554.Ref.Buttons.ID_TURBO_DRIVE;
import static org.usfirst.frc.team1554.Ref.Buttons.ID_ARMS_TOGGLE;
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

import org.usfirst.frc.team1554.Ref.Channels;
import org.usfirst.frc.team1554.data.JoystickSendable;
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


//import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.interfaces.Accelerometer.Range;

// TODO bind value methods and change listeners

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to each mode, as described in the IterativeRobot documentation. If you change the name of this class or the package after creating this project, you must also update the manifest file in the resource directory.
 */
public class Robot extends EnhancedIterativeRobot {

	private static final AsyncExecutor asyncHub = new AsyncExecutor(CONCURRENCY);

	private final DualJoystickControl control;
	private final PneumaticControl pneumatics;
//	private final DigitalInput winchSwitchBottom;
//	private final DigitalInput winchSwitchTop;
	private final MotorScheme motors;
	private final BasicSense senses;
	private final USBCamera camera;

	private SpeedController winchMotor;
	private Move autonomousMoves;

	private double upWinchValue = 1.0;
	private double downWinchValue = 0.6;
	
	private boolean forkliftUp = false;
	private boolean forkliftDown = false;
	private boolean allowMovement = true;

	public Robot() {
		super("Oceanside Sailors", 0x612);

		Console.debug("Creating and Initializing Controls/Motor Scheme/Senses...");
		this.control = new DualJoystickControl(JOYSTICK_LEFT, JOYSTICK_RIGHT);
		this.control.setMagnitudeThreshold(MAG_STICK_DEADBAND);
		this.control.setTwistThreshold(TWIST_STICK_DEADBAND);
		this.motors = MotorScheme.Builder.newFourMotorDrive(FL_DMOTOR, RL_DMOTOR, FR_DMOTOR, RR_DMOTOR).setInverted(false, true).setDriveManager(DriveManager.MECANUM_POLAR).addMotor(Channels.WINCH_MOTOR, Names.WINCH_MOTOR).build();
		this.motors.getRobotDrive().setMaxOutput(DRIVE_SCALE_FACTOR);
		this.senses = BasicSense.makeBuiltInSense(Range.k4G);
		this.pneumatics = new PneumaticControl("Robot Pneumatics Control");
//		this.winchSwitchTop = new DigitalInput(Channels.DIGITAL_WINCH_UP_LIMIT);
//		this.winchSwitchBottom = new DigitalInput(Channels.DIGITAL_WINCH_DOWN_LIMIT);
		
		Console.debug("Initializing Button Actions...");
		this.control.putButtonAction(ID_TURBO_DRIVE, "Turbo Speed", () -> getDrive().setLeftRightMotorOutputs(1.0, -1.0), Hand.RIGHT);
		this.control.putButtonAction(ID_SWAP_JOYSTICKS, "Swap Joysticks", () -> this.control.swapJoysticks(), Hand.RIGHT);
		this.control.putButtonAction(ID_DISABLE_TWIST, "Toggle Left Twist", () -> this.control.setDisableTwistAxis(Hand.LEFT, !this.control.getDisableTwistAxis(Hand.LEFT)), Hand.LEFT);
		this.control.putButtonAction(ID_DISABLE_TWIST, "Toggle Right Twist", () -> this.control.setDisableTwistAxis(Hand.RIGHT, !this.control.getDisableTwistAxis(Hand.RIGHT)), Hand.RIGHT);
		
		Console.debug("Starting Camera Capture...");
		this.camera = new USBCamera();
		this.camera.setSize(CameraSize.MEDIUM);
		CameraStream.INSTANCE.startAutomaticCapture(this.camera);
		Console.debug(String.format("Resolution: %dx%d | Quality: %s | FPS: %s", this.camera.getSize().WIDTH, this.camera.getSize().HEIGHT, this.camera.getQuality().name(), this.camera.getFPS().kFPS));
	}

	@Override
	protected void onInitialization() {
		final FileHandle testFile = new FileHandle("Sweet.txt");
		Console.info(testFile.path() + ": " + testFile.create());

		this.winchMotor = this.motors.getMotor(Names.WINCH_MOTOR);
		
		this.control.putButtonAction(3, "Move Winch Up", () -> forkliftUp = true, Hand.RIGHT);
		this.control.putButtonAction(5, "Move Winch Down", () -> forkliftDown = true, Hand.RIGHT);
		this.control.putButtonAction(ID_ARMS_TOGGLE, "Toggle Arms", () -> {this.pneumatics.toggleArms(); Timer.delay(0.5);}, Hand.RIGHT);
		
		
		this.autonomousMoves = Move.startChain(this).forward(0.2).delay(1).build();
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
		this.autonomousMoves.reset();
	}

	@Override
	public void onAutonomous() {
		this.autonomousMoves.act();
	}

	@Override
	public void preTeleop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTeleop() {
		this.winchMotor.set(0);
		this.control.update();
		
		verticalMoveUp();
		verticalMoveDown();

		if (this.allowMovement) {
			updateDrive();
		}
		
//		updateImmutableValues();
//		updateDashboard();
	}

	@Override
	public void preTest() {

	}

	@Override
	public void onTest() {
		
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
		
		pneumatics.dispose();
	}

	public static final <T> AsyncResult<T> addAsyncTask(AsyncTask<T> task) {
		return asyncHub.submit(task);
	}

	public static final <T> AsyncResult<T> addAsyncTask(Callable<T> task) {
		return asyncHub.submit(() -> task.call());
	}

	public static final AsyncResult<Void> addAsyncTask(Runnable task) {
		return asyncHub.submit(() -> {
			task.run();
			return null;
		});
	}

	private void initDashboard() {
		putData(Names.JOYSTICK_LEFT, new JoystickSendable(this.control, Hand.LEFT));
		putData(Names.JOYSTICK_RIGHT, new JoystickSendable(this.control, Hand.RIGHT));
		putBoolean(Names.ALLOW_MOVEMENT, this.allowMovement);
		putNumber(Names.WINCH_UP_VALUE, this.upWinchValue);
		putNumber(Names.WINCH_DOWN_VALUE, this.downWinchValue);
		putData(pneumatics);
	}
	
	private void verticalMoveUp() {
//		if(this.winchSwitchTop.get())
//			winchMotor.set(0);
//		else 
			if(forkliftUp){
			winchMotor.set(-this.upWinchValue);
			forkliftUp = false;
		}
	}
	
	private void verticalMoveDown() {
//		if(this.winchSwitchBottom.get())
//			winchMotor.set(0);
//		else 
			if(forkliftDown){
			winchMotor.set(this.downWinchValue);
			forkliftDown = false;
		}
	}
	
	private void updateImmutableValues() {
		putData(Names.JOYSTICK_LEFT, new JoystickSendable(this.control, Hand.LEFT));
		putData(Names.JOYSTICK_RIGHT, new JoystickSendable(this.control, Hand.RIGHT));
		putData(pneumatics);
	}

	private void updateDashboard() {
		this.allowMovement = getBoolean(Names.ALLOW_MOVEMENT);
		this.upWinchValue = getNumber(Names.WINCH_UP_VALUE);
		this.downWinchValue = getNumber(Names.WINCH_DOWN_VALUE);
	}

}
