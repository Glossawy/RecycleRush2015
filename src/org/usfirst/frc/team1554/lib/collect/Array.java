package org.usfirst.frc.team1554.lib.collect;

import org.usfirst.frc.team1554.lib.math.MathUtils;
import org.usfirst.frc.team1554.lib.util.Preconditions;
import org.usfirst.frc.team1554.lib.util.Predicate;
import org.usfirst.frc.team1554.lib.util.Predicate.PredicateIterable;

import java.util.Arrays;
import java.util.Iterator;

import static org.usfirst.frc.team1554.lib.util.ReflectionHelper.newArray;

public class Array<T> implements Iterable<T> {

    public static <T> Array<T> of(Class<T> arrayType) {
        return new Array<T>(arrayType);
    }

    public static <T> Array<T> of(boolean ordered, int capacity, Class<T> arrayType) {
        return new Array<T>(ordered, capacity, arrayType);
    }

    @SafeVarargs
    public static <T> Array<T> with(T... array) {
        return new Array<T>(array);
    }

    public T[] items;

    public int size;
    public boolean ordered;

    private ArrayIterable<T> iterable;
    private PredicateIterable<T> predicateIterable;

    public Array() {
        this(true, 16);
    }

    public Array(Class<? extends T> arrayType) {
        this(true, 16, arrayType);
    }

    @SuppressWarnings("unchecked")
    public Array(boolean ordered, int capacity) {
        this.ordered = ordered;
        this.items = (T[]) new Object[capacity];
    }

    @SuppressWarnings("unchecked")
    public Array(boolean ordered, int capacity, @SuppressWarnings("rawtypes") Class arrayType) {
        this.ordered = ordered;
        this.items = (T[]) newArray(arrayType, capacity);
    }

    public Array(Array<? extends T> array) {
        this(array.ordered, array.size, array.items.getClass().getComponentType());
        this.size = array.size;
        System.arraycopy(array.items, 0, this.items, 0, this.size);
    }

    public Array(T[] array) {
        this(true, array, 0, array.length);
    }

    public Array(boolean ordered, T[] arr, int start, int count) {
        this(ordered, count, arr.getClass().getComponentType());
        this.size = count;
        System.arraycopy(arr, start, this.items, 0, this.size);
    }

    public void add(T value) {
        if (this.size == this.items.length) {
            resize(Math.max(8, (int) (this.size * 1.75f)));
        }

        this.items[this.size++] = value;
    }

    public void addAll(Array<? extends T> array) {
        addAll(array, 0, array.size);
    }

    public void addAll(Array<? extends T> array, int start, int count) {
        if ((start + count) > array.size)
            throw new IllegalArgumentException("start + count MUST be <= array.size! Size: " + array.size);

        addAll(array.items, start, count);
    }

    @SuppressWarnings("unchecked")
    public void addAll(T... items) {
        addAll(items, 0, items.length);
    }

    public void addAll(T[] array, int start, int count) {
        final int requirement = this.size + count;
        if (this.size > this.items.length) {
            resize(Math.max(8, (int) (requirement * 1.75f)));
        }

        System.arraycopy(array, start, this.items, this.size, count);
        this.size += count;
    }

    public T get(int index) {
        Preconditions.checkElementIndex(index, this.size);

        return this.items[index];
    }

    public void set(int index, T value) {
        Preconditions.checkElementIndex(index, this.size);

        this.items[index] = value;
    }

    public void insert(int index, T value) {
        Preconditions.checkElementIndex(index, this.size + 1);

        if (this.size == this.items.length) {
            resize(Math.max(8, (int) (this.size * 1.75f)));
        }

        if (this.ordered) {
            System.arraycopy(this.items, index, this.items, index + 1, this.size - index);
        } else {
            this.items[this.size] = this.items[index];
        }

        this.size++;
        this.items[index] = value;
    }

    public void swap(int first, int second) {
        Preconditions.checkElementIndex(first, this.size);
        Preconditions.checkElementIndex(second, this.size);

        final T fVal = this.items[first];
        this.items[first] = this.items[second];
        this.items[second] = fVal;
    }

    public boolean contains(T val, boolean identityComparison) {
        if (identityComparison || (val == null)) {
            for (int i = 0; i < this.items.length; i++)
                if (this.items[i] == val) return true;
        } else {
            for (int i = 0; i < this.items.length; i++)
                if (this.items[i].equals(val)) return true;
        }

        return false;
    }

