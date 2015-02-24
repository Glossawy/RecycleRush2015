package org.usfirst.frc.team1554.control;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.NamedSendable;
import edu.wpi.first.wpilibj.tables.ITable;
import org.usfirst.frc.team1554.Ref.Channels;
import org.usfirst.frc.team1554.lib.Console;
import org.usfirst.frc.team1554.lib.Disposable;
import org.usfirst.frc.team1554.lib.SolenoidValues;

public class PneumaticControl implements Disposable, NamedSendable {

    private final Compressor compressor = new Compressor();
    private final DoubleSolenoid arms = new DoubleSolenoid(Channels.CHANNEL_ARM_FWD, Channels.CHANNEL_ARM_BCK);
    private SolenoidValues currentState = SolenoidValues.OFF;
    private ITable table;

    private final String name;

    public PneumaticControl(String name) {
        this.name = name;
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
        this.arms.set(this.currentState.getValue());
    }

    public void unlockArms() {
        this.currentState = SolenoidValues.FORWARD;
        this.arms.set(this.currentState.getValue());
    }

    public void relaxArms() {
        this.currentState = SolenoidValues.OFF;
        this.arms.set(Value.kOff);
    }

    @Override
    public void initTable(ITable subtable) {
        this.table = subtable;

        subtable.putString("Solenoid State", this.currentState.name());
    }

    @Override
    public ITable getTable() {
        return this.table;
    }

    @Override
    public String getSmartDashboardType() {
        return "Pneumatics State";
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void dispose() {
        relaxArms();
        this.arms.free();
    }

}
