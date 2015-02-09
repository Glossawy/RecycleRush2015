package org.usfirst.frc.team1554.lib.value;

public interface ReadOnlyValue<T> {

	void addListener(ChangeListener<? extends T> listener);

	void removeListener(ChangeListener<? extends T> listener);

	T get();

}
