package org.usfirst.frc.team1554.lib.math;

public class WindowAverage {

	public static void main(String[] args) {
		final WindowAverage avg = new WindowAverage(500);

		for (int i = 0; i < (Integer.MAX_VALUE / 10); i++) {
			avg.addValue(MathUtils.random(-100, 100));
		}

		System.out.println("Reached Calculations! =D");
		System.out.println(avg.min());
		System.out.println(avg.max());
		System.out.println(avg.mean());
		System.out.println(avg.variance());
		System.out.println(avg.standardDeviation());
	}

	private final double[] values;
	private final double[] tmpBuffer;

	private int valueCount = 0;
	private double mean = 0, min = 0, max = 0;
	private boolean calc = true;

	public WindowAverage(int windowLength) {
		this.values = new double[windowLength];
		this.tmpBuffer = new double[windowLength - 1];
	}

	public void addValue(double value) {
		if (this.valueCount == this.values.length) {
			System.arraycopy(this.values, 1, this.tmpBuffer, 0, this.tmpBuffer.length);
			this.values[this.values.length - 1] = value;
			System.arraycopy(this.tmpBuffer, 0, this.values, 0, this.tmpBuffer.length);
		} else {
			this.values[this.valueCount++] = value;
		}

		this.calc = true;
	}

	public double mean() {
		if (!hasEnoughData()) return 0;

		if (this.calc) {
			calculate();
			this.calc = false;
		}

		return this.mean;
	}

	public double variance() {
		if (!hasEnoughData()) return 0;

		final double mean = mean();
		double sum = 0;
		for (final double val : this.values) {
			final double diff = val - mean;
			sum += diff * diff;
		}

		return sum;
	}

	public double standardDeviation() {
		if (!hasEnoughData()) return 0;

		return Math.sqrt(variance());
	}

	public double max() {
		if (this.calc) {
			calculate();
			this.calc = false;
		}

		return this.max;
	}

	public double min() {
		if (this.calc) {
			calculate();
			this.calc = false;
		}

		return this.min;
	}

	public double getLeastRecent() {
		return this.values[0];
	}

	public double getMostRecent() {
		return this.values[this.values.length - 1];
	}

	public boolean hasEnoughData() {
		return this.valueCount >= this.values.length;
	}

	public void clear() {
		this.valueCount = 0;

		for (int i = 0; i < this.values.length; i++) {
			this.values[i] = 0;
		}

		this.calc = true;
	}

	private void calculate() {
		double sum = 0;
		double min = 0;
		double max = 0;

		for (final double val : this.values) {
			sum += val;

			min = Math.min(val, min);
			max = Math.max(val, max);
		}

		this.mean = sum / this.values.length;
		this.max = max;
		this.min = min;
	}

}
