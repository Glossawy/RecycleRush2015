package org.usfirst.frc.team1554.lib.readonly;

public class FloatConstant implements ReadOnlyFloat {

    private final float value;

    public FloatConstant(float value) {
        this.value = value;
    }

    @Override
    public int intValue() {
        return Math.round(this.value);
    }

    @Override
    public long longValue() {
        return Math.round(this.value);
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
    public void addListener(ChangeListener<? super Number> listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeListener(ChangeListener<? super Number> listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public Float getValue() {
        return this.value;
    }

    @Override
    public float get() {
        return this.value;
    }

}
