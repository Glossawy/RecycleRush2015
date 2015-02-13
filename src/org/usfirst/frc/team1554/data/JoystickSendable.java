package org.usfirst.frc.team1554.data;

import org.usfirst.frc.team1554.lib.JoystickControl;
import org.usfirst.frc.team1554.lib.JoystickControl.Hand;
import org.usfirst.frc.team1554.lib.collect.Array;
import org.usfirst.frc.team1554.lib.collect.IntMap;
import org.usfirst.frc.team1554.lib.collect.IntMap.Entry;

import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.tables.ITable;

public class JoystickSendable implements Sendable {

	private final IntMap<Array<String>> actions;
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

		for (final Entry<Array<String>> entry : this.actions.entries()) {
			final String list = createDesc(entry.value);

			subtable.putString("Button " + entry.key, list);
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

	private final StringBuilder builder = new StringBuilder();

	private String createDesc(Array<String> actionNames) {
		this.builder.setLength(0);
		this.builder.trimToSize();

		final String last = actionNames.items[actionNames.size - 1];
		for (final String s : actionNames.items) {
			this.builder.append(s);

			if (s != last) {
				this.builder.append(':');
			}
		}

		return this.builder.toString();
	}
}
