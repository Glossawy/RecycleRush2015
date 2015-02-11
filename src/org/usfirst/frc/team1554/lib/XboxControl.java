package org.usfirst.frc.team1554.lib;

import static org.usfirst.frc.team1554.lib.XboxConstants.AXIS_TRIGGER_LEFT;
import static org.usfirst.frc.team1554.lib.XboxConstants.AXIS_TRIGGER_RIGHT;
import static org.usfirst.frc.team1554.lib.XboxConstants.AXIS_X_LEFT_STICK;
import static org.usfirst.frc.team1554.lib.XboxConstants.AXIS_X_RIGHT_STICK;
import static org.usfirst.frc.team1554.lib.XboxConstants.AXIS_Y_LEFT_STICK;
import static org.usfirst.frc.team1554.lib.XboxConstants.AXIS_Y_RIGHT_STICK;
import static org.usfirst.frc.team1554.lib.XboxConstants.BUMPER_LEFT;
import static org.usfirst.frc.team1554.lib.XboxConstants.BUMPER_RIGHT;
import static org.usfirst.frc.team1554.lib.XboxConstants.BUTTON_STICK_LEFT;
import static org.usfirst.frc.team1554.lib.XboxConstants.BUTTON_STICK_RIGHT;
import static org.usfirst.frc.team1554.lib.XboxConstants.DPAD_POV;

import org.usfirst.frc.team1554.lib.collect.IntMap;
import org.usfirst.frc.team1554.lib.collect.IntMap.Entry;
import org.usfirst.frc.team1554.lib.collect.Maps;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;

// FIXME Put documentation and Preconditions to try and prevent user error
/**
 * Implementation for Supporting a USB Xbox Controller as a Joystick. Some helper
 * methods are provided for Xbox-Specific Features.
 * 
 * @author Matthew
 *
 */
public class XboxControl implements JoystickControl {

	private final IntMap<Runnable> buttonMap = Maps.newIntMap(9);
	private final XboxJoystickWrapper wrappedStick;
	final Joystick stick;

	public double twistThreshold = 0.0;
	public double magnitudeThreshold = 0.0;
	public double triggerPressThreshold = 0.9;

	public boolean twistDisabled = false;
	public boolean dampenOutputs = false;
	public boolean doMovementCutoff = false;
	public boolean doRotationCutoff = false;
	public boolean doSwapJoystickButtonActions = true;
	private Axes movAxes, rotAxes;

	public XboxControl(int port) {
		this.stick = new Joystick(port);
		this.movAxes = new Axes(AXIS_X_LEFT_STICK, AXIS_Y_LEFT_STICK);
		this.rotAxes = new Axes(AXIS_X_RIGHT_STICK, AXIS_Y_RIGHT_STICK);
		this.wrappedStick = new XboxJoystickWrapper(this, port);
	}

	@Override
	public double getX() {
		return this.movAxes.getX(this.stick);
	}

	@Override
	public double getY() {
		return this.movAxes.getY(this.stick);
	}

	@Override
	public double getTwist() {
		return this.rotAxes.getX(this.stick);
	}

	@Override
	public double getMagnitude() {
		return this.movAxes.getMagnitude(this.stick);
	}

	@Override
	public double getDirectionRadians() {
		final double x = getX();
		final double y = getY();

		return Math.atan2(x, -y);
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
		return this.twistDisabled;
	}

	public boolean isBumperPressed(Hand hand) {
		switch (hand) {
		case RIGHT:
			return this.stick.getRawButton(BUMPER_RIGHT);
		case LEFT:
			return this.stick.getRawButton(BUMPER_LEFT);
		default:
			return this.stick.getRawButton(BUMPER_RIGHT) && this.stick.getRawButton(BUMPER_LEFT);
		}
	}

	public boolean isTriggerPressed(Hand hand) {
		switch (hand) {
		case RIGHT:
			return this.stick.getRawAxis(AXIS_TRIGGER_RIGHT) > this.triggerPressThreshold;
		case LEFT:
			return this.stick.getRawAxis(AXIS_TRIGGER_LEFT) > this.triggerPressThreshold;
		default:
			return isTriggerPressed(Hand.RIGHT) && isTriggerPressed(Hand.LEFT);
		}
	}

	public boolean isStickPressed(Hand hand) {
		switch (hand) {
		case RIGHT:
			return this.stick.getRawButton(BUTTON_STICK_RIGHT);
		case LEFT:
			return this.stick.getRawButton(BUTTON_STICK_LEFT);
		default:
			return isStickPressed(Hand.RIGHT) && isStickPressed(Hand.LEFT);
		}
	}

	public boolean isDPadCenterPressed() {
		return getDirectionDPad() == -1;
	}

	public double getDirectionDPad() {
		return this.stick.getPOV(DPAD_POV);
	}

	public double getTriggerValue(Hand hand) {
		switch (hand) {
		case RIGHT:
			return this.stick.getRawAxis(AXIS_TRIGGER_RIGHT);
		case LEFT:
			return this.stick.getRawAxis(AXIS_TRIGGER_LEFT);
		default:
			return this.stick.getRawAxis(AXIS_TRIGGER_RIGHT) * this.stick.getRawAxis(AXIS_TRIGGER_LEFT);
		}
	}

