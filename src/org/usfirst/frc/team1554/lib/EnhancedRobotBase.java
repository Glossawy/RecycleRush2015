package org.usfirst.frc.team1554.lib;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.RobotBase;
import org.usfirst.frc.team1554.lib.util.BufferUtils;

import static edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary.*;

/**
 * Base Class shared by all Enhanced Robot Implementations. Due to the critical nature of understanding
 * how RobotBase classes work and how RT Linux works (as per NI Documentation), PLEASE only subclass this class
 * if you know, for sure, what you are doing. <br />
 * <br />
 * Common pitfalls (like not putting a Timer dalay of atleast 10 milliseconds to {@link #onAny()} calls) can
 * lead to a completely unusable implementation as these errors may lead to OutOfMemory Errors and crashing code.
 *
 * @author Matthew
 */
public abstract class EnhancedRobotBase extends RobotBase implements Disposable {

    public static EnhancedRobotBase RUNNING_INSTANCE;

    /**
     * Representation of Current Robot State. This is the alternative solution to the boolean flag switches in {@link IterativeRobot} that encapsulates method calls as well.
     *
     * @author Matthew
     */
    public enum RobotState {
        /**
         * Disabled Mode Handler
         */
        DISABLED {
            @Override
            public void doPreMethod(EnhancedRobotBase bot) {
                bot.ds().InDisabled(false);
                bot.preDisabled();
            }

            @Override
            public void doOnMethod(EnhancedRobotBase bot) {
                FRCNetworkCommunicationObserveUserProgramDisabled();
                bot.onDisabled();
            }

            @Override
            public void doPostMethod(EnhancedRobotBase bot) {
                bot.postDisabled();
                bot.ds().InDisabled(false);
            }
        },
        /**
         * Teleoperated Handler
         */
        TELEOP {
            @Override
            public void doPreMethod(EnhancedRobotBase bot) {
                bot.ds().InOperatorControl(true);
                bot.preTeleop();
            }

            @Override
            public void doOnMethod(EnhancedRobotBase bot) {
                FRCNetworkCommunicationObserveUserProgramTeleop();
                bot.onTeleop();
            }

            @Override
            public void doPostMethod(EnhancedRobotBase bot) {
                bot.postTeleop();
                bot.ds().InOperatorControl(false);
            }
        },
        /**
         * Autonomous Handler
         */
        AUTONOMOUS {
            @Override
            public void doPreMethod(EnhancedRobotBase bot) {
                bot.ds().InAutonomous(true);
                bot.preAutonomous();
            }

            @Override
            public void doOnMethod(EnhancedRobotBase bot) {
                FRCNetworkCommunicationObserveUserProgramAutonomous();
                bot.onAutonomous();
            }

            @Override
            public void doPostMethod(EnhancedRobotBase bot) {
                bot.postAutonomous();
                bot.ds().InAutonomous(false);
            }
        },
        /**
         * Test Mode Handler
         */
        TEST_MODE {
            @Override
            public void doPreMethod(EnhancedRobotBase bot) {
                bot.ds().InTest(true);
                bot.preTest();
            }

            @Override
            public void doOnMethod(EnhancedRobotBase bot) {
                FRCNetworkCommunicationObserveUserProgramTest();
                bot.onTest();
            }

            @Override
            public void doPostMethod(EnhancedRobotBase bot) {
                bot.postDisabled();
                bot.ds().InTest(false);
            }
        };

        /**
         * Execute Appropriate preX() method. (State Entrance Method)
         *
         * @param bot
         */
        abstract public void doPreMethod(EnhancedRobotBase bot);

        /**
         * Execute Appropriate onX() method. (State Execution Method)
         *
         * @param bot
         */
        abstract public void doOnMethod(EnhancedRobotBase bot);

        /**
         * Execute Appropriate postX() method. (State Exit Method)
         *
         * @param bot
         */
        abstract public void doPostMethod(EnhancedRobotBase bot);

    }

