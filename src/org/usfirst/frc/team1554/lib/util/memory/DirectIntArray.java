package org.usfirst.frc.team1554.lib.util.memory;

import org.usfirst.frc.team1554.lib.util.Preconditions;

import java.util.Iterator;

/**
 * Needs Documentation
 *
 * @author Matthew
 *         Created 2/23/2015 at 11:37 PM
 */
@SuppressWarnings({"ALL", "SameParameterValue"})
public class DirectIntArray implements Freeable, Iterable<Integer> {

    public static final int BYTES_SIZE = 4;

    private final MemoryAccess ma;

    public DirectIntArray(int size) {
        this.ma = MemoryAccess.allocateMemory(size * BYTES_SIZE);
    }

    private DirectIntArray(MemoryAccess access) {
        this.ma = access;
    }

    public boolean isUnsafe() {
        return ma.isUnsafe();
    }

    public int get(int index) {
        return ma.getInt(index * BYTES_SIZE);
    }

    public void set(int index, int value) {
        ma.putInt(index * BYTES_SIZE, value);
    }

    public int size() {
        return ma.length() / BYTES_SIZE;
    }

    @Override
    public void free() {
        ma.free();
    }

    @Override
    public DirectIntArray clone() {
        return new DirectIntArray(ma.makeCopy());
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<Integer> iterator() {
        return new DirectIntIterator(this);
    }

    class DirectIntIterator implements Iterator<Integer> {
        private final DirectIntArray arr;
        private final int size;
        private int index = 0;

        DirectIntIterator(DirectIntArray arr) {
            this.arr = arr;
            this.size = arr.size();
        }

        @Override
        public boolean hasNext() {
            return index < size;
        }

        @Override
        public Integer next() {
            Preconditions.checkElementIndex(index, size);

            return arr.get(index++);
        }

        @Override
        public void remove() {
            arr.set(index++, 0);
        }
    }
}
