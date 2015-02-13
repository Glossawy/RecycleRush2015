package org.usfirst.frc.team1554.lib;

import org.usfirst.frc.team1554.lib.MotorScheme.DriveManager;
import org.usfirst.frc.team1554.lib.collect.Array;
import org.usfirst.frc.team1554.lib.collect.IntMap;
import org.usfirst.frc.team1554.lib.collect.IntMap.Entry;
import org.usfirst.frc.team1554.lib.collect.ObjectSet;
import org.usfirst.frc.team1554.lib.meta.OutOfDateException;

import edu.wpi.first.wpilibj.Joystick;

// TODO Multiple Actions on Button Press (Multimap)
/**
 * Abstraction of basic Joystick Controls. This allows for easier interaction between different Joystick Configurations and RobotDrive. This interaction can be even further abstracted through the use of {@link DriveManager} found in {@link MotorScheme}.
 * 
 * @author Matthew
 */
public interface JoystickControl extends Disposable {

	/**
	 * Abstraction of handedness, particularly useful in DualJoystickControl.
	 * 
	 * @author Matthew
	 */
	public enum Hand {
		LEFT(edu.wpi.first.wpilibj.GenericHID.Hand.kLeft.value), RIGHT(edu.wpi.first.wpilibj.GenericHID.Hand.kRight.value), BOTH(-1);

		public final int WPILib_value;

		private Hand(int val) {
			this.WPILib_value = val;
		}

		public static Hand from(edu.wpi.first.wpilibj.GenericHID.Hand hand) {
			for (final Hand h : values())
				if (h.WPILib_value == hand.value)
					return h;

			throw new OutOfDateException("GenericHID.Hand must have changed! Unsupported Hand Values...");
		}
	}

	/** Get the X Value of the Movement Joystick */
	double getX();

	/** Get the Y Value of the Movement Joystick */
	double getY();

	/** Get the Twist Value of the Twist Joystick */
	double getTwist();

	/** Get the Magnitude (using Euclidean Distance) of the Movement Joystick */
	double getMagnitude();

	/** Get the direction of the Magnitude vector of the Movement Joystick in Radians */
	default double getDirectionRadians() {
		final double x = getX();
		final double y = getY();

		return Math.atan2(x, -y);
	}

	/** Get the direction of the magnitude vector of the Movement Joystick in Degrees */
	default double getDirectionDegrees() {
		return Math.toDegrees(getDirectionRadians());
	}

	default int getPOV(Hand hand) {
		return getPOV(hand, 0);
	}

	/** Get POV Setting (DPad on Controller or Directional Switch on Top of Joystick) */
	int getPOV(Hand hand, int povIndex);

	/** Determine if the Twist Axis of the Left or Right Side Joystick is disabled */
	boolean getDisableTwistAxis(Hand side);

	boolean isDampenOutputs();

	/** Get Twist Joystick */
	Joystick leftJoystick();

	/** Get Movement Joystick */
	Joystick rightJoystick();

	/** Add a Button Action which is a ButtonAction tied to a Button ID */
	void putButtonAction(int bId, ButtonAction action, Hand side);

	/** Add a Button Action which is a ButtonAction tied to a Button ID */
	default void putButtonAction(int bId, String actionName, Runnable action, Hand side) {
		putButtonAction(bId, ButtonAction.as(actionName, action), side);
	}

	/** Remove a Button Action */
	void removeButtonAction(int bId, Hand side);

	IntMap<Array<String>> getBindingInformation(Hand side);

	/**
	 * Swap Joystick sides. This should also swap any actions and side-dependent values. This was implemented for the sole purpose of supporting Left-handed people and allowing on-the-fly swapping.
	 */
	void swapJoysticks();

	void setDampenOutputs(boolean dampen);

	/**
	 * Set the Threshold of the twist value. This is in the range [0, 1] where 1 disables the twist axis. <br />
	 * <br />
	 * What happens once the threshold is reached depends on whether or not the 'cutoff' variable is enabled. If it is enabled then the current value is used. If it is disabled then the value is equal to <code>twistValue - twistThreshold</code> where <code>twistThreshold</code> is positive or negative as appropriate for the <code>twistValue</code>.
	 * 
	 * @param val
	 */
	void setTwistThreshold(double val);

	/**
	 * Sets the Threshold of the magnitude value. This is in the range [0, 1] where 1 disables the movement completely. <br />
	 * <br />
	 * What happens once the threshold is reached depends on whether or not the 'cutoff' variable is enabled. If it is enabled then the current value is used. If it is disabled then the value is equal to <code>magnitude - magnitudeThreshold</code>.
	 * 
	 * @param val
	 */
	void setMagnitudeThreshold(double val);

	/**
	 * Disable the Twist Axis on the sided joystick. This is carried through if the Joysticks are swapped using {@link #swapJoysticks()}.
	 * 
	 * @param side
	 * @param disable
	 */
	void setDisableTwistAxis(Hand side, boolean disable);

	/**
	 * Enable or Disable Joystick Cutoff for Threshold Values. See {@link #setTwistThreshold(double)} and {@link #setMagnitudeThreshold(double)} for details.
	 * 
	 * @param side
	 * @param cutoff
	 */
	void setJoystickCutoff(Hand side, boolean cutoff);

	/**
	 * Check Registered Joystick Button Actions for
	 */
	void update();

	@Override
	void dispose();

	public static IntMap<Array<String>> toBindings(IntMap<ObjectSet<ButtonAction>> actions) {
		final IntMap<Array<String>> bindings = new IntMap<Array<String>>(actions.size);

		for (final Entry<ObjectSet<ButtonAction>> entry : actions.entries()) {
			final int bId = entry.key;
			final Array<String> array = Array.of(String.class);
			for (final ButtonAction action : entry.value) {
				array.add(action.name());
			}

			bindings.put(bId, array);
		}

		return bindings;
	}
}
