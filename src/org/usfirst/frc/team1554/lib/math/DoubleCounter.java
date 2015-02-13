package org.usfirst.frc.team1554.lib.math;

public class DoubleCounter {

	public static class ObservableValue<T> {
		private T val;

		private ObservableValue(T val) {
			this.val = val;
		}

		public T get() {
			return this.val;
		}

		void set(ObservableValue<T> val) {
			this.val = val.get();
		}

		void set(T val) {
			this.val = val;
		}
	}

	public final ObservableValue<Integer> count;
	public final ObservableValue<Double> total, average;
	public final ObservableValue<Double> min, max, latest, value;
	public final ObservableValue<Boolean> isWindowed = new ObservableValue<Boolean>(false);

	private final WindowAverage avg;

	public DoubleCounter(int windowSize) {
		this.avg = windowSize > 1 ? new WindowAverage(windowSize) : null;

		this.count = new ObservableValue<Integer>(0);
		this.total = new ObservableValue<Double>(0.0);
		this.min = new ObservableValue<Double>(Double.MIN_VALUE);
		this.max = new ObservableValue<Double>(Double.MAX_VALUE);
		this.average = new ObservableValue<Double>(0.0);
		this.latest = new ObservableValue<Double>(0.0);
		this.value = new ObservableValue<Double>(0.0);

		if (this.avg != null) {
			this.avg.clear();
		}
	}

	public void put(double value) {
		this.latest.set(value);
		this.total.set(this.total.get() + value);
		this.count.set(this.count.get() + 1);

		this.average.set(this.total.get() / this.count.get());

		if (this.avg != null) {
			this.avg.addValue(value);
			this.value.set(this.avg.mean());
		} else {
			this.value.set(this.latest);
		}

		if (this.avg == null || this.avg.hasEnoughData()) {
			this.min.set(Math.min(this.min.get(), this.value.get()));
			this.max.set(Math.max(this.max.get(), this.value.get()));
		}
	}

	public void reset() {
		this.count.set(0);
		this.total.set(0.0);
		this.min.set(Double.MIN_VALUE);
		this.max.set(Double.MAX_VALUE);
		this.average.set(0.0);
		this.latest.set(0.0);
		this.value.set(0.0);

		if (this.avg != null) {
			this.avg.clear();
		}
	}
}
