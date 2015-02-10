package org.usfirst.frc.team1554.lib.readonly;

public class LongConstant implements ReadOnlyLong {

	private final long value;

	public LongConstant(long value) {
		this.value = value;
	}

	@Override
	public int intValue() {
		return (int) this.value;
	}

	@Override
	public long longValue() {
		return this.value;
	}

	@Override
	public float floatValue() {
		return this.value * 1.0f;
	}

	@Override
	public double doubleValue() {
		return this.value;
	}

	@Override
	public void addListener(ChangeListener<? super Number> listener) {
		// TODO Auto-generated method stub
	}

	@Override
	public void removeListener(ChangeListener<? super Number> listener) {
		// TODO Auto-generated method stub
	}

	@Override
	public Long getValue() {
		return Long.valueOf(this.value);
	}

	@Override
	public long get() {
		return this.value;
	}

}
