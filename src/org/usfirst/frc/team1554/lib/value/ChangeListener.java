package org.usfirst.frc.team1554.lib.value;

@FunctionalInterface
public interface ChangeListener<T> {

	void onChange(ReadOnlyValue<T> current, T oldValue, T newValue);

}
