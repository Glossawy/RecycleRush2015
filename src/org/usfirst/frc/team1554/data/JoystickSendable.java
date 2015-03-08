package org.usfirst.frc.team1554.data;

import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.tables.ITable;
import org.usfirst.frc.team1554.lib.collect.IntMap;
import org.usfirst.frc.team1554.lib.collect.IntMap.Entry;
import org.usfirst.frc.team1554.lib.common.JoystickControl;
import org.usfirst.frc.team1554.lib.common.JoystickControl.Hand;

public class JoystickSendable implements Sendable {

    private final IntMap<String> actions;
    private ITable table;

    public JoystickSendable(JoystickControl control) {
        this(control, Hand.BOTH);
    }

    public JoystickSendable(JoystickControl control, Hand hand) {
        this.actions = control.getBindingInformation(hand);
    }

    @Override
    public void initTable(ITable subtable) {
        this.table = subtable;

        for (final Entry<String> entry : this.actions.entries()) {
            subtable.putString("Button " + entry.key, entry.value);
        }
    }

    @Override
    public ITable getTable() {
        return this.table;
    }

    @Override
    public String getSmartDashboardType() {
        return "Joystick Bindings";
    }
}
