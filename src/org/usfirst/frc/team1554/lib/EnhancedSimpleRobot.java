package org.usfirst.frc.team1554.lib;

import org.usfirst.frc.team1554.lib.io.Console;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.communication.UsageReporting;
import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary.tInstances;
import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary.tResourceType;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

public abstract class EnhancedSimpleRobot extends EnhancedRobotBase {

	private RobotState state = RobotState.DISABLED;
	
	public EnhancedSimpleRobot(String teamName, int teamNumber) {
		super(teamName, teamNumber);
	}

	@Override
	protected void prestart() {
		EnhancedRobotBase.ensureNativesLoaded();
		super.prestart();
	}
	
	@Override
	public void startCompetition() {
		UsageReporting.report(tResourceType.kResourceType_Framework, tInstances.kFramework_Simple);
		
		LiveWindow.setEnabled(false);
		onInitialization();
		
		state.doPreMethod(this);
		while(true) {
			if(isDisabled()) {
				if (this.state != RobotState.DISABLED) {
					Console.info("Exiting " + this.state.name());
					this.state.doPostMethod(this);
					LiveWindow.setEnabled(false);
					this.state = RobotState.DISABLED;
					Console.info("Entering " + this.state.name());
					this.state.doPreMethod(this);
				}
			} else if(isOperatorControl()) {
				if (this.state != RobotState.TELEOP) {
					Console.info("Exiting " + this.state.name());
					this.state.doPostMethod(this);
					LiveWindow.setEnabled(false);
					this.state = RobotState.TELEOP;
					Console.info("Entering " + this.state.name());
					this.state.doPreMethod(this);
				}
			} else if(isAutonomous()) {
				if (this.state != RobotState.AUTONOMOUS) {
					Console.info("Exiting " + this.state.name());
					this.state.doPostMethod(this);
					LiveWindow.setEnabled(false);
					this.state = RobotState.AUTONOMOUS;
					Console.info("Entering " + this.state.name());
					this.state.doPreMethod(this);
				}
			} else if(isTest()) {
				if (this.state != RobotState.TEST_MODE) {
					Console.info("Exiting " + this.state.name());
					this.state.doPostMethod(this);
					LiveWindow.setEnabled(true);
					this.state = RobotState.TEST_MODE;
					Console.info("Entering " + this.state.name());
					this.state.doPreMethod(this);
				}
			}
			
			RobotState tempState = state;
			while(tempState == RobotState.DISABLED ? isDisabled() : isEnabled() && tempState == state) {
				onAny();
				tempState.doOnMethod(this);
				Timer.delay(0.01);
			}
		}
	}

	@Override
	protected abstract void onInitialization();
	
	@Override
	public void preDisabled() {}

	@Override
	public void preAutonomous() {}

	@Override
	public void preTeleop() {}

}
