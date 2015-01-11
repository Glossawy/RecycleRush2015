package org.usfirst.frc.team1554.lib;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.Joystick;

public interface JoystickControl {

	double getX();

	double getY();

	double getTwist();

	double getMagnitude();

	double getDirectionRadians();

	double getDirectionDegrees();

	Joystick leftJoystick();

	Joystick rightJoystick();

	void putButtonAction(int bId, Runnable action, Hand side);

	Runnable removeButtonAction(int bId, Hand side);

	void swapJoysticks();

}
