package org.usfirst.frc.team1554.lib.readonly;

import org.usfirst.frc.team1554.lib.common.Console;
import org.usfirst.frc.team1554.lib.util.Preconditions;
import org.usfirst.frc.team1554.lib.util.RoboUtils;

import java.util.Arrays;

// Look at ExpressionHelper in javafx.beans.value.ExpressionHelper<T>fc
public abstract class ReadOnlyValueHelper<T> {

    public static <T> ReadOnlyValueHelper<T> addListener(ReadOnlyValueHelper<T> helper, ReadOnlyValue<T> value, ChangeListener<? super T> listener) {
        Preconditions.checkNotNull(value);
        Preconditions.checkNotNull(listener);

        value.getValue();

        return helper == null ? new ChangeHelper<>(value, listener) : helper.addListener(listener);
    }

    public static <T> ReadOnlyValueHelper<T> removeListener(ReadOnlyValueHelper<T> helper, ReadOnlyValue<T> value, ChangeListener<? super T> listener) {
        Preconditions.checkNotNull(listener);
        return helper == null ? null : helper.removeListener(listener);
    }

    public static <T> void fireChangeEvent(ReadOnlyValueHelper<T> helper) {
        if (helper == null) return;

        helper.fireChangeEvent();
    }

    protected final ReadOnlyValue<T> readOnlyVal;

    private ReadOnlyValueHelper(ReadOnlyValue<T> val) {
        this.readOnlyVal = val;
    }

    protected abstract ReadOnlyValueHelper<T> addListener(ChangeListener<? super T> listener);

    protected abstract ReadOnlyValueHelper<T> removeListener(ChangeListener<? super T> listener);

    protected abstract void fireChangeEvent();

    public static class ChangeHelper<T> extends ReadOnlyValueHelper<T> {

        private final ChangeListener<? super T> listener;
        private T currentValue;

        private ChangeHelper(ReadOnlyValue<T> value, ChangeListener<? super T> listener) {
            super(value);

            this.listener = listener;
            this.currentValue = value.getValue();
        }

        @Override
        protected ReadOnlyValueHelper<T> addListener(ChangeListener<? super T> listener) {
            return new MultiChange<>(this.readOnlyVal, this.listener, listener);
        }

        @Override
        protected ReadOnlyValueHelper<T> removeListener(ChangeListener<? super T> listener) {
            return this;
        }

        @Override
        protected void fireChangeEvent() {
            final T oldValue = this.currentValue;
            this.currentValue = this.readOnlyVal.getValue();

            final boolean changed = this.currentValue == null ? oldValue != null : !this.currentValue.equals(oldValue);

            if (changed) {
                try {
                    this.listener.onChange(this.readOnlyVal, oldValue, this.currentValue);
                } catch (final Exception e) {
                    Console.exception(e);
                    RoboUtils.exceptionToDS(e);
                }
            }
        }
    }

    public static class MultiChange<T> extends ReadOnlyValueHelper<T> {

        private ChangeListener<? super T> listeners[];
        private int changeSize = 0;
        private T currentValue;

        @SafeVarargs
        private MultiChange(ReadOnlyValue<T> value, ChangeListener<? super T>... listeners) {
            super(value);

            this.listeners = listeners;
            this.changeSize = listeners.length;

            this.currentValue = value.getValue();
        }

        private MultiChange(ChangeHelper<T> helper) {
            this(helper.readOnlyVal, helper.listener);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected synchronized ReadOnlyValueHelper<T> addListener(ChangeListener<? super T> listener) {
            if (this.listeners == null) {
                this.listeners = new ChangeListener[]{listener};
                this.changeSize = 1;
            } else {
                if (this.changeSize == this.listeners.length) {
                    final int newSize = ((this.listeners.length * 3) / 2) + 1;
                    this.listeners = Arrays.copyOf(this.listeners, newSize);
                }

                this.listeners[this.changeSize++] = listener;
            }

            return this;
        }

        @Override
        protected synchronized ReadOnlyValueHelper<T> removeListener(ChangeListener<? super T> listener) {
            if (this.listeners != null) {
                for (int i = 0; i < this.changeSize; i++) {
                    if (listener.equals(this.listeners[i])) {
                        System.arraycopy(this.listeners, i + 1, this.listeners, i, this.changeSize - i - 1);
                        this.changeSize--;
                        break;
                    }
                }
            }

            return this;
        }

        @Override
        protected void fireChangeEvent() {
            ChangeListener<? super T> curChange[];
            int curChangeSize;
            synchronized (this) {
                curChange = this.listeners;
                curChangeSize = this.changeSize;
            }

            if (curChangeSize > 0) {
                final T oldValue = this.currentValue;
                this.currentValue = this.readOnlyVal.getValue();
                final boolean changed = this.currentValue == null ? oldValue != null : !this.currentValue.equals(oldValue);

                if (changed) {
                    synchronized (this) {
                        for (int i = 0; i < curChangeSize; i++) {
                            try {
                                curChange[i].onChange(this.readOnlyVal, oldValue, this.currentValue);
                            } catch (final Exception e) {
                                Console.exception(e);
                                RoboUtils.exceptionToDS(e);
                            }
                        }
                    }
                }
            }
        } // All of the closing brackets!

    }

}
