package org.usfirst.frc.team1554.lib.readonly;

public interface ReadOnlyValue<T> {

	void addListener(ChangeListener<? super T> listener);

	void removeListener(ChangeListener<? super T> listener);

	T getValue();

}
