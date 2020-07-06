/*==================================================================================================
 The MIT License (MIT)

 Copyright (c) 2015 Glossawy

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 =================================================================================================*/

package org.usfirst.frc.team1554.control;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.tables.ITable;
import org.usfirst.frc.team1554.Ref.Channels;
import org.usfirst.frc.team1554.lib.common.Disposable;
import org.usfirst.frc.team1554.lib.common.SendableDataSource;
import org.usfirst.frc.team1554.lib.common.robot.Console;
import org.usfirst.frc.team1554.lib.common.system.SolenoidValues;

public class PneumaticControl implements Disposable, SendableDataSource {

    @SuppressWarnings("FieldCanBeLocal")
    private final Compressor compressor = new Compressor();
    private final DoubleSolenoid arms = new DoubleSolenoid(Channels.CHANNEL_ARM_FWD, Channels.CHANNEL_ARM_BCK);
    private SolenoidValues currentState = SolenoidValues.OFF;

    public PneumaticControl() {
        this.arms.set(this.currentState.getValue());
        this.compressor.setClosedLoopControl(true);
    }

    public void toggleArms() {
        Console.debug("Solenoid Value is Toggled");
        switch (this.currentState) {
            case OFF:
                lockArms();
                break;
            case FORWARD:
                lockArms();
                break;
            case REVERSE:
                unlockArms();
                break;
        }

    }

    public void lockArms() {
        this.currentState = SolenoidValues.REVERSE;
        set(currentState);
    }

    public void unlockArms() {
        this.currentState = SolenoidValues.FORWARD;
        set(currentState);
    }

    public void relaxArms() {
        this.currentState = SolenoidValues.OFF;
        set(currentState);
    }

    private void set(SolenoidValues value) {
        this.arms.set(value.getValue());
        Timer.delay(0.5);
    }

    @Override
    public void setTableValues(ITable subtable) {
        subtable.putString("Solenoid State", this.currentState.name());
    }

    @Override
    public void dispose() {
        relaxArms();
        this.arms.free();
    }

}
