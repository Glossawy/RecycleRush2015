package org.usfirst.frc.team1554.lib.readonly;

public class IntegerConstant implements ReadOnlyInteger {

	private final int value;

	public IntegerConstant(int value) {
		this.value = value;
	}

	@Override
	public int intValue() {
		return this.value;
	}

	@Override
	public long longValue() {
		return this.value;
	}

	@Override
	public float floatValue() {
		return this.value;
	}

	@Override
	public double doubleValue() {
		return this.value;
	}

	@Override
	public int get() {
		return this.value;
	}

	@Override
	public Integer getValue() {
		return Integer.valueOf(this.value);
	}

	@Override
	public void addListener(ChangeListener<? super Number> listener) {
		// TODO Auto-generated method stub
	}

	@Override
	public void removeListener(ChangeListener<? super Number> listener) {
		// TODO Auto-generated method stub
	}

}
