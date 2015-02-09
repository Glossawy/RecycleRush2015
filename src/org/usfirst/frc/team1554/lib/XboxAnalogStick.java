package org.usfirst.frc.team1554.lib;

import org.usfirst.frc.team1554.lib.XboxControl.Axes;

import edu.wpi.first.wpilibj.Joystick;

class XboxAnalogStick implements JoystickControl {

	private final Axes axes;
	private final Joystick control;

	XboxAnalogStick(Axes axes, XboxControl control) {
		this.axes = axes;
		this.control = control.stick;
	}

	XboxAnalogStick(int xAxis, int yAxis, XboxControl control) {
		this(new Axes(xAxis, yAxis), control);
	}

	@Override
	public double getX() {
		return this.axes.getX(this.control);
	}

	@Override
	public double getY() {
		return this.axes.getY(this.control);
	}

	@Override
	public double getMagnitude() {
		final double x = getX();
		final double y = getY();

		return Math.sqrt((x * x) + (y * y));
	}

	@Override
	public double getTwist() {
		return 0;
	}
	
	@Override
	public int getPOV(Hand hand, int povIndex) {
		return 0;
	}

	@Override
	public boolean getDisableTwistAxis(Hand side) {
		return false;
	}

	@Override
	public boolean isDampenOutputs() {
		return false;
	}

	@Override
	public Joystick leftJoystick() {
		return this.control;
	}

	@Override
	public Joystick rightJoystick() {
		return this.control;
	}

	@Override
	public void putButtonAction(int bId, Runnable action, Hand side) {
	}

	@Override
	public Runnable removeButtonAction(int bId, Hand side) {
		return null;
	}

	@Override
	public void swapJoysticks() {
	}

	@Override
	public void setDampenOutputs(boolean dampen) {
	}

	@Override
	public void setTwistThreshold(double val) {
	}

	@Override
	public void setMagnitudeThreshold(double val) {
	}

	@Override
	public void setDisableTwistAxis(Hand side, boolean disable) {
	}

	@Override
	public void setJoystickCutoff(Hand side, boolean cutoff) {
	}

	@Override
	public void update() {
	}

	@Override
	public void dispose() {
	}

}
