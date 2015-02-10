package org.usfirst.frc.team1554.lib.readonly;

public class BooleanConstant implements ReadOnlyBoolean {

	private final boolean value;

	public BooleanConstant(boolean value) {
		this.value = value;
	}

	@Override
	public void addListener(ChangeListener<? super Boolean> listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeListener(ChangeListener<? super Boolean> listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean get() {
		return this.value;
	}

	@Override
	public Boolean getValue() {
		return Boolean.valueOf(this.value);
	}

}
