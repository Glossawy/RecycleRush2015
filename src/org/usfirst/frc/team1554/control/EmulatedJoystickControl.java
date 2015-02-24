package org.usfirst.frc.team1554.control;

import edu.wpi.first.wpilibj.Joystick;
import org.usfirst.frc.team1554.lib.ButtonAction;
import org.usfirst.frc.team1554.lib.JoystickControl;
import org.usfirst.frc.team1554.lib.collect.Array;
import org.usfirst.frc.team1554.lib.collect.IntMap;

class EmulatedJoystickControl implements JoystickControl {

    public final double x, y, twist;

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
    public int getPOV(Hand hand, int povIndex) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean getDisableTwistAxis(Hand side) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isDampenOutputs() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Joystick leftJoystick() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Joystick rightJoystick() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void putButtonAction(int bId, ButtonAction action, Hand side) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeButtonAction(int bId, Hand side) {
        // TODO Auto-generated method stub
    }

    @Override
    public void swapJoysticks() {
        // TODO Auto-generated method stub

    }

    @Override
    public void setDampenOutputs(boolean dampen) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setTwistThreshold(double val) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setMagnitudeThreshold(double val) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setDisableTwistAxis(Hand side, boolean disable) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setJoystickCutoff(Hand side, boolean cutoff) {
        // TODO Auto-generated method stub

    }

    @Override
    public void update() {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub

    }

    @Override
    public IntMap<Array<String>> getBindingInformation(Hand side) {
        // TODO Auto-generated method stub
        return null;
    }

}
