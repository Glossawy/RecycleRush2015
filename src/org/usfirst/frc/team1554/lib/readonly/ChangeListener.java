package org.usfirst.frc.team1554.lib.readonly;

@FunctionalInterface
public interface ChangeListener<T> {

	void onChange(ReadOnlyValue<? extends T> current, T oldValue, T newValue);

}
