package org.usfirst.frc.team1554.lib;

import org.usfirst.frc.team1554.lib.io.Console; 
import org.usfirst.frc.team1554.lib.meta.RobotExecutionException;
import org.usfirst.frc.team1554.lib.util.RoboUtils;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary;

public abstract class EnhancedRobotBase extends RobotBase implements Disposable {

	/**
	 * Representation of Current Robot State. This is the alternative solution to the
	 * boolean flag switches in {@link IterativeRobot} that encapsulates method calls
	 * as well.
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
				FRCNetworkCommunicationsLibrary.FRCNetworkCommunicationObserveUserProgramDisabled();
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
				FRCNetworkCommunicationsLibrary.FRCNetworkCommunicationObserveUserProgramTeleop();
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
				FRCNetworkCommunicationsLibrary.FRCNetworkCommunicationObserveUserProgramAutonomous();
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
				FRCNetworkCommunicationsLibrary.FRCNetworkCommunicationObserveUserProgramTest();
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

	public EnhancedRobotBase(String teamName, int teamNumber) {
		TeamInfo.set(teamName, teamNumber);
	}

	@Override
	abstract public void startCompetition();

	protected abstract void onInitialization();
	public void onAny(){}

	/**
	 * Code to execute before entering Disabled Mode.
	 */
	abstract public void preDisabled();

	/**
	 * Code to execute while Disabled. This is looped as quickly as possible.
	 */
	abstract public void onDisabled();

	/**
	 * Code to execute while leaving Disabled Mode. Free or Reset State-Dependent
	 * Resources and Objects here.
	 */
	public void postDisabled() {
//		Console.info("DEFAULT POSTDISABLED()! Override me!");
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
	 * Code to execute while leaving Autonomous. Free or Reset State-Dependent
	 * Resources and Objects here.
	 */
	public void postAutonomous() {
//		Console.info("DEFAULT POSTAUTONOMOUS()! Override me!");
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
	 * Code to execute while leaving Teleop. Free or Reset State-Dependent Resources
	 * and Objects here.
	 */
	public void postTeleop() {
//		Console.info("DEFAULT POSTTELEOP()! Override me!");
	}

	/**
	 * Code to execute before entering Test Mode.
	 */
	public void preTest() {
//		Console.info("DEFAULT PRETEST()! Override me!");
	}

	/**
	 * Code to execute while in Test. This is looped as quickly as possible.
	 */
	abstract public void onTest();

	/**
	 * Code to execute while leaving Test Mode. Free or Reset State-Dependent
	 * Resources and Objects here.
	 */
	public void postTest() {
//		Console.info("DEFAULT POSTTEST()! Override me!");
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
		return m_ds;
	}
	
	@Override
	public void free() {
		super.free();

		dispose();
	}

	@Override
	abstract public void dispose();

	public static final void ensureNativesLoaded() {
		try {
			// Force the Static Initializers to Run
//			Class.forName(Native.class.getName());
		} catch (final Exception e) {
			Console.exception(e);
			RoboUtils.exceptionToDS(e);
			throw new RobotExecutionException("Failed to Load Natives!", e);
		}
	}

}
