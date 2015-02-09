package org.usfirst.frc.team1554.lib;

public class EnhancedSimpleRobot extends EnhancedRobotBase {

	public EnhancedSimpleRobot(String teamName, int teamNumber) {
		super(teamName, teamNumber);
	}

	@Override
	protected void prestart() {
		EnhancedRobotBase.ensureNativesLoaded();
	}

	@Override
	public void onInitialization() {
		// TODO Auto-generated method stub

	}

	@Override
	public void preDisabled() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDisabled() {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

	@Override
	public void onTest() {
		// TODO Auto-generated method stub

	}

	@Override
	public JoystickControl getJoysticks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MotorScheme getMotorScheme() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BasicSense getBasicSenses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
