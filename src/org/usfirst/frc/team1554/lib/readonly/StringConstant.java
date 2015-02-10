package org.usfirst.frc.team1554.lib.readonly;

public class StringConstant implements ReadOnlyString {

	private final String value;

	public StringConstant(String str) {
		this.value = str;
	}

	@Override
	public String get() {
		return this.value;
	}

	@Override
	public String getValue() {
		return this.value;
	}

	@Override
	public void addListener(ChangeListener<? super String> listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeListener(ChangeListener<? super String> listener) {
		// TODO Auto-generated method stub

	}

}
