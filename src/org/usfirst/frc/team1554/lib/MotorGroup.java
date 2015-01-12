package org.usfirst.frc.team1554.lib;

import java.util.Set;

import org.usfirst.frc.team1554.lib.collect.Sets;

import edu.wpi.first.wpilibj.MotorSafety;
import edu.wpi.first.wpilibj.MotorSafetyHelper;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.SensorBase;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;

/**
 * Represents a Group of Motors that must attempt to remain synchronized in speed.
 *
 * @author Matthew
 * @param <T>
 */
public class MotorGroup<T extends PWM & SpeedController> extends SensorBase implements SpeedController, MotorSafety, LiveWindowSendable {

	private final MotorSafetyHelper msHelper = new MotorSafetyHelper(this);
	private final Set<T> motors;

	private ITableListener mTableListener;
	private ITable mTable;

	public MotorGroup(T[] synchronizedMotors) {
		this.motors = Sets.newHashSet(synchronizedMotors);
	}

	public void addMotor(T motor) {
		this.motors.add(motor);
		set(get());
		updateTable();
	}

	public boolean removeMotor(T motor) {
		return this.motors.remove(motor);
	}

	public Iterable<T> getMotors() {
		return this.motors;
	}

	@Override
	public void pidWrite(double output) {
		set(output);
	}

	@Override
	public double get() {
		if (this.motors.size() <= 0) return 0;

		double sum = 0;

		for (final T sc : this.motors) {
			sum += sc.get();
		}

		return sum / this.motors.size();
	}

	@Override
	public void set(double speed, byte syncGroup) {
		set(speed);
		feed();
	}

	@Override
	public void set(double speed) {
		for (final T sc : this.motors) {
			sc.set(speed);
		}

		feed();
	}

	@Override
	public void disable() {
		for (final T sc : this.motors) {
			sc.setRaw(0);
		}
	}

	@Override
	public void setExpiration(double timeout) {
		this.msHelper.setExpiration(timeout);
	}

	@Override
	public double getExpiration() {
		return this.msHelper.getExpiration();
	}

	@Override
	public boolean isAlive() {
		return this.msHelper.isAlive();
	}

	@Override
	public void stopMotor() {
		disable();
	}

	@Override
	public void setSafetyEnabled(boolean enabled) {
		this.msHelper.setSafetyEnabled(enabled);
	}

	@Override
	public boolean isSafetyEnabled() {
		return this.msHelper.isSafetyEnabled();
	}

	@Override
	public String getDescription() {
		final StringBuilder sb = new StringBuilder("{MotorGroup: [");

		int count = 0;
		for (final T sc : this.motors) {
			count++;
			sb.append("PWM ").append(sc.getChannel());

			if (count != this.motors.size()) {
				sb.append(", ");
			}
		}

		sb.append("]}");

		return sb.toString();
	}

	@Override
	public void initTable(ITable subtable) {
		this.mTable = subtable;
		updateTable();
	}

	@Override
	public ITable getTable() {
		return this.mTable;
	}

	@Override
	public String getSmartDashboardType() {
		return "Speed Controller";
	}

	@Override
	public void updateTable() {
		if (this.mTable != null) {
			this.mTable.putNumber("Synch. Speed", get());
		}
	}

	@Override
	public void startLiveWindowMode() {
		set(0);
		this.mTableListener = new ITableListener() {
			@Override
			public void valueChanged(ITable source, String key, Object value, boolean isNew) {
				set(((Double) value).doubleValue());
			}
		};

		this.mTable.addTableListener("Speed 0", this.mTableListener, true);
	}

	@Override
	public void stopLiveWindowMode() {
		set(0);
		this.mTable.removeTableListener(this.mTableListener);
	}

	@Override
	public void free() {
		for (final T sc : this.motors) {
			sc.free();
		}

		this.motors.clear();
		super.free();
	}

	private void feed() {
		this.msHelper.feed();
	}
}
