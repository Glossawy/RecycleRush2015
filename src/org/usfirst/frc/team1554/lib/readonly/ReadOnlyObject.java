package org.usfirst.frc.team1554.lib.readonly;

public interface ReadOnlyObject<T> extends ReadOnlyValue<T> {

    T get();

}
