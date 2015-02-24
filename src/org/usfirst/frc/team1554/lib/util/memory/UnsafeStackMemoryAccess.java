package org.usfirst.frc.team1554.lib.util.memory;

import org.usfirst.frc.team1554.lib.util.Preconditions;
import org.usfirst.frc.team1554.lib.util.ReflectionHelper;
import sun.misc.Unsafe;

import static org.usfirst.frc.team1554.lib.util.memory.UnsignedConstants.*;

/**
 * Needs Documentation
 *
 * @author Matthew
 *         Created 2/23/2015 at 6:05 PM
 */
class UnsafeStackMemoryAccess extends MemoryAccess {

    private static final Unsafe unsafe = ReflectionHelper.getStaticField(Unsafe.class, "theUnsafe", Unsafe.class);
    private static final int BASE_OFFSET = unsafe.arrayBaseOffset(byte[].class);

    private final long address;

    UnsafeStackMemoryAccess(int bytes) {
        super(bytes);

        address = unsafe.allocateMemory(bytes);
    }

    @Override
    public boolean isUnsafe() {
        return true;
    }

    @Override
    public void put(int memoryOffset, byte[] buffer, int bufferOffset, int bytes) {
        performCheck();
        Preconditions.checkElementIndex(memoryOffset, length() - bytes - 1);
        Preconditions.checkElementIndex(bufferOffset, buffer.length - bytes - 1);
        Preconditions.checkExpression(bytes > 0 && bytes <= length());
        Preconditions.checkNotNull(buffer);

        unsafe.copyMemory(buffer, BASE_OFFSET + bufferOffset, null, address + memoryOffset, bytes);
    }

    @Override
    public void put(int memoryOffset, byte[] buffer) {
        performCheck();
        Preconditions.checkElementIndex(memoryOffset, length() - buffer.length - 1);
        Preconditions.checkExpression(buffer.length <= length());
        Preconditions.checkNotNull(buffer);

        unsafe.copyMemory(buffer, BASE_OFFSET, null, address + memoryOffset, buffer.length);
    }

    @Override
    public void get(int memoryOffset, byte[] buffer, int bufferOffset, int bytes) {
        performCheck();
        Preconditions.checkElementIndex(memoryOffset, length() - bytes - 1);
        Preconditions.checkElementIndex(bufferOffset, buffer.length - bytes - 1);
        Preconditions.checkExpression(bytes > 0 && bytes <= length());
        Preconditions.checkNotNull(buffer);

        unsafe.copyMemory(null, address + memoryOffset, buffer, BASE_OFFSET + bufferOffset, bytes);
    }

    @Override
    public void get(int memoryOffset, byte[] buffer) {
        performCheck();
        Preconditions.checkElementIndex(memoryOffset, length() - buffer.length - 1);
        Preconditions.checkExpression(buffer.length <= length());
        Preconditions.checkNotNull(buffer);

        unsafe.copyMemory(null, address + memoryOffset, buffer, BASE_OFFSET, buffer.length);
    }

    @Override
    public byte getByte(int offset) {
        performCheck();
        Preconditions.checkElementIndex(offset, length());

        return unsafe.getByte(address + offset);
    }

    @Override
    public void putByte(int offset, byte val) {
        performCheck();
        Preconditions.checkElementIndex(offset, length());

        unsafe.putByte(address + offset, val);
    }

    @Override
    public short getUnsignedByte(int offset) {
        performCheck();
        Preconditions.checkElementIndex(offset, length());

        return (short) (unsafe.getByte(address + offset) & BYTE_TO_UNSIGNED_BYTE);
    }

    @Override
    public void putUnsignedByte(int offset, short val) {
        performCheck();
        Preconditions.checkElementIndex(offset, length());
        Preconditions.checkExpression(val >= 0 && val < MAX_UNSIGNED_BYTE, "Unsigned Byte must be between 0 and " + MAX_UNSIGNED_BYTE);

        unsafe.putByte(address + offset, (byte) val);
    }

    @Override
    public short getShort(int offset) {
        performCheck();
        Preconditions.checkElementIndex(offset, length() - 1);

        return unsafe.getShort(address + offset);
    }

    @Override
    public void putShort(int offset, short val) {
        performCheck();
        Preconditions.checkElementIndex(offset, length() - 1);

        unsafe.putShort(address + offset, val);
    }

    @Override
    public int getUnsignedShort(int offset) {
        performCheck();
        Preconditions.checkElementIndex(offset, length() - 1);

        return unsafe.getShort(address + offset) & SHORT_TO_UNSIGNED_SHORT;
    }

    @Override
    public void putUnsignedShort(int offset, int val) {
        performCheck();
        Preconditions.checkElementIndex(offset, length() - 1);
        Preconditions.checkExpression(val >= 0 && val < MAX_UNSIGNED_SHORT, "Unsigned Short must be between 0 and " + MAX_UNSIGNED_SHORT);

        unsafe.putShort(address + offset, (short) val);
    }

    @Override
    public int getInt(int offset) {
        performCheck();
        Preconditions.checkElementIndex(offset, length() - 3);

        return unsafe.getInt(address + offset);
    }

    @Override
    public void putInt(int offset, int value) {
        performCheck();
        Preconditions.checkElementIndex(offset, length() - 3);

        unsafe.putInt(address + offset, value);
    }

    @Override
    public long getUnsignedInt(int offset) {
        performCheck();
        Preconditions.checkElementIndex(offset, length() - 3);

        return unsafe.getInt(address + offset) & INT_TO_UNSIGNED_INT;
    }

    @Override
    public void putUnsignedInt(int offset, long val) {
        performCheck();
        Preconditions.checkElementIndex(offset, length() - 3);
        Preconditions.checkExpression(val >= 0 && val < MAX_UNSIGNED_INT, "Unsigned Int must be between 0 and " + MAX_UNSIGNED_INT);

        unsafe.putInt(address + offset, (int) val);
    }

    @Override
    public long getLong(int offset) {
        performCheck();
        Preconditions.checkElementIndex(offset, length() - 7);

        return unsafe.getLong(address + offset);
    }

    @Override
    public void putLong(int offset, long value) {
        performCheck();
        Preconditions.checkElementIndex(offset, length() - 7);

        unsafe.putLong(address + offset, value);
    }

    @Override
    public void copy(int offset, MemoryAccess dest, int destOffset, int length) {
        Preconditions.checkState(!isDisposed(), "Cannot manipulate freed memory!");
        Preconditions.checkState(!dest.isDisposed(), "Cannot manipulate freed memory!");
        Preconditions.checkElementIndex(offset, length() - length);
        Preconditions.checkElementIndex(destOffset, dest.length() - length);

        if (dest instanceof UnsafeStackMemoryAccess) {
            unsafe.copyMemory(address + offset, ((UnsafeStackMemoryAccess) dest).address + destOffset, length);
        } else {
            byte[] arr = new byte[length];
            unsafe.copyMemory(null, address + offset, arr, unsafe.arrayBaseOffset(byte[].class), length);
            dest.put(destOffset, arr);
        }
    }

    @Override
    public MemoryAccess clone() {
        performCheck();
        UnsafeStackMemoryAccess mem = new UnsafeStackMemoryAccess(length());
        unsafe.copyMemory(address, mem.address, length());

        return mem;
    }

    @Override
    protected void freeMemory() {
        unsafe.freeMemory(address);
    }

    private void performCheck() {
        Preconditions.checkState(!isDisposed(), "Cannot access freed UnsafeStackMemory!");
    }
}
