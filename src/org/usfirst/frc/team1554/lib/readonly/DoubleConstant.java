package org.usfirst.frc.team1554.lib.readonly;

public class DoubleConstant implements ReadOnlyDouble {

    private final double value;

    public DoubleConstant(double value) {
        this.value = value;
    }

    @Override
    public int intValue() {
        return (int) Math.round(this.value);
    }

    @Override
    public long longValue() {
        return Math.round(this.value);
    }

    @Override
    public float floatValue() {
        return (float) this.value;
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
    public Double getValue() {
        return Double.valueOf(this.value);
    }

    @Override
    public double get() {
        return this.value;
    }

}
