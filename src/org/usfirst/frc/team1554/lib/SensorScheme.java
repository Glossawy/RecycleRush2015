package org.usfirst.frc.team1554.lib;

import java.util.Map;

import org.usfirst.frc.team1554.lib.collect.Maps;
import org.usfirst.frc.team1554.lib.meta.Author;
import org.usfirst.frc.team1554.lib.meta.Beta;
import org.usfirst.frc.team1554.lib.meta.Noteworthy;
import org.usfirst.frc.team1554.lib.util.IBuilder;

import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;
import edu.wpi.first.wpilibj.interfaces.Accelerometer.Range;
import edu.wpi.first.wpilibj.interfaces.Potentiometer;

@Beta
@Author(name = "Matthew Crocco", msg = "matthewcrocco@gmail.com")
@Noteworthy("This is actively in development as a recent feature!")
public interface SensorScheme {

	Gyro gyro();

	Accelerometer accelerometer();

	Map<String, Encoder> encoders();

	Map<String, Potentiometer> potentiometers();

	default double gyroAngle() {
		return gyro() == null ? 0.0 : gyro().getAngle();
	}

	public static class Builder implements IBuilder<SensorScheme> {

		private final Map<String, Encoder> encoders = Maps.newHashMap();
		@SuppressWarnings("unused")
		private final Map<String, Potentiometer> potentiometers = Maps.newHashMap();

		// TODO SOlenoids
		// TODO Counters

		private Builder() {
		}

		public static Builder create() {
			return new Builder();
		}

		public Builder setGyro(Gyro gyro) {
			return this;
		}

		public Builder setAccelerometer(Accelerometer accel) {
			return this;
		}

		public Builder useBuiltInAccelerometer() {
			return setAccelerometer(new BuiltInAccelerometer());
		}

		public Builder useBuiltInAccelerometer(Range range) {
			return setAccelerometer(new BuiltInAccelerometer(range));
		}

		public Builder addEncoder(String name, Encoder encoder) {
			encoders.put(name, encoder);

			return this;
		}

		@Override
		public SensorScheme build() {
			throw new UnsupportedOperationException();
		}

	}
}