    public int indexOf(T value, boolean identityComparison) {
        if (identityComparison || (value == null)) {
            for (int i = 0; i < this.size; i++)
                if (this.items[i] == value) return i;
        } else {
            for (int i = 0; i < this.size; i++)
                if (this.items[i].equals(value)) return i;
        }

        return -1;
    }

    public int lastIndexOf(T value, boolean identityComparison) {
        if (identityComparison || (value == null)) {
            for (int i = this.size - 1; i >= 0; i--)
                if (this.items[i] == value) return i;
        } else {
            for (int i = this.size - 1; i >= 0; i--)
                if (this.items[i].equals(value)) return i;
        }

        return -1;
    }

    public boolean removeValue(T value, boolean identityComparison) {
        final int index = indexOf(value, identityComparison);

        if (index < 0) return false;

        removeIndex(index);
        return true;
    }

    public T removeIndex(int index) {
        Preconditions.checkElementIndex(index, this.size);
        final T value = this.items[index];

        this.size--;
        if (this.ordered) {
            System.arraycopy(this.items, index + 1, this.items, index, this.size - index);
        } else {
            this.items[index] = this.items[this.size];
        }

        this.items[this.size] = null;
        return value;
    }

    public void removeRange(int start, int end) {
        Preconditions.checkElementIndex(end, this.size);
        Preconditions.checkElementIndex(start, end + 1);

        final int count = (end - start) + 1;
        if (this.ordered) {
            System.arraycopy(this.items, start + count, this.items, start, this.size - (start + count));
        } else {
            final int last = this.size - 1;
            for (int i = 0; i < count; i++) {
                this.items[start + 1] = this.items[last - i];
            }
        }

        this.size -= count;
    }

    public boolean removeAll(Array<? extends T> array, boolean identityComparison) {
        final int startSize = this.size;
        if (identityComparison) {
            for (int i = 0; i < array.size; i++) {
                final T item = array.get(i);
                for (int j = 0; j < this.size; j++) {
                    if (item == this.items[j]) {
                        removeIndex(j);
                        this.size--;
                        break;
                    }
                }
            }
        } else {
            for (int i = 0; i < array.size; i++) {
                final T item = array.get(i);
                for (int j = 0; j < this.size; j++) {
                    if (item.equals(this.items[j])) {
                        removeIndex(j);
                        this.size--;
                        break;
                    }
                }
            }
        }

        return this.size != startSize;
    }

    public T pop() {
        Preconditions.checkState(this.size != 0, "No Elements to Pop!");

        final T item = this.items[--this.size];
        this.items[this.size] = null;
        return item;
    }

    public T peek() {
        Preconditions.checkState(this.size != 0, "No Elements to Peek!");

        return this.items[this.size - 1];
    }

    public T first() {
        Preconditions.checkState(this.size != 0, "No elements to Get!");

        return this.items[0];
    }

    public void clear() {
        for (int i = 0; i < this.size; i++) {
            this.items[i] = null;
        }

        this.size = 0;
    }

    public T[] shrink() {
        if (this.items.length != this.size) {
            resize(this.size);
        }

        return this.items;
    }

    public T[] ensureCapacity(int additionalCapacity) {
        final int newSize = this.size + additionalCapacity;
        if (newSize > this.items.length) {
            resize(Math.max(8, newSize));
        }

        return this.items;
    }

    @SuppressWarnings("unchecked")
    protected T[] resize(int newSize) {
        final T[] items = this.items;
        final T[] newItems = (T[]) newArray(items.getClass().getComponentType(), newSize);

        System.arraycopy(items, 0, newItems, 0, Math.min(this.size, newSize));
        this.items = newItems;
        return newItems;
    }

    // TODO Sort Methods

    // TODO Select Methods

    public void reverse() {
        for (int i = 0, last = this.size - 1, n = this.size / 2; i < n; i++) {
            final int j = last - i;
            final T temp = this.items[i];
            this.items[i] = this.items[j];
            this.items[j] = temp;
        }
    }

    public void shuffle() {
        for (int i = this.size - 1; i >= 0; i--) {
            final int j = MathUtils.random(i);
            final T temp = this.items[i];
            this.items[i] = this.items[j];
            this.items[j] = temp;
        }
    }

