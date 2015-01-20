package org.usfirst.frc.team1554.lib.io.comms;

import static org.usfirst.frc.team1554.lib.io.comms.RoboCommsConstants.ACCELEROMETER_ENTRY;
import static org.usfirst.frc.team1554.lib.io.comms.RoboCommsConstants.ALLIANCE_ENTRY;
import static org.usfirst.frc.team1554.lib.io.comms.RoboCommsConstants.DEBUG_TABLE;
import static org.usfirst.frc.team1554.lib.io.comms.RoboCommsConstants.LINE_PREFIX;
import static org.usfirst.frc.team1554.lib.io.comms.RoboCommsConstants.MAX_SIZE;
import static org.usfirst.frc.team1554.lib.io.comms.RoboCommsConstants.PIE_ENTRY;
import static org.usfirst.frc.team1554.lib.io.comms.RoboCommsConstants.ROOT_TABLE;
import static org.usfirst.frc.team1554.lib.io.comms.RoboCommsConstants.TEMPERATURE_ENTRY;
import static org.usfirst.frc.team1554.lib.io.comms.RoboCommsConstants.VISIBILITY_ENTRY;
import static org.usfirst.frc.team1554.lib.io.comms.RoboCommsConstants.VOLTAGE_ENTRY;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.usfirst.frc.team1554.lib.collect.Maps;
import org.usfirst.frc.team1554.lib.io.Console;
import org.usfirst.frc.team1554.lib.util.Preconditions;

import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;

/**
 * A Singleton Communicator that is used on the Server (Robot) Side of RoboComms. <br />
 * <br />
 * RoboComms is a Networking Program that uses WPILib's NetworkTables library to
 * transfer Robot System data and System Output Stream data to an external program
 * (FRC 1554 call it, simply, RoboComms Receiver) which acts in NetworkTable's Client
 * Mode and receives this data. <br />
 * <br />
 * A simple Protocol is also defined for handling Custom "User Tables" that can be
 * programatically introduced and parsed by the RoboComms Receiver as desired. If the
 * PDP stop or CAN is removed this class should be modified although the Protocol is
 * defined and may be replicated easily.
 * 
 * @author Matthew
 */
public enum RoboComms {

	INSTANCE;

	private final Lock bufLock = new ReentrantLock(true);

	private final ITable commsTable; // Stores Communication Data (Output Stream
	// Data)
	private final ITable debugTable; // Stores System Info Data (CAN, Voltages, Etc.)
	private final Timer timer;

	private final DriverStation ds = DriverStation.getInstance();
	private final PowerDistributionPanel pdp = new PowerDistributionPanel();
	private final int MAX_LINE = MAX_SIZE;

	private final Map<ITable, TableInfoProvider> userTables = Maps.newHashMap();
	private Accelerometer accel = null;
	private final String[] buffer = new String[this.MAX_LINE];
	private int index = 0;

