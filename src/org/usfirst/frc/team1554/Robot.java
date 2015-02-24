package org.usfirst.frc.team1554;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.interfaces.Accelerometer.Range;
import org.usfirst.frc.team1554.Ref.Channels;
import org.usfirst.frc.team1554.control.Move;
import org.usfirst.frc.team1554.control.PneumaticControl;
import org.usfirst.frc.team1554.control.WinchControl;
import org.usfirst.frc.team1554.data.JoystickSendable;
import org.usfirst.frc.team1554.lib.*;
import org.usfirst.frc.team1554.lib.JoystickControl.Hand;
import org.usfirst.frc.team1554.lib.MotorScheme.DriveManager;
import org.usfirst.frc.team1554.lib.concurrent.AsyncExecutor;
import org.usfirst.frc.team1554.lib.concurrent.AsyncResult;
import org.usfirst.frc.team1554.lib.concurrent.AsyncTask;
import org.usfirst.frc.team1554.lib.io.FileHandle;
import org.usfirst.frc.team1554.lib.math.MathUtils;
import org.usfirst.frc.team1554.lib.util.memory.DirectIntArray;
import org.usfirst.frc.team1554.lib.vision.CameraSize;
import org.usfirst.frc.team1554.lib.vision.CameraStream;
import org.usfirst.frc.team1554.lib.vision.USBCamera;

import java.util.concurrent.Callable;

import static edu.wpi.first.wpilibj.smartdashboard.SmartDashboard.*;
import static org.usfirst.frc.team1554.Ref.Buttons.*;
import static org.usfirst.frc.team1554.Ref.Channels.*;
import static org.usfirst.frc.team1554.Ref.Ports.JOYSTICK_LEFT;
import static org.usfirst.frc.team1554.Ref.Ports.JOYSTICK_RIGHT;
import static org.usfirst.frc.team1554.Ref.Values.*;
import static org.usfirst.frc.team1554.control.WinchControl.Direction.DOWNWARDS;
import static org.usfirst.frc.team1554.control.WinchControl.Direction.UPWARDS;

// TODO bind value methods and change listeners

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to each mode, as described in the IterativeRobot documentation. If you change the name of this class or the package after creating this project, you must also update the manifest file in the resource directory.
 */
public class Robot extends EnhancedIterativeRobot {

    private static final AsyncExecutor asyncHub = new AsyncExecutor(CONCURRENCY);

    private final DualJoystickControl control;
    private final PneumaticControl pneumatics;
    private final WinchControl winch;
    private final MotorScheme motors;
    private final BasicSense senses;
    private final USBCamera camera;

    private Move autonomousMoves;

    private double upWinchValue = 1.0;
    private double downWinchValue = 0.6;

    private boolean allowMovement = true;

    public static void main(String[] args) {
        DirectIntArray arr = new DirectIntArray(3);

        for (int i = 0; i < arr.size(); i++) {
            arr.set(i, MathUtils.random(0, 20));
        }

        for (int i = 0; i < arr.size(); i++) {
            System.out.println(arr.get(i));
        }

        arr.free();
    }

    public Robot() {
        super("Sailors", 0x612);
        Console.debug("Creating and Initializing Controls/Motor Scheme/Senses...");
        this.control = new DualJoystickControl(JOYSTICK_LEFT, JOYSTICK_RIGHT);
        this.control.setMagnitudeThreshold(MAG_STICK_DEADBAND);
        this.control.setTwistThreshold(TWIST_STICK_DEADBAND);
        this.motors = MotorScheme.Builder.newFourMotorDrive(FL_DMOTOR, RL_DMOTOR, FR_DMOTOR, RR_DMOTOR).setInverted(false, true).setDriveManager(DriveManager.MECANUM_POLAR).addMotor(Channels.WINCH_MOTOR, Names.WINCH_MOTOR).build();
        this.motors.getRobotDrive().setMaxOutput(DRIVE_SCALE_FACTOR);
        this.senses = BasicSense.makeBuiltInSense(Range.k4G);
        this.pneumatics = new PneumaticControl("Robot Pneumatics Control");
        this.winch = new WinchControl(this.motors.getMotor(Names.WINCH_MOTOR), this.upWinchValue, this.downWinchValue);

        Console.debug("Initializing Button Actions...");
        this.control.putButtonAction(ID_TURBO_DRIVE, "Turbo Speed", () -> getDrive().setLeftRightMotorOutputs(1.0, -1.0), Hand.RIGHT);
        this.control.putButtonAction(ID_SWAP_JOYSTICKS, "Swap Joysticks", () -> this.control.swapJoysticks(), Hand.BOTH);
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

        this.control.putButtonAction(ID_FORKLIFT_UP, "Move Winch Up", () -> this.winch.move(UPWARDS), Hand.RIGHT);
        this.control.putButtonAction(ID_FORKLIFT_DOWN, "Move Winch Down", () -> this.winch.move(DOWNWARDS), Hand.RIGHT);
        this.control.putButtonAction(ID_ARMS_TOGGLE, "Toggle Arms", () -> {
            this.pneumatics.toggleArms();
            Timer.delay(0.5);
        }, Hand.RIGHT);

        this.autonomousMoves = Move.startChain(this).forward(0.2).delay(1).build();
        initDashboard();
        Console.debug("Initialization Complete!");
    }

    @Override
    public void preDisabled() {
        this.winch.disable();
    }

    @Override
    public void onDisabled() {

    }

    @Override
    public void postDisabled() {
        this.winch.enable();
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

    }

    @Override
    public void onTeleop() {
        this.control.update();
        this.winch.update();

        if (this.allowMovement) {
            updateDrive();
        }
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

        this.pneumatics.dispose();
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
        putData(this.pneumatics);
        putData(this.winch);
        putData("Camera Params", CameraStream.INSTANCE);
    }

    private void updateImmutableValues() {
        putData(Names.JOYSTICK_LEFT, new JoystickSendable(this.control, Hand.LEFT));
        putData(Names.JOYSTICK_RIGHT, new JoystickSendable(this.control, Hand.RIGHT));
        putData(this.pneumatics);
    }

    private void updateDashboard() {
        this.allowMovement = getBoolean(Names.ALLOW_MOVEMENT);
        this.upWinchValue = getNumber(Names.WINCH_UP_VALUE);
        this.downWinchValue = getNumber(Names.WINCH_DOWN_VALUE);
    }

}
