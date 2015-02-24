package org.usfirst.frc.team1554.control;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.NamedSendable;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.tables.ITable;
import org.usfirst.frc.team1554.lib.util.Preconditions;

public class WinchControl implements NamedSendable {

    public enum Direction {
        UPWARDS, DOWNWARDS;
    }

    private final SpeedController winchMotor;

    private double upValue;
    private double downValue;

    private boolean enabled = true;
    private boolean moveUp = false;
    private boolean moveDown = false;

    private DigitalInput upSwitch;
    private DigitalInput downSwitch;
    private ITable dataTable;

    public WinchControl(SpeedController motor, double upSpeed, double downSpeed, DigitalInput upSwitch, DigitalInput downSwitch) {
        this.winchMotor = motor;
        this.upValue = upSpeed;
        this.downValue = downSpeed;
        this.upSwitch = upSwitch;
        this.downSwitch = downSwitch;
    }

    public WinchControl(SpeedController motor, double upSpeed, double downSpeed, int upSwitchChannel, int downSwitchChannel) {
        this(motor, upSpeed, downSpeed, upSwitchChannel == -1 ? null : new DigitalInput(upSwitchChannel), downSwitchChannel == -1 ? null : new DigitalInput(downSwitchChannel));
    }

    public WinchControl(SpeedController motor, double upSpeed, double downSpeed) {
        this(motor, upSpeed, downSpeed, null, null);
    }

    public void update() {
        double speed = 0;

        if (!(this.moveUp && this.moveDown) && this.enabled) {
            if (this.moveUp && !isSwitchActivated(Direction.UPWARDS)) {
                speed = this.upValue;
            }

            if (this.moveDown && !isSwitchActivated(Direction.DOWNWARDS)) {
                speed = this.downValue;
            }
        }

        this.winchMotor.set(speed);
        this.moveUp = false;
        this.moveDown = false;
    }

    public void move(Direction dir) {
        Preconditions.checkNotNull(dir);

        if (dir == Direction.UPWARDS) {
            this.moveUp = true;
        } else {
            this.moveDown = true;
        }
    }

    public void enable() {
        this.enabled = true;
    }

    public void disable() {
        this.enabled = false;
    }

    public DigitalInput setSwitch(Direction dir, DigitalInput newSwitch) {
        DigitalInput prev;

        if (dir == Direction.UPWARDS) {
            prev = this.upSwitch;
            this.upSwitch = newSwitch;
        } else {
            prev = this.downSwitch;
            this.downSwitch = newSwitch;
        }

        return prev;
    }

    public void setUpSpeed(double upSpeed) {
        this.upValue = upSpeed;
    }

    public void setDownSpeed(double downSpeed) {
        this.downValue = downSpeed;
    }

    public double upSpeed() {
        return this.upValue;
    }

    public double downSpeed() {
        return this.downValue;
    }

    public boolean isSwitchActivated(Direction switchLoc) {
        Preconditions.checkNotNull(switchLoc);

        if (switchLoc == Direction.UPWARDS)
            return (this.upSwitch != null) && this.upSwitch.get();
        else
            return (this.downSwitch != null) && this.downSwitch.get();
    }

    @Override
    public void initTable(ITable subtable) {
        this.dataTable = subtable;
    }

    public void updateNetworkTable() {
        if (this.dataTable == null) return;

        this.dataTable.putBoolean("Enabled", this.enabled);
        this.dataTable.putBoolean("Top Switch", this.upSwitch == null ? false : this.upSwitch.get());
        this.dataTable.putBoolean("Bottom Switch", this.downSwitch == null ? false : this.downSwitch.get());
        this.dataTable.putNumber("Upwards Speed", this.upValue);
        this.dataTable.putNumber("Downwards Speed", this.downValue);
    }

    @Override
    public ITable getTable() {
        return this.dataTable;
    }

    @Override
    public String getSmartDashboardType() {
        return "Winch State";
    }

    @Override
    public String getName() {
        return "Winch State";
    }

}
