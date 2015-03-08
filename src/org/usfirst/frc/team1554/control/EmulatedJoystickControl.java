package org.usfirst.frc.team1554.control;

import edu.wpi.first.wpilibj.Joystick;
import org.usfirst.frc.team1554.lib.collect.IntMap;
import org.usfirst.frc.team1554.lib.common.Action;
import org.usfirst.frc.team1554.lib.common.JoystickControl;

class EmulatedJoystickControl implements JoystickControl {

    private final double x;
    private final double y;
    private final double twist;

    EmulatedJoystickControl(double x, double y, double twist) {
        this.x = x;
        this.y = y;
        this.twist = twist;
    }

    @Override
    public double getX() {
        return this.x;
    }

    @Override
    public double getY() {
        return this.y;
    }

    @Override
    public double getTwist() {
        return this.twist;
    }

    @Override
    public double getMagnitude() {
        return Math.sqrt((this.x * this.x) + (this.y * this.y));
    }

    @Override
    public Iterable<IntMap.Entry<Action>> getButtonActions(Hand hand) {
        return null;
    }

    /**
     * Get the direction of the Magnitude vector of the Movement
     * Joystick in Radians
     */
    @Override
    public double getDirectionRadians() {
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

        return null;
    }

    @Override
    public Joystick rightJoystick() {

        return null;
    }

    @Override
    public void putButtonAction(int bId, Action action, Hand side) {


    }

    @Override
    public void removeButtonAction(int bId, Hand side) {

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

    @Override
    public IntMap<String> getBindingInformation(Hand side) {
        throw new UnsupportedOperationException("Cannot get binding information for AnalogStick");
    }

}