	private RoboComms() {
		this.commsTable = NetworkTable.getTable(ROOT_TABLE);
		this.debugTable = this.commsTable.getSubTable(DEBUG_TABLE);

		this.timer = new Timer(true);
		this.timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				updateTables();
			}
		}, 5000, 500);

	}

	/**
	 * Write an Empty Line to the Console
	 */
	public void writeLine() {
		Console.info("");
	}

	/**
	 * Write a line of text to the RoboComms Console
	 * 
	 * @param line
	 */
	public void writeLine(String line) {
		// Maintain lock to prevent Update Thread Desync
		this.bufLock.lock();
		try {
			// If we have reached the end, we have to shift data and insert the new
			// line.
			// If we have space, life is simple.
			if (this.buffer.length == this.index) {
				for (int i = 1; i < this.buffer.length; i++) {
					this.buffer[i - 1] = this.buffer[i];
				}

				this.buffer[this.index - 1] = line;
			} else {
				this.buffer[this.index++] = line;
			}
		} finally {
			this.bufLock.unlock();
		}

		updateBufferTable();
	}

	/**
	 * Insert Line at a Given Position in the Buffer. This position is equivalent to
	 * the line number - 1
	 * 
	 * @param line
	 * @param position
	 */
	public void insertLine(String line, int position) {
		Preconditions.checkExpression(position < this.MAX_LINE, "Cannot insert line at " + position + " when only " + this.MAX_LINE + " lines are supported!");

		// Prevent Desync
		this.bufLock.lock();
		try {
			if (position == this.index) {
				writeLine(line); // Double Lock should be fine since it is a
				// Reentrant Lock
			} else if (position < this.index) {
				// We have to shift all data above this line
				for (int i = this.index; i >= position; i--) {
					if (i >= (this.buffer.length - 1)) {
						continue;
					}

					this.buffer[i + 1] = this.buffer[i];
				}

				this.buffer[position] = line;

				if (this.buffer.length != this.index) {
					this.index++;
				}
			} // Ignore anything above index. It wouldn't be displayed anyway.
		} finally {
			this.bufLock.unlock();
		}

		updateBufferTable();
	}

	/**
	 * Added since {@link BuiltInAccelerometer} may not be available or desired. By
	 * default, no Accelerometer is queried.
	 * 
	 * @param accelerometer
	 */
	public void setAccelerometer(Accelerometer accelerometer) {
		this.accel = accelerometer;
	}

	/**
	 * Create and Receive new User Table. The Info Provider will be called whenever
	 * an update is needed (this is approximately every 0.5 seconds.
	 * 
	 * @param name
	 * @param infoProvider
	 */
	public void getUserTable(String name, TableInfoProvider infoProvider) {
		final ITable table = this.commsTable.getSubTable(name);
		table.putBoolean(VISIBILITY_ENTRY, false);
		infoProvider.updateTable(table);
		this.userTables.put(table, infoProvider);
	}

	/**
	 * Determine if a User Table should be visible.
	 * 
	 * @param name
	 * @param visible
	 */
	public void setUserTableVisibility(String name, boolean visible) {
		Preconditions.checkExpression(this.commsTable.containsSubTable(name));
		final ITable table = this.commsTable.getSubTable(name);
		Preconditions.checkExpression(table == this.debugTable, "Clients CANNOT Modify Debug Table ['sysinfo']!");

		table.putBoolean(VISIBILITY_ENTRY, visible);
	}

	/**
	 * Lock Buffer and update Comms Table Entries
	 */
	private void updateBufferTable() {
		this.bufLock.lock();
		try {
			for (int i = 0; (i < this.buffer.length) && (this.buffer[i] != null); i++) {
				this.commsTable.putString(LINE_PREFIX + i, this.buffer[i]);
			}
		} finally {
			this.bufLock.unlock();
		}
	}

	/**
	 * Update System Info and User Tables
	 */
	private void updateTables() {
		this.debugTable.putString(ALLIANCE_ENTRY, this.ds.getAlliance() + " " + this.ds.getLocation());
		this.debugTable.putNumber(TEMPERATURE_ENTRY, this.pdp.getTemperature());
		this.debugTable.putNumber(VOLTAGE_ENTRY, this.pdp.getVoltage());
		this.debugTable.putString(PIE_ENTRY, String.format("P: %.03f\tI: %.03f\tE: %.03f", this.pdp.getTotalPower(), this.pdp.getTotalCurrent(), this.pdp.getTotalEnergy()));

		if (this.accel == null) {
			this.debugTable.putString(ACCELEROMETER_ENTRY, "No Accelerometer Provided!");
		} else {
			this.debugTable.putString(ACCELEROMETER_ENTRY, String.format("[%s] X: %.03f Y: %.03f Z: %.03f", (this.accel instanceof BuiltInAccelerometer) ? "Built-In" : "Provided", this.accel.getX(), this.accel.getY(), this.accel.getZ()));
		}

		for (final Entry<ITable, TableInfoProvider> tableEntry : this.userTables.entrySet()) {
			tableEntry.getValue().updateTable(tableEntry.getKey());
		}
	}

}
