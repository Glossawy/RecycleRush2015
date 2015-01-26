package org.usfirst.frc.team1554.lib;

import org.usfirst.frc.team1554.lib.io.Console;
import org.usfirst.frc.team1554.lib.util.RoboUtils;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary;
import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary.tInstances;
import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary.tResourceType;
import edu.wpi.first.wpilibj.communication.UsageReporting;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

// TODO Listeners
/**
 * An Alternative (and hopefully enhanced) approach to {@link IterativeRobot}. For
 * one this class is Abstract and requires the user to implement certain methods that
 * just make sense. <br />
 * <br />
 * This implementation also uses a separate {@link RobotState} enum to manage State
 * and State Transfer. This bot also uses the more abstract {@link JoystickControl}
 * and {@link MotorScheme} classes to make getting started much easier and providing
 * convenience methods for many things. Creating a {@link RobotDrive} is abstracted
 * to {@link RoboUtils#makeRobotDrive(MotorScheme) makeRobotDrive} and basic movement
 * is abstracted to {@link MotorScheme#updateDrive(RobotDrive, JoystickControl)
 * MotorScheme.updateDrive} or even just {@link #updateDrive()}.<br />
 * <br />
 * This implementation is noticeably larger than IterativeBot due to the fact that
 * instead of just initX() and periodicX(), this class uses Entrance, On, AND Exit
 * methods. i.e. preX(), onX(), postX() for a probably more logical flow of
 * initialization, execution and finalization between states.
 * 
 * @author Matthew
 */
public abstract class EnhancedIterativeRobot extends RobotBase implements Disposable {

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
			public void doPreMethod(EnhancedIterativeRobot bot) {
				bot.preDisabled();
			}

			@Override
			public void doOnMethod(EnhancedIterativeRobot bot) {
				FRCNetworkCommunicationsLibrary.FRCNetworkCommunicationObserveUserProgramDisabled();
				bot.onDisabled();
			}

