package org.usfirst.frc.team1554.lib;

import java.util.Iterator;

import org.usfirst.frc.team1554.lib.collect.IntMap;
import org.usfirst.frc.team1554.lib.collect.IntMap.Entry;
import org.usfirst.frc.team1554.math.MathUtils;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.Joystick;

public class SingleJoystickControl implements JoystickControl {

	private final Joystick stick;
	private final IntMap<Runnable> actions = new IntMap<Runnable>(8);

	private double twistLim = 0.0, magLim = 0.0;

	public SingleJoystickControl(Joystick stick) {
		this.stick = stick;
	}

	public SingleJoystickControl(int port) {
		this.stick = new Joystick(port);
	}

	@Override
	public double getX() {
		return this.stick.getX();
	}

	@Override
	public double getY() {
		return this.stick.getY();
	}

	@Override
	public double getTwist() {
		final double twist = this.stick.getTwist();
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
		return this.stick;
	}

	@Override
	public Joystick rightJoystick() {
		return this.stick;
	}

	@Override
	public void swapJoysticks() {
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
		if (bId > this.stick.getButtonCount()) throw new IllegalArgumentException("Button ID can't be greater than the joystick button count!: " + bId + " -> " + this.stick.getButtonCount() + " max");

		this.actions.put(bId, action);
	}

	@Override
	public Runnable removeButtonAction(int bId, Hand side) {
		if (bId > this.stick.getButtonCount()) throw new IllegalArgumentException("Button ID can't be greater than the joystick button count!: " + bId + " -> " + this.stick.getButtonCount() + " max");

		return this.actions.remove(bId);
	}

	@Override
	public void update() {
		final Iterator<Entry<Runnable>> entries = this.actions.iterator();

		while (entries.hasNext()) {
			final Entry<Runnable> entry = entries.next();

			if (this.stick.getRawButton(entry.key)) {
				entry.value.run();
			}
		}
	}

}
