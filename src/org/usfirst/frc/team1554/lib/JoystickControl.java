package org.usfirst.frc.team1554.lib;

import edu.wpi.first.wpilibj.Joystick;

public interface JoystickControl {

	public enum Hand {
		LEFT, RIGHT, BOTH;
	}

	double getX();

	double getY();

	double getTwist();

	double getMagnitude();

	double getDirectionRadians();

	double getDirectionDegrees();

	boolean getDisableTwistAxis(Hand side);

	Joystick leftJoystick();

	Joystick rightJoystick();

	void putButtonAction(int bId, Runnable action, Hand side);

	Runnable removeButtonAction(int bId, Hand side);

	void swapJoysticks();

	void setTwistThreshold(double val);

	void setMagnitudeThreshold(double val);

	void setDisableTwistAxis(Hand side, boolean disable);

	void update();
}
