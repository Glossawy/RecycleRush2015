package org.usfirst.frc.team1554.lib;

import java.util.Iterator;

import org.usfirst.frc.team1554.lib.collect.Array;
import org.usfirst.frc.team1554.lib.collect.IntMap;
import org.usfirst.frc.team1554.lib.collect.IntMap.Entry;
import org.usfirst.frc.team1554.lib.collect.ObjectSet;
import org.usfirst.frc.team1554.lib.math.MathUtils;

import edu.wpi.first.wpilibj.Joystick;

public class SingleJoystickControl implements JoystickControl {

	private final Joystick stick;
	private final IntMap<ObjectSet<ButtonAction>> actions = new IntMap<>(8);

	private boolean cutoff = false;
	private boolean disableTwist = false;
	private boolean dampen = false;
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
		if (this.disableTwist)
			return 0;
		final double twist = this.stick.getTwist();
		return Math.abs(twist) <= this.twistLim ? 0.0 : this.cutoff ? twist : twist - this.twistLim * (twist < 0 ? -1 : 1);
		// return Math.abs(twist) <= this.twistLim ? 0.0 : twist;
	}

	@Override
	public double getMagnitude() {
		final double x = getX();
		final double y = getY();

		final double mag = Math.sqrt(x * x + y * y);

		return mag <= this.magLim ? 0.0 : this.cutoff ? mag : mag - this.magLim;
		// return mag <= this.magLim ? 0.0 : mag;
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
	public int getPOV(Hand hand, int povIndex) {
		return this.stick.getPOV(povIndex);
	}

	@Override
	public boolean getDisableTwistAxis(Hand side) {
		return this.disableTwist;
	}

	@Override
	public boolean isDampenOutputs() {
		return this.dampen;
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
	public void setDampenOutputs(boolean dampen) {
		this.dampen = dampen;
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
	public void setDisableTwistAxis(Hand side, boolean disable) {
		this.disableTwist = disable;
	}

	@Override
	public void setJoystickCutoff(Hand side, boolean cutoff) {
		this.cutoff = cutoff;
	}

	@Override
	public void putButtonAction(int bId, ButtonAction action, Hand side) {
		if (bId > this.stick.getButtonCount())
			throw new IllegalArgumentException("Button ID can't be greater than the joystick button count!: " + bId + " -> " + this.stick.getButtonCount() + " max");

		ObjectSet<ButtonAction> actions = this.actions.get(bId, null);

		if (action == null) {
			this.actions.put(bId, actions = new ObjectSet<>());
		}

		actions.add(action);
	}

	@Override
	public void removeButtonAction(int bId, Hand side) {
		if (bId > this.stick.getButtonCount())
			throw new IllegalArgumentException("Button ID can't be greater than the joystick button count!: " + bId + " -> " + this.stick.getButtonCount() + " max");

		this.actions.remove(bId);
	}

	@Override
	public void update() {
		final Iterator<Entry<ObjectSet<ButtonAction>>> entries = this.actions.iterator();

		while (entries.hasNext()) {
			final Entry<ObjectSet<ButtonAction>> entry = entries.next();

			if (this.stick.getRawButton(entry.key)) {
				for (final ButtonAction action : entry.value) {
					action.act();
				}
			}
		}
	}

	@Override
	public void dispose() {
	}

	@Override
	public IntMap<Array<String>> getBindingInformation(Hand side) {
		return JoystickControl.toBindings(this.actions);
	}

}