    @Override
    public Iterator<T> iterator() {
        if (this.iterable == null) {
            this.iterable = new ArrayIterable<T>(this);
        }

        return this.iterable.iterator();
    }

    public Iterable<T> select(Predicate<T> predicate) {
        if (this.predicateIterable == null) {
            this.predicateIterable = new PredicateIterable<>(this.iterable, predicate);
        } else {
            this.predicateIterable.set(this.iterable, predicate);
        }

        return this.predicateIterable;
    }

    public void truncate(int newSize) {
        if (this.size <= newSize) return;

        for (int i = newSize; i < this.size; i++) {
            this.items[i] = null;
        }

        this.size = newSize;
    }

    public T random() {
        Preconditions.checkState(this.size != 0, "No Elements to Select From!");

        return this.items[MathUtils.random(this.size - 1)];
    }

    @SuppressWarnings("unchecked")
    public T[] toArray() {
        return (T[]) toArray(this.items.getClass().getComponentType());
    }

    public <V> V[] toArray(Class<V> type) {
        final V[] result = newArray(type, this.size);
        System.arraycopy(this.items, 0, result, 0, this.size);
        return result;
    }

    @Override
    public int hashCode() {
        int result = 17;

        result = (37 * result) + this.size;
        result = (37 * result) + (this.items != null ? Arrays.hashCode(this.items) : 0);

        return result;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) return true;
        if (!(object instanceof Array)) return false;

        final Array<?> arr = (Array<?>) object;
        if (this.size != arr.size) return false;

        final Object[] i1 = this.items;
        final Object[] i2 = arr.items;

        for (int i = 0; i < this.size; i++) {
            final Object o1 = i1[i];
            final Object o2 = i2[i];

            if (!(o1 == null ? o2 == null : o1.equals(o2))) return false;
        }

        return true;
    }

    @Override
    public String toString() {
        if (this.size == 0) return "[]";

        final StringBuilder sb = new StringBuilder();
        sb.append('[');
        sb.append(this.items[0]);

        for (int i = 0; i < this.size; i++) {
            sb.append(", ").append(this.items[i]);
        }

        sb.append(']');
        return sb.toString();
    }

    public static class ArrayIterator<T> implements Iterator<T>, Iterable<T> {
        private final Array<T> array;
        private final boolean allowRemove;
        int index;
        boolean valid = true;

        public ArrayIterator(Array<T> array) {
            this(array, true);
        }

        public ArrayIterator(Array<T> array, boolean allowRemove) {
            this.array = array;
            this.allowRemove = allowRemove;
        }

        @Override
        public boolean hasNext() {
            Preconditions.checkState(this.valid, "#iterator() cannot be used nested.");

            return this.index < this.array.size;
        }

        @Override
        public T next() {
            Preconditions.checkElementIndex(this.index, this.array.size, String.valueOf(this.index));
            Preconditions.checkState(this.valid, "#iterator() cannot be used nested.");

            return this.array.items[this.index++];
        }

        @Override
        public void remove() {
            Preconditions.checkState(this.allowRemove, "#remove() is Disallowed");

            this.index--;
            this.array.removeIndex(this.index);
        }

        public void reset() {
            this.index = 0;
        }

        @Override
        public Iterator<T> iterator() {
            return this;
        }
    }

    public static class ArrayIterable<T> implements Iterable<T> {
        private final Array<T> array;
        private final boolean allowRemove;
        private ArrayIterator<T> iterator1, iterator2;

        public ArrayIterable(Array<T> array) {
            this(array, true);
        }

        public ArrayIterable(Array<T> array, boolean allowRemove) {
            this.array = array;
            this.allowRemove = allowRemove;
        }

        @Override
        public Iterator<T> iterator() {
            if (this.iterator1 == null) {
                this.iterator1 = new ArrayIterator<T>(this.array, this.allowRemove);
                this.iterator2 = new ArrayIterator<T>(this.array, this.allowRemove);
            }
            if (!this.iterator1.valid) {
                this.iterator1.index = 0;
                this.iterator1.valid = true;
                this.iterator2.valid = false;
                return this.iterator1;
            }
            this.iterator2.index = 0;
            this.iterator2.valid = true;
            this.iterator1.valid = false;
            return this.iterator2;
        }
    }
}
