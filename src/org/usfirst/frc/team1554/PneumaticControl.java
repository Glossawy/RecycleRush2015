package org.usfirst.frc.team1554;

import org.usfirst.frc.team1554.Ref.Channels;
import org.usfirst.frc.team1554.lib.Disposable;
import org.usfirst.frc.team1554.lib.SolenoidValues;
import org.usfirst.frc.team1554.lib.io.Console;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.NamedSendable;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.tables.ITable;

public class PneumaticControl implements Disposable, NamedSendable {

	private final Compressor compressor = new Compressor();
	private final DoubleSolenoid arms = new DoubleSolenoid(Channels.CHANNEL_ARM_FWD, Channels.CHANNEL_ARM_BCK);
	private SolenoidValues currentState = SolenoidValues.OFF;
	private ITable table;
	
	private final String name;
	
	public PneumaticControl(String name){
		this.name = name;
		arms.set(currentState.getValue());
		compressor.setClosedLoopControl(true);
	}
	
	public void toggleArms() {
		Console.debug("Solenoid Value is Toggled");
		switch(currentState) {
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
		currentState = SolenoidValues.REVERSE;
		arms.set(currentState.getValue());
	}
	
	public void unlockArms() {
		currentState = SolenoidValues.FORWARD;
		arms.set(currentState.getValue());
	}
	
	public void relaxArms() {
		currentState = SolenoidValues.OFF;
		arms.set(Value.kOff);
	}
	
	@Override
	public void initTable(ITable subtable) {
		this.table = subtable;
		
		subtable.putString("Solenoid State", currentState.name());
	}

	@Override
	public ITable getTable() {
		return table;
	}

	@Override
	public String getSmartDashboardType() {
		return "Pneumatics State";
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void dispose() {
		relaxArms();
		arms.free();
	}
	
}
