package org.usfirst.frc.team1554.lib;

import org.usfirst.frc.team1554.lib.collect.IntMap;
import org.usfirst.frc.team1554.lib.collect.IntMap.Keys;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.Joystick;

public class SingleJoystickControl implements JoystickControl {

	private final Joystick stick;
	private final IntMap<Runnable> actions = new IntMap<Runnable>(8);

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
		return this.stick.getTwist();
	}

	@Override
	public double getMagnitude() {
		final double x = getX();
		final double y = getY();

		return Math.sqrt((x * x) + (y * y));
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
		Keys keys = actions.keys();
		
		while(keys.hasNext) {
			int id = keys.next();
			
			if(stick.getRawButton(id))
				actions.get(id, null).run();
		}
	}

}
