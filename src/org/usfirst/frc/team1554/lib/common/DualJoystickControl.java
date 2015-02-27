package org.usfirst.frc.team1554.lib.common;
import static org.usfirst.frc.team1554.lib.common.JoystickControl.toBindings;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Preferences;
import org.usfirst.frc.team1554.lib.collect.IntMap;
import org.usfirst.frc.team1554.lib.collect.IntMap.Entry;
import org.usfirst.frc.team1554.lib.math.MathUtils;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class DualJoystickControl implements JoystickControl {

    public static final String LEFT_STICK_TWIST_DISABLE = "joystick.left.twist";
    public static final String RIGHT_STICK_TWIST_DISABLE = "joystick.right.twist";
    public static final String LEFT_STICK_DO_CUTOFF = "joystick.left.cutoff";
    public static final String RIGHT_STICK_DO_CUTOFF = "joystick.right.cutoff";

    private Joystick leftStick, rightStick;
    private final Preferences prefs = Preferences.getInstance();
    private IntMap<ButtonAction> leftActions = new IntMap<>(8);
    private IntMap<ButtonAction> rightActions = new IntMap<>(8);

    private double twistLim = 0.0, magLim = 0.0;
    private boolean dampen = false;

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
        if (this.prefs.getBoolean(LEFT_STICK_TWIST_DISABLE, false))
            return 0;
        final boolean cutoff = this.prefs.getBoolean(LEFT_STICK_DO_CUTOFF, false);

        final double twist = this.leftStick.getTwist();
        return Math.abs(twist) <= this.twistLim ? 0.0 : cutoff ? twist : twist - (this.twistLim * (twist < 0 ? -1 : 1));
    }

    @Override
    public double getMagnitude() {
        final boolean cutoff = this.prefs.getBoolean(RIGHT_STICK_DO_CUTOFF, false);
        final double x = getX();
        final double y = getY();

        final double mag = Math.sqrt((x * x) + (y * y));

        return mag <= this.magLim ? 0.0 : cutoff ? mag : mag - this.magLim;
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
        switch (hand) {
            case RIGHT:
                return this.rightStick.getPOV(povIndex);
            case LEFT:
                return this.leftStick.getPOV(povIndex);
            default:
                throw new UnsupportedOperationException("Cannot get POV for both joysticks!");
        }
    }

    @Override
    public IntMap<String> getBindingInformation(Hand side) {
        switch (side) {
            case LEFT:
                return toBindings(this.leftActions);
            case RIGHT:
                return toBindings(this.rightActions);
            case BOTH:
                final IntMap<String> bindings = toBindings(this.leftActions);

                for (final Entry<String> entry : toBindings(this.rightActions).entries()) {
                    if (bindings.get(entry.key, null) != null) {
                        String name = "Right = " + bindings.get(entry.key, null) + ", Left = " + entry.value;
                        bindings.put(entry.key, name);
                    } else {
                        bindings.put(entry.key, entry.value);
                    }
                }

                return bindings;
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public boolean getDisableTwistAxis(Hand side) {
        switch (side) {
            case LEFT:
                return this.prefs.getBoolean(LEFT_STICK_TWIST_DISABLE, false);
            case RIGHT:
                return this.prefs.getBoolean(RIGHT_STICK_TWIST_DISABLE, false);
            case BOTH:
                return this.prefs.getBoolean(LEFT_STICK_TWIST_DISABLE, false) && this.prefs.getBoolean(RIGHT_STICK_TWIST_DISABLE, false);
            default:
                return false;
        }
    }

    @Override
    public boolean isDampenOutputs() {
        return this.dampen;
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
        final IntMap<ButtonAction> tmpMap = this.leftActions;
        final Joystick temp = this.leftStick;
        final boolean tbool = this.prefs.getBoolean(LEFT_STICK_TWIST_DISABLE, false);
        final boolean cbool = this.prefs.getBoolean(LEFT_STICK_DO_CUTOFF, false);

        // Swap Left-Right Dependent Parameters
        this.leftActions = this.rightActions;
        this.rightActions = tmpMap;

        this.leftStick = this.rightStick;
        this.rightStick = temp;

        this.prefs.putBoolean(LEFT_STICK_TWIST_DISABLE, this.prefs.getBoolean(RIGHT_STICK_TWIST_DISABLE, false));
        this.prefs.putBoolean(RIGHT_STICK_TWIST_DISABLE, tbool);

        this.prefs.putBoolean(LEFT_STICK_DO_CUTOFF, this.prefs.getBoolean(RIGHT_STICK_DO_CUTOFF, false));
        this.prefs.putBoolean(RIGHT_STICK_DO_CUTOFF, cbool);
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

        switch (side) {
            case LEFT:
                this.prefs.putBoolean(LEFT_STICK_TWIST_DISABLE, disable);
                break;
            case RIGHT:
                this.prefs.putBoolean(RIGHT_STICK_TWIST_DISABLE, disable);
                break;
            case BOTH:
                setDisableTwistAxis(Hand.LEFT, disable);
                setDisableTwistAxis(Hand.RIGHT, disable);
                break;
        }
    }

    @Override
    public void setJoystickCutoff(Hand side, boolean cutoff) {
        switch (side) {
            case LEFT:
                this.prefs.putBoolean(LEFT_STICK_DO_CUTOFF, cutoff);
                break;
            case RIGHT:
                this.prefs.putBoolean(RIGHT_STICK_DO_CUTOFF, cutoff);
                break;
            case BOTH:
                setJoystickCutoff(Hand.LEFT, cutoff);
                setJoystickCutoff(Hand.RIGHT, cutoff);
                break;
        }
    }

    @Override
    public Iterable<Entry<ButtonAction>> getButtonActions(Hand hand) {
        if (hand == Hand.LEFT)
            return leftActions.entries();
        else if (hand == Hand.RIGHT)
            return rightActions.entries();
        else {
            return new DualEntries(leftActions.entries(), rightActions.entries());
        }
    }

    @Override
    public void putButtonAction(int bId, ButtonAction action, Hand side) {

        if (side == Hand.BOTH) {
            putButtonAction(bId, action, Hand.LEFT);
            putButtonAction(bId, action, Hand.RIGHT);
            return;
        }

        final IntMap<ButtonAction> actions = side == Hand.LEFT ? this.leftActions : this.rightActions;
        final Joystick stick = side == Hand.RIGHT ? this.leftStick : this.rightStick;

        if (bId > stick.getButtonCount())
            throw new IllegalArgumentException("Button ID can't be greater than the joystick button count!: " + bId + " -> " + stick.getButtonCount() + " max");

        actions.put(bId, action);
    }

    @Override
    public void removeButtonAction(int bId, Hand side) {

        if (side == Hand.BOTH) {
            removeButtonAction(bId, Hand.LEFT);
            removeButtonAction(bId, Hand.RIGHT);
        }

        final IntMap<ButtonAction> actions = side == Hand.LEFT ? this.leftActions : this.rightActions;

        actions.remove(bId);
    }

    @Override
    public void update() {
        Iterator<Entry<ButtonAction>> ids = this.rightActions.iterator();

        while (ids.hasNext()) {
            final Entry<ButtonAction> entry = ids.next();

            if (this.rightStick.getRawButton(entry.key)) {
                entry.value.act();
            }
        }

        ids = this.leftActions.iterator();
        while (ids.hasNext()) {
            final Entry<ButtonAction> entry = ids.next();

            if (this.leftStick.getRawButton(entry.key)) {
                entry.value.act();
            }
        }
    }

    @Override
    public void dispose() {
    }

    private static class DualEntries implements Iterator<Entry<ButtonAction>>, Iterable<Entry<ButtonAction>> {

        IntMap.Entries<ButtonAction> left, right;

        public DualEntries(IntMap.Entries<ButtonAction> left, IntMap.Entries<ButtonAction> right) {
            this.left = left;
            this.right = right;
        }

        /**
         * Returns {@code true} if the iteration has more elements.
         * (In other words, returns {@code true} if {@link #next}
         * would
         * return an element rather than throwing an exception.)
         *
         * @return {@code true} if the iteration has more elements
         */
        @Override
        public boolean hasNext() {
            return left.hasNext() || right.hasNext();
        }

        /**
         * Returns the next element in the iteration.
         *
         * @return the next element in the iteration
         * @throws NoSuchElementException if the iteration has no more
         *                                elements
         */
        @Override
        public Entry<ButtonAction> next() {
            if (left.hasNext())
                return left.next();
            if (right.hasNext())
                return right.next();

            throw new NoSuchElementException("No More ButtonActions!");
        }

        @Override
        public Iterator<Entry<ButtonAction>> iterator() {
            return this;
        }
    }

}
