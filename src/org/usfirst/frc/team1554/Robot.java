/*==================================================================================================
 The MIT License (MIT)

 Copyright (c) 2015 Glossawy

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 =================================================================================================*/

package org.usfirst.frc.team1554;

import static edu.wpi.first.wpilibj.smartdashboard.SmartDashboard.*;
import static org.usfirst.frc.team1554.Ref.Buttons.*;
import static org.usfirst.frc.team1554.Ref.Channels.*;
import static org.usfirst.frc.team1554.Ref.Ports.JOYSTICK_LEFT;
import static org.usfirst.frc.team1554.Ref.Ports.JOYSTICK_RIGHT;
import static org.usfirst.frc.team1554.Ref.Values.*;
import static org.usfirst.frc.team1554.control.WinchControl.Direction.DOWNWARDS;
import static org.usfirst.frc.team1554.control.WinchControl.Direction.UPWARDS;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.interfaces.Accelerometer.Range;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;
import org.usfirst.frc.team1554.Ref.Channels;
import org.usfirst.frc.team1554.control.Move;
import org.usfirst.frc.team1554.control.PneumaticControl;
import org.usfirst.frc.team1554.control.WinchControl;
import org.usfirst.frc.team1554.control.WinchControl.Direction;
import org.usfirst.frc.team1554.lib.common.control.DualJoystickControl;
import org.usfirst.frc.team1554.lib.common.control.JoystickControl;
import org.usfirst.frc.team1554.lib.common.control.JoystickControl.Hand;
import org.usfirst.frc.team1554.lib.common.robot.Console;
import org.usfirst.frc.team1554.lib.common.robot.EnhancedIterativeRobot;
import org.usfirst.frc.team1554.lib.common.system.BasicSense;
import org.usfirst.frc.team1554.lib.common.system.MotorScheme;
import org.usfirst.frc.team1554.lib.common.system.MotorScheme.DriveManager;
import org.usfirst.frc.team1554.lib.concurrent.AsyncExecutor;
import org.usfirst.frc.team1554.lib.concurrent.AsyncResult;
import org.usfirst.frc.team1554.lib.concurrent.AsyncTask;
import org.usfirst.frc.team1554.lib.vision.CameraSize;
import org.usfirst.frc.team1554.lib.vision.CameraStream;
import org.usfirst.frc.team1554.lib.vision.USBCamera;

import java.util.concurrent.Callable;

// TODO bind value methods and change listeners

/**
 * The VM is configured to automatically run this class, and to call
 * the functions corresponding to each mode, as described in the
 * IterativeRobot documentation. If you change the name of this class
 * or the package after creating this project, you must also update
 * the manifest file in the resource directory.
 */
public class Robot extends EnhancedIterativeRobot {

    private static final AsyncExecutor asyncHub = new AsyncExecutor(CONCURRENCY);

    private final DualJoystickControl control;
    private final PneumaticControl pneumatics;
    private final WinchControl winch;
    private final MotorScheme motors;
    private final BasicSense senses;
    @SuppressWarnings("FieldCanBeLocal")
    private final USBCamera camera;
    private final ITable sdTable;

    private Move autonomousMoves;

    private double upWinchValue = 1.0;
    private double downWinchValue = -0.8;

    private boolean allowMovement = true;

    public Robot() {
        super("Sailors", 0x612);
        this.sdTable = NetworkTable.getTable("SmartDashboard");

        Talon winchMotor = new Talon(Channels.WINCH_MOTOR);

        Console.debug("Creating and Initializing Controls/Motor Scheme/Senses...");
        this.control = new DualJoystickControl(JOYSTICK_LEFT, JOYSTICK_RIGHT);
        this.control.setMagnitudeThreshold(MAG_STICK_DEADBAND);
        this.control.setTwistThreshold(TWIST_STICK_DEADBAND);
        this.motors = MotorScheme.Builder.newFourMotorDrive(FL_DMOTOR, RL_DMOTOR, FR_DMOTOR, RR_DMOTOR).setInverted(false, true).setDriveManager(DriveManager.MECANUM_POLAR).addMotor(winchMotor).build();
        this.motors.getRobotDrive().setMaxOutput(DRIVE_SCALE_FACTOR);
        this.senses = BasicSense.makeBuiltInSense(Range.k4G);
        this.pneumatics = new PneumaticControl();
        this.winch = new WinchControl(winchMotor, this.upWinchValue, this.downWinchValue);

        Console.debug("Initializing Button Actions...");
        this.control.putButtonAction(ID_SWAP_JOYSTICKS, "Swap Joysticks", this.control::swapJoysticks, Hand.BOTH);
        this.control.putButtonAction(ID_DISABLE_TWIST, "Toggle Left Twist", () -> this.control.toggleDisableTwistAxis(Hand.LEFT), Hand.LEFT);
        this.control.putButtonAction(ID_DISABLE_TWIST, "Toggle Right Twist", () -> this.control.toggleDisableTwistAxis(Hand.RIGHT), Hand.RIGHT);

        Console.debug("Starting Camera Capture...");
        this.camera = new USBCamera();
        this.camera.setSize(CameraSize.MEDIUM);
        CameraStream.INSTANCE.startAutomaticCapture(this.camera);
        Console.debug(String.format("Resolution: %dx%d | Quality: %s | FPS: %s", this.camera.getSize().WIDTH, this.camera.getSize().HEIGHT, this.camera.getQuality().name(), this.camera.getFPS().kFPS));
    }


    @Override
    protected void onInitialization() {
        this.control.putButtonAction(ID_FORKLIFT_UP, "Move Winch Up", () -> this.winch.move(UPWARDS), Hand.RIGHT);
        this.control.putButtonAction(ID_FORKLIFT_DOWN, "Move Winch Down", () -> this.winch.move(DOWNWARDS), Hand.RIGHT);
        this.control.putButtonAction(ID_ARMS_TOGGLE, "Toggle Arms", pneumatics::toggleArms, Hand.RIGHT);

        this.autonomousMoves = Move.startChain(this)
                .custom(pneumatics::lockArms)
                .delay(2.5)
                .custom(() -> {
                    winch.move(Direction.UPWARDS);
                    winch.update();
                })
                .delay(2.5)
                .custom(() -> {
                    winch.disable();
                    winch.update();
                })
                .cartesian(0, 0.8)
                .delay(2.5)
                .stop()
                .build();

        initDashboard();
        Console.info("Initialization Complete!");
    }

    @Override
    public void preDisabled() {
        this.winch.disable();
    }

    @Override
    public void onDisabled() {
        pneumatics.unlockArms();
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

    public static <T> AsyncResult<T> addAsyncTask(AsyncTask<T> task) {
        return asyncHub.submit(task);
    }

    public static <T> AsyncResult<T> addAsyncTask(Callable<T> task) {
        return asyncHub.submit(() -> task.call());
    }

    public static AsyncResult<Void> addAsyncTask(Runnable task) {
        return asyncHub.submit(() -> {
            task.run();
            return null;
        });
    }

    private void initDashboard() {
        putBoolean(Names.ALLOW_MOVEMENT, this.allowMovement);
        putNumber(Names.WINCH_UP_VALUE, this.upWinchValue);
        putNumber(Names.WINCH_DOWN_VALUE, this.downWinchValue);
        this.pneumatics.setTableValues(sdTable);
        putData(this.winch);
        putData("Camera Params", CameraStream.INSTANCE);
    }

}