	/**
	 * Returns a JoystickControl representing a single analog stick. Since it is
	 * literally just 2 axes and a button, this Control is very limited. The button
	 * cannot be configured using the returned JoystickControl to maintain
	 * consistency between the "Analog Stick View" and the actual JoystickControl it
	 * is attached to.
	 * 
	 * @param side
	 * @return
	 */
	public JoystickControl getAnalogStick(Hand side) {
		switch (side) {
		case LEFT:
			return new XboxAnalogStick(AXIS_X_LEFT_STICK, AXIS_Y_LEFT_STICK, this);
		case RIGHT:
			return new XboxAnalogStick(AXIS_X_RIGHT_STICK, AXIS_Y_RIGHT_STICK, this);
		default:
			throw new UnsupportedOperationException("At this time you cannot get both Analog Sticks in a single JoystickControl!");
		}
	}

	@Override
	public boolean isDampenOutputs() {
		return this.dampenOutputs;
	}

	@Override
	public Joystick leftJoystick() {
		return this.wrappedStick;
	}

	@Override
	public Joystick rightJoystick() {
		return this.wrappedStick;
	}

	@Override
	public void putButtonAction(int bId, Runnable action, Hand side) {
		this.buttonMap.put(bId, action);
	}

	@Override
	public Runnable removeButtonAction(int bId, Hand side) {
		return this.buttonMap.remove(bId);
	}

	@Override
	public void swapJoysticks() {
		final Axes tempAxes = this.movAxes;
		final Runnable tempAct = this.buttonMap.get(BUTTON_STICK_LEFT, DO_NOTHING);
		final boolean tempCutoff = this.doMovementCutoff;

		this.movAxes = this.rotAxes;
		this.rotAxes = tempAxes;

		this.doMovementCutoff = this.doRotationCutoff;
		this.doRotationCutoff = tempCutoff;

		if (this.doSwapJoystickButtonActions) {
			this.buttonMap.put(BUTTON_STICK_LEFT, this.buttonMap.get(BUTTON_STICK_RIGHT, DO_NOTHING));
			this.buttonMap.put(BUTTON_STICK_RIGHT, tempAct);
		}
	}

	@Override
	public void setDampenOutputs(boolean dampen) {
		this.dampenOutputs = dampen;
	}

	@Override
	public void setTwistThreshold(double val) {
		this.twistThreshold = val;
	}

	@Override
	public void setMagnitudeThreshold(double val) {
		this.magnitudeThreshold = val;
	}

	@Override
	public void setDisableTwistAxis(Hand side, boolean disable) {
		this.twistDisabled = disable;
	}

	@Override
	public void setJoystickCutoff(Hand side, boolean cutoff) {
		switch (side) {
		case LEFT:
			this.doMovementCutoff = cutoff;
			break;
		case RIGHT:
			this.doRotationCutoff = cutoff;
			break;
		case BOTH:
			this.doMovementCutoff = cutoff;
			this.doRotationCutoff = cutoff;
			break;
		}
	}

	@Override
	public void update() {
		for (final Entry<Runnable> entry : this.buttonMap.entries())
			if (this.stick.getRawButton(entry.key) && (entry.value != null)) {
				entry.value.run();
			}
	}

	@Override
	public void dispose() {
	}

	static class Axes {
		int xAxis, yAxis;

		Axes(int x, int y) {
			this.xAxis = x;
			this.yAxis = y;
		}

		double getX(GenericHID hid) {
			return hid.getRawAxis(this.xAxis);
		}

		double getY(GenericHID hid) {
			return hid.getRawAxis(this.yAxis);
		}

		double getMagnitude(GenericHID hid) {
			final double x = getX(hid);
			final double y = getY(hid);

			return Math.sqrt((x * x) + (y * y));
		}
	}

	public static class XboxJoystickWrapper extends Joystick {

		private final XboxControl control;

		private XboxJoystickWrapper(XboxControl control, int port) {
			super(port);
			this.control = control;
		}

		@Override
		public double getX(Hand hand) {
			return this.control.getX();
		}

		@Override
		public double getY(Hand hand) {
			return this.control.getY();
		}

		@Override
		public double getZ(Hand hand) {
			return 0.0;
		}

		@Override
		public double getTwist() {
			return this.control.getTwist();
		}

		@Override
		public double getMagnitude() {
			return this.control.getMagnitude();
		}

		@Override
		public double getDirectionRadians() {
			return this.control.getDirectionRadians();
		}

		@Override
		public double getDirectionDegrees() {
			return this.control.getDirectionDegrees();
		}

		@Override
		public boolean getBumper(Hand hand) {
			return this.control.isBumperPressed(org.usfirst.frc.team1554.lib.JoystickControl.Hand.from(hand));
		}

		@Override
		public boolean getTrigger(Hand hand) {
			return this.control.isTriggerPressed(org.usfirst.frc.team1554.lib.JoystickControl.Hand.from(hand));
		}

		@Override
		public boolean getTop(Hand hand) {
			return this.control.isBumperPressed(org.usfirst.frc.team1554.lib.JoystickControl.Hand.from(hand));
		}
	}

	private static Runnable DO_NOTHING = () -> {
	};

}