    final Class<? extends EnhancedRobotBase> robotClass;
    final Identifier robotId;

    public EnhancedRobotBase(String teamName, int teamNumber) {
        Thread.setDefaultUncaughtExceptionHandler(new RobotExceptionHandler());
        this.robotClass = getClass();
        this.robotId = Identifier.newBasicID(teamName, teamNumber);
    }

    public EnhancedRobotBase(Identifier id) {
        Thread.setDefaultUncaughtExceptionHandler(new RobotExceptionHandler());
        this.robotClass = getClass();
        this.robotId = id;
    }

    /**
     * {@link EnhancedRobotBase} implementations must implement their own {@link #startCompetition()} implementations. <br />
     * <br />
     * This is the primary execution loop for all EnhancedRobots.
     */
    @Override
    abstract public void startCompetition();

    /**
     * Any initialization code should be called here, it is called immediately before entering the Disabled state in ALL {@link RobotBase} implementations and therefore all {@link EnhancedRobotBase} implementations will follow this. <br />
     */
    protected abstract void onInitialization();

    /**
     * This is an OPTIONAL method. The implementation is non-specific and may not be included in all {@link EnhancedRobotBase} implementations. <br />
     * <br />
     * It IS guaranteed that {@link EnhancedIterativeRobot} and {@link EnhancedSimpleRobot} will call {@link #onAny()} in the normal update loop immediately before the current state method but not necessarily on EVERY iteration as is true for State Methods. This would occur if it is taking an extremely long time for {@link DriverStation#isNewControlData()} to return true or {@link DriverStation#waitForData()} blocks for an excessively long time. <br />
     * <br />
     * Typically this is indicative of an underlying problem with lost network packets.
     */
    protected void onAny() {
    }

    /**
     * Code to execute before entering Disabled Mode.
     */
    abstract public void preDisabled();

    /**
     * Code to execute while Disabled. This is looped as quickly as possible.
     */
    abstract public void onDisabled();

    /**
     * Code to execute while leaving Disabled Mode. Free or Reset State-Dependent Resources and Objects here.
     */
    public void postDisabled() {
        // Console.info("DEFAULT POSTDISABLED()! Override me!");
    }

    /**
     * Code to execute before entering Autonomous.
     */
    abstract public void preAutonomous();

    /**
     * Code to execute while in Autonomous. This is looped as quickly as possible.
     */
    abstract public void onAutonomous();

    /**
     * Code to execute while leaving Autonomous. Free or Reset State-Dependent Resources and Objects here.
     */
    public void postAutonomous() {
    }

    /**
     * Code to execute before entering TeleOp.
     */
    abstract public void preTeleop();

    /**
     * Code to execute while in Teleop. This is looped as quickly as possible.
     */
    abstract public void onTeleop();

    /**
     * Code to execute while leaving Teleop. Free or Reset State-Dependent Resources and Objects here.
     */
    public void postTeleop() {
    }

    /**
     * Code to execute before entering Test Mode.
     */
    public void preTest() {
    }

    /**
     * Code to execute while in Test. This is looped as quickly as possible.
     */
    abstract public void onTest();

    /**
     * Code to execute while leaving Test Mode. Free or Reset State-Dependent Resources and Objects here.
     */
    public void postTest() {
    }

    /**
     * Get Joystick Controls
     *
     * @return
     */
    abstract public JoystickControl getJoysticks();

    /**
     * Get Robot Motor Scheme
     *
     * @return
     */
    abstract public MotorScheme getMotorScheme();

    /**
     * Get Robot Basic Sensors
     *
     * @return
     */
    abstract public BasicSense getBasicSenses();

    public final DriverStation ds() {
        return this.m_ds;
    }

    @Override
    public void free() {
        super.free();

        dispose();
        BufferUtils.disposeAllBuffers();
    }

    @Override
    abstract public void dispose();

    @Override
    protected final void finalize() throws Exception {
        free();
    }

}
