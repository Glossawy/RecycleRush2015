package org.usfirst.frc.team1554.lib.readonly;

public interface ReadOnlyNumber extends ReadOnlyValue<Number> {

    int intValue();

    long longValue();

    float floatValue();

    double doubleValue();

}