			@Override
			public void doPostMethod(EnhancedIterativeRobot bot) {
				bot.postDisabled();
			}
		},
		/**
		 * Teleoperated Handler
		 */
		TELEOP {
			@Override
			public void doPreMethod(EnhancedIterativeRobot bot) {
				bot.preTeleop();
			}

			@Override
			public void doOnMethod(EnhancedIterativeRobot bot) {
				FRCNetworkCommunicationsLibrary.FRCNetworkCommunicationObserveUserProgramTeleop();
				bot.onTeleop();
			}

			@Override
			public void doPostMethod(EnhancedIterativeRobot bot) {
				bot.postTeleop();
			}
		},
		/**
		 * Autonomous Handler
		 */
		AUTONOMOUS {
			@Override
			public void doPreMethod(EnhancedIterativeRobot bot) {
				bot.preAutonomous();
			}

			@Override
			public void doOnMethod(EnhancedIterativeRobot bot) {
				FRCNetworkCommunicationsLibrary.FRCNetworkCommunicationObserveUserProgramAutonomous();
				bot.onAutonomous();
			}

			@Override
			public void doPostMethod(EnhancedIterativeRobot bot) {
				bot.postAutonomous();
			}
		},
		/**
		 * Test Mode Handler
		 */
		TEST_MODE {
			@Override
			public void doPreMethod(EnhancedIterativeRobot bot) {
				bot.preTest();
			}

			@Override
			public void doOnMethod(EnhancedIterativeRobot bot) {
				FRCNetworkCommunicationsLibrary.FRCNetworkCommunicationObserveUserProgramTest();
				bot.onTest();
			}

			@Override
			public void doPostMethod(EnhancedIterativeRobot bot) {
				bot.postDisabled();
			}
		};

		/**
		 * Execute Appropriate preX() method. (State Entrance Method)
		 * 
		 * @param bot
		 */
		abstract public void doPreMethod(EnhancedIterativeRobot bot);

		/**
		 * Execute Appropriate onX() method. (State Execution Method)
		 * 
		 * @param bot
		 */
		abstract public void doOnMethod(EnhancedIterativeRobot bot);

		/**
		 * Execute Appropriate postX() method. (State Exit Method)
		 * 
		 * @param bot
		 */
		abstract public void doPostMethod(EnhancedIterativeRobot bot);
	}

	private RobotState state = RobotState.DISABLED;
	private boolean forceLive = false;

	public EnhancedIterativeRobot() {
		super();
	}

	@Override
	protected final void prestart() {
		// Don't immediately enable robot
	}

	/**
	 * Run Competition in an Infinite Loop akin to {@link IterativeRobot} but using
	 * modern Java features such as enums ({@link RobotState}) and now with "Pre",
	 * "On" and "Post" methods. Essentially states not have an Entrance method, a
	 * During method and an Exit method. <br />
	 * <br />
	 * The general method call sequence in a state change is: <Br />
	 * <br />
	 * 
	 * <pre>
	 * {@code                             
	 *                                   When Moving from State X to State Y
	 *  startCompetition() -> preX() -> onX() -> postX() -> preY() -> ...
	 *                          ^         |
	 *                          |         | While Still in State X
	 *                          |<-------<-
	 * }
	 * </pre>
	 * 
	 * <br />
	 * Where X is the Start State and Y is the Proceeding State. '...' represents the
	 * Y version of this same loop which will then move to either State X or State Z. <br />
	 * <br />
	 * If the Robot reaches an exceptional state (throws an Exception) and it is not
	 * caught by User Code, then the exception is logged (By {@link Console} and
	 * {@link RoboComms} by proxy) and then thrown to WPILib to be handled as
	 * appropriate.
	 * 
	 * @author Matthew Crocco
	 */
	@Override
	public final void startCompetition() {
		UsageReporting.report(tResourceType.kResourceType_Framework, tInstances.kFramework_Iterative);

		onInitialization();

		// We call this now (not in prestart like default) so that the robot
		// won't enable until the initialization has finished. This is useful
		// because otherwise it's sometimes possible to enable the robot
		// before the code is ready.
		FRCNetworkCommunicationsLibrary.FRCNetworkCommunicationObserveUserProgramStarting();

		LiveWindow.setEnabled(false);
		this.state.doPreMethod(this);
		try {
			while (true) {
				if (isDisabled()) {
					if (this.state != RobotState.DISABLED) {
						Console.info("Exiting " + this.state.name());
						this.state.doPostMethod(this);
						LiveWindow.setEnabled(false || this.forceLive);
						this.state = RobotState.DISABLED;
						Console.info("Entering " + this.state.name());
						this.state.doPreMethod(this);
					}
				} else if (isTest()) {
					if (this.state != RobotState.TEST_MODE) {
						Console.info("Exiting " + this.state.name());
						this.state.doPostMethod(this);
						LiveWindow.setEnabled(true);
						this.state = RobotState.TEST_MODE;
						Console.info("Entering " + this.state.name());
						this.state.doPreMethod(this);
					}
				} else if (isAutonomous()) {
					if (this.state != RobotState.AUTONOMOUS) {
						Console.info("Exiting " + this.state.name());
						this.state.doPostMethod(this);
						LiveWindow.setEnabled(false || this.forceLive);
						this.state = RobotState.AUTONOMOUS;
						Console.info("Entering " + this.state.name());
						this.state.doPreMethod(this);
					}
				} else {
					if (this.state != RobotState.TELEOP) {
						Console.info("Exiting " + this.state.name());
						this.state.doPostMethod(this);
						LiveWindow.setEnabled(false || this.forceLive);
						this.state = RobotState.TELEOP;
						Console.info("Entering " + this.state.name());
						this.state.doPreMethod(this);
					}
				}

				if (this.m_ds.isNewControlData()) {
					this.state.doOnMethod(this);
				}

				this.m_ds.waitForData();
			}
		} catch (final Throwable t) {
			// This is the WORST Case Scenario. Only way to break out of the loop.
			System.err.println("Huh. Robot Code Missed Exception/Throwable.");
			Console.exception(t);
			RoboUtils.exceptionToDS(t);
		} finally {
			free();
		}
	}

	/**
	 * Called before the Iterative Loop begins. Any and all variables should be
	 * initialized here if not in the constructor. After this is called,
	 * {@link #getJoysticks()} and {@link #getMotorScheme()} is called. If these are
	 * null then an exception is thrown as they are required by
	 * {@link EnhancedIterativeRobot}.
	 */
	abstract public void onInitialization();

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
		Console.info("DEFAULT POSTDISABLED()! Override me!");
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
		Console.info("DEFAULT POSTAUTONOMOUS()! Override me!");
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
		Console.info("DEFAULT POSTTELEOP()! Override me!");
	}

	/**
	 * Code to execute before entering Test Mode.
	 */
	public void preTest() {
		Console.info("DEFAULT PRETEST()! Override me!");
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
		Console.info("DEFAULT POSTTEST()! Override me!");
	}

	/**
	 * Get Joystick Controls
	 * 
	 * @return
	 */
	abstract public JoystickControl getJoysticks();

	/**
	 * Get Robot Motor Sheme
	 * 
	 * @return
	 */
	abstract public MotorScheme getMotorScheme();

	/**
	 * Get Robot Drive
	 * 
	 * @return
	 */
	abstract public RobotDrive getDrive();

	abstract public BasicSense getBasicSenses();
	
	/**
	 * Fprce LiveWindow to be available in ALL modes.
	 * 
	 * @param forceLiveWindow
	 */
	public void setLiveWindowForced(boolean forceLiveWindow) {
		this.forceLive = forceLiveWindow;
	}

	/**
	 * Is LiveWindow Force Available in ALL modes?
	 * 
	 * @return
	 */
	public boolean isLiveWindowForced() {
		return this.forceLive;
	}

	/**
	 * Update RobotDrive as appropriate with the current JoystickControls.
	 */
	public void updateDrive() {
		getMotorScheme().getDriveManagement().updateDrive(getDrive(), getJoysticks(), getBasicSenses());
	}

	@Override
	public final void free() {
		getMotorScheme().dispose();
		getJoysticks().dispose();
		getBasicSenses().dispose();
		
		dispose();
	}

	@Override
	abstract public void dispose();

}
