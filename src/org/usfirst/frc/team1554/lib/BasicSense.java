package org.usfirst.frc.team1554.lib;

import org.usfirst.frc.team1554.lib.math.Vector3;

import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.NamedSendable;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;
import edu.wpi.first.wpilibj.interfaces.Accelerometer.Range;
import edu.wpi.first.wpilibj.tables.ITable;

/**
 * A simple container class for representing Basic Sensors inside of this API.<br />
 * <br />
 * This only requires a Gyro and Accelerometer. In fact both can be null.
 * 
 * @author Matthew
 */
public class BasicSense implements Disposable, NamedSendable {

	private final Gyro gyro;
	private final Accelerometer accelerometer;

	private ITable table;

	/**
	 * Returns a "Blind" Robot. No Gyro or Accelerometer is available and we are not
	 * using the RoboRIO built in accelerometer.
	 * 
	 * @return
	 */
	public static BasicSense makeNoSensors() {
		return new BasicSense(null, null);
	}

	/**
	 * Return a robot that can only sense direction, we are not using the
	 * {@link BuiltInAccelerometer}.
	 * 
	 * @param gyro
	 * @return
	 */
	public static BasicSense makeGyroOnly(Gyro gyro) {
		return new BasicSense(gyro, null);
	}

	/**
	 * Returns a robot that can only sense G Force. We are not using a Gyro.
	 * 
	 * @param accel
	 * @return
	 */
	public static BasicSense makeAccelerometerOnly(Accelerometer accel) {
		return new BasicSense(null, accel);
	}

	/**
	 * Returns a Robot that can sense both G Force and Gyro Direction.
	 * 
	 * @param gyro
	 * @param accel
	 * @return
	 */
	public static BasicSense makeBasicSense(Gyro gyro, Accelerometer accel) {
		return new BasicSense(gyro, accel);
	}

	/**
	 * Returns a Robot that can sense direction and uses the RoboRIO
	 * {@link BuiltInAccelerometer}.
	 * 
	 * @param gyro
	 * @param range
	 * @return
	 */
	public static BasicSense makeBuiltInSense(Gyro gyro, Range range) {
		return makeBasicSense(gyro, new BuiltInAccelerometer(range));
	}

	/**
	 * Returns a Robot that can sense directions and uses the RoboRIO
	 * {@link BuiltInAccelerometer}.
	 * 
	 * @param gyro
	 * @return
	 */
	public static BasicSense makeBuiltInSense(Gyro gyro) {
		return makeBuiltInSense(gyro, Range.k8G);
	}

	/**
	 * Returns a robot that uses the RoboRIO {@link BuiltInAccelerometer} but cannot
	 * sense direction.
	 * 
	 * @param range
	 * @return
	 */
	public static BasicSense makeBuiltInSense(Range range) {
		return makeBasicSense(null, new BuiltInAccelerometer(range));
	}

	/**
	 * Returns a robot that uses the RoboRIO {@link BuiltInAccelerometer} in an 8G
	 * range but cannot sense direction.
	 * 
	 * @return
	 */
	public static BasicSense makeBuiltInSense() {
		return makeBuiltInSense(Range.k8G);
	}

	private BasicSense(Gyro gyro, Accelerometer accel) {
		this.gyro = gyro;
		this.accelerometer = accel;
	}

	/**
	 * Get the attached {@link Gyro}
	 * 
	 * @return
	 */
	public Gyro getGyro() {
		return this.gyro;
	}

	/**
	 * Get the attached {@link Accelerometer}
	 * 
	 * @return
	 */
	public Accelerometer getAccelerometer() {
		return this.accelerometer;
	}

	/**
	 * Get the X Acceleration
	 * 
	 * @return
	 */
	public double getX() {
		return this.accelerometer.getX();
	}

	/**
	 * Get the Y Acceleration
	 * 
	 * @return
	 */
	public double getY() {
		return this.accelerometer.getY();
	}

	/**
	 * Get the Z Acceleration
	 * 
	 * @return
	 */
	public double getZ() {
		return this.accelerometer.getZ();
	}

	/**
	 * Get the Magnitude of the Acceleration.
	 * 
	 * @return
	 */
	public double getAccelerationMagnitude() {
		final double x = getX();
		final double y = getY();
		final double z = getZ();

		return Math.sqrt((x * x) + (y * y) + (z * z));
	}

	/**
	 * Get the current Acceleration as a 3D Vector. See {@link Vector3}
	 * 
	 * @return
	 */
	public Vector3 getAccelerationVector() {
		return new Vector3(getX(), getY(), getZ());
	}

	/**
	 * Get Gyro angle in the range [0, 360)
	 * 
	 * @return
	 */
	public double getAngle() {
		if (!hasGyro()) return 0.0;

		return this.gyro.getAngle() % 360.0;
	}

	/**
	 * Get Gyro angle in the range [0, 2pi)
	 * 
	 * @return
	 */
	public double getAngleRadians() {
		if (!hasGyro()) return 0.0;

		return Math.toRadians(this.gyro.getAngle() % 360.0);
	}

	/**
	 * Returns whether or not a Gyro is attached.
	 * 
	 * @return
	 */
	public boolean hasGyro() {
		return this.gyro != null;
	}

	/**
	 * Returns whether or not an Accelerometer is attached.
	 * 
	 * @return
	 */
	public boolean hasAccelerometer() {
		return this.accelerometer != null;
	}

	@Override
	public void dispose() {
		this.gyro.free();
	}

	@Override
	public void initTable(ITable subtable) {
		this.table = subtable;

	}

	public void updateTable() {
		if (this.gyro != null) {
			this.table.putNumber("Angle (Deg)", getAngle());
			this.table.putNumber("Angle (Rad)", getAngleRadians());
		}

		if (this.accelerometer != null) {
			this.table.putValue("G Forces", getAccelerationVector());
		}
	}

	@Override
	public ITable getTable() {
		return this.table;
	}

	@Override
	public String getSmartDashboardType() {
		return "Basic Sensors";
	}

	@Override
	public String getName() {
		return "Basic Sense";
	}

}
