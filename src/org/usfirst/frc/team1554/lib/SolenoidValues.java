package org.usfirst.frc.team1554.lib;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.DoubleSolenoid;

public enum SolenoidValues {

	OFF(Value.kOff),
	FORWARD(Value.kForward),
	REVERSE(Value.kReverse);
	
	private final DoubleSolenoid.Value val;
	
	private SolenoidValues(Value val) {
		this.val = val;
	}
	
	public DoubleSolenoid.Value getValue() {
		return val;
	}
	
}
