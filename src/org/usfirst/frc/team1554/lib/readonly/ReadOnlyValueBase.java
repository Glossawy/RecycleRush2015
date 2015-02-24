package org.usfirst.frc.team1554.lib.readonly;

public abstract class ReadOnlyValueBase<T> implements ReadOnlyValue<T> {

    private ReadOnlyValueHelper<T> helper;

    @Override
    public void addListener(ChangeListener<? super T> listener) {
        this.helper = ReadOnlyValueHelper.addListener(this.helper, this, listener);
    }

    @Override
    public void removeListener(ChangeListener<? super T> listener) {
        this.helper = ReadOnlyValueHelper.removeListener(this.helper, this, listener);
    }

    @Override
    abstract public T getValue();

    protected void fireChangeEvent() {
        ReadOnlyValueHelper.fireChangeEvent(this.helper);
    }

}
