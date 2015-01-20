package org.usfirst.frc.team1554.lib;

import java.util.Iterator;

import org.usfirst.frc.team1554.lib.collect.IntMap;
import org.usfirst.frc.team1554.lib.collect.IntMap.Entry;
import org.usfirst.frc.team1554.math.MathUtils;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.Joystick;

public class DualJoystickControl implements JoystickControl {

	private Joystick leftStick, rightStick;
	private IntMap<Runnable> leftActions = new IntMap<Runnable>(8);
	private IntMap<Runnable> rightActions = new IntMap<Runnable>(8);

	private double twistLim = 0.0, magLim = 0.0;

	public DualJoystickControl(Joystick left, Joystick right) {
		this.leftStick = left;
		this.rightStick = right;
	}

	public DualJoystickControl(int leftPort, int rightPort) {
		this(new Joystick(leftPort), new Joystick(rightPort));
	}

	@Override
	public double getX() {
		return this.rightStick.getX();
	}

	@Override
	public double getY() {
		return this.rightStick.getY();
	}

	@Override
	public double getTwist() {
		final double twist = this.leftStick.getTwist();
		return Math.abs(twist) <= this.twistLim ? 0.0 : twist - (this.twistLim * (twist < 0 ? -1 : 1));
	}

	@Override
	public double getMagnitude() {
		final double x = getX();
		final double y = getY();

		final double mag = Math.sqrt((x * x) + (y * y));

		return mag <= this.magLim ? 0.0 : mag - this.magLim;
	}

	@Override
	public double getDirectionRadians() {
		return Math.atan2(getX(), -getY());
	}

	@Override
	public double getDirectionDegrees() {
		return Math.toDegrees(getDirectionRadians());
	}

	@Override
	public Joystick leftJoystick() {
		return this.leftStick;
	}

	@Override
	public Joystick rightJoystick() {
		return this.rightStick;
	}

	@Override
	public void swapJoysticks() {
		final IntMap<Runnable> tmpMap = this.leftActions;
		final Joystick temp = this.leftStick;

		this.leftActions = this.rightActions;
		this.rightActions = tmpMap;

		this.leftStick = this.rightStick;
		this.rightStick = temp;
	}

	@Override
	public void setTwistThreshold(double val) {
		this.twistLim = MathUtils.clamp(val, 0.0, 1.0);
	}

	@Override
	public void setMagnitudeThreshold(double val) {
		this.magLim = MathUtils.clamp(val, 0.0, 1.0);
	}

	@Override
	public void putButtonAction(int bId, Runnable action, Hand side) {
		final IntMap<Runnable> actions = side == Hand.kLeft ? this.leftActions : this.rightActions;
		final Joystick stick = side == Hand.kLeft ? this.leftStick : this.rightStick;

		if (bId > stick.getButtonCount()) throw new IllegalArgumentException("Button ID can't be greater than the joystick button count!: " + bId + " -> " + stick.getButtonCount() + " max");

		actions.put(bId, action);
	}

	@Override
	public Runnable removeButtonAction(int bId, Hand side) {
		final IntMap<Runnable> actions = side == Hand.kLeft ? this.leftActions : this.rightActions;

		return actions.remove(bId);
	}

	@Override
	public void update() {
		Iterator<Entry<Runnable>> ids = this.rightActions.iterator();

		while (ids.hasNext()) {
			final Entry<Runnable> entry = ids.next();

			if (this.rightStick.getRawButton(entry.key)) {
				entry.value.run();
			}
		}

		ids = this.leftActions.iterator();
		while (ids.hasNext()) {
			final Entry<Runnable> entry = ids.next();

			if (this.leftStick.getRawButton(entry.key)) {
				entry.value.run();
			}
		}
	}

}
