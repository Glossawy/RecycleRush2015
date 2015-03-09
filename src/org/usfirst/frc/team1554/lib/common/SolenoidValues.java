package org.usfirst.frc.team1554.lib.common;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public enum SolenoidValues {

    OFF(Value.kOff), FORWARD(Value.kForward), REVERSE(Value.kReverse);

    private final Value val;

    private SolenoidValues(Value val) {
        this.val = val;
    }

    public Value getValue() {
        return this.val;
    }

}
