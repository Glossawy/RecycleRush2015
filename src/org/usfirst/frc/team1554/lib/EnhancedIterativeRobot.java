package org.usfirst.frc.team1554.lib;

import org.usfirst.frc.team1554.lib.util.RoboUtils;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary;
import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary.tInstances;
import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary.tResourceType;
import edu.wpi.first.wpilibj.communication.UsageReporting;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

/**
 * @author Matthew
 */
public abstract class EnhancedIterativeRobot extends RobotBase {

	public enum RobotState {
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

		abstract public void doPreMethod(EnhancedIterativeRobot bot);

		abstract public void doOnMethod(EnhancedIterativeRobot bot);

		abstract public void doPostMethod(EnhancedIterativeRobot bot);
	}

	private final JoystickControl controls;
	private final MotorScheme motorScheme;
	private final RobotDrive drive;

	private RobotState state = null;

	public EnhancedIterativeRobot(JoystickControl controls, MotorScheme motorScheme) {
		this.controls = controls;
		this.motorScheme = motorScheme;
		this.drive = RoboUtils.makeRobotDrive(motorScheme);
	}

	@Override
	protected void prestart() {
		// Don't immediately enable robot
	}

	@Override
	public void startCompetition() {
		UsageReporting.report(tResourceType.kResourceType_Framework, tInstances.kFramework_Iterative);

		onInitialization();

		// We call this now (not in prestart like default) so that the robot
		// won't enable until the initialization has finished. This is useful
		// because otherwise it's sometimes possible to enable the robot
		// before the code is ready.
		FRCNetworkCommunicationsLibrary.FRCNetworkCommunicationObserveUserProgramStarting();

		LiveWindow.setEnabled(true);
		while (true) {
			if (isDisabled()) {
				if (this.state != RobotState.DISABLED) {
					this.state.doPostMethod(this);
					this.state = RobotState.DISABLED;
					this.state.doPreMethod(this);
				}
			} else if (isTest()) {
				if (this.state != RobotState.TEST_MODE) {
					this.state.doPostMethod(this);
					this.state = RobotState.TEST_MODE;
					this.state.doPreMethod(this);
				}
			} else if (isAutonomous()) {
				if (this.state != RobotState.AUTONOMOUS) {
					this.state.doPostMethod(this);
					this.state = RobotState.AUTONOMOUS;
					this.state.doPreMethod(this);
				}
			} else {
				if (this.state != RobotState.TELEOP) {
					this.state.doPostMethod(this);
					this.state = RobotState.TELEOP;
					this.state.doPreMethod(this);
				}
			}

			if (this.m_ds.isNewControlData()) {
				this.state.doOnMethod(this);
			}

			this.m_ds.waitForData();
		}
	}

	abstract public void onInitialization();

	public void preDisabled() {
	}

	public void preTest() {
	}

	abstract public void preTeleop();

	abstract public void preAutonomous();

	abstract public void onDisabled();

	abstract public void onTest();

	abstract public void onTeleop();

	abstract public void onAutonomous();

	public void postDisabled() {
	}

	public void postTest() {
	}

	public void postTeleop() {
	}

	public void postAutonomous() {
	}

	public JoystickControl getJoysticks() {
		return this.controls;
	}

	public MotorScheme getMotorScheme() {
		return this.motorScheme;
	}

	public RobotDrive getDrive() {
		return this.drive;
	}

}
