package org.usfirst.frc.team1554.lib.util.memory;

import org.usfirst.frc.team1554.lib.util.Preconditions;
import org.usfirst.frc.team1554.lib.util.ReflectionHelper;
import sun.misc.Unsafe;

import static org.usfirst.frc.team1554.lib.util.memory.UnsignedConstants.*;

/**
 * Created by Matthew on 2/23/2015.
 */
class UnsafeByteHandler extends ByteHandler {

    private static final Unsafe UNSAFE = ReflectionHelper.getStaticField(Unsafe.class, "theUnsafe", Unsafe.class);
    private static final long BYTE_ARRAY_OFFSET;

    static {
        int bas = UNSAFE.arrayBaseOffset(byte[].class);
        // Method Test
        UNSAFE.copyMemory(new byte[1], bas, new byte[1], bas, 1);
        BYTE_ARRAY_OFFSET = bas;
    }

    @Override
    public boolean isUnsafe() {
        return true;
    }

    @Override
    public byte getByte(byte[] data, int offset) {
        Preconditions.checkElementIndex(offset, data.length);

        return UNSAFE.getByte(data, BYTE_ARRAY_OFFSET + offset);
    }

    @Override
    public void putByte(byte[] data, int offset, byte val) {
        Preconditions.checkElementIndex(offset, data.length);

        UNSAFE.putByte(data, BYTE_ARRAY_OFFSET + offset, val);
    }

    @Override
    public short getUnsignedByte(byte[] data, int offset) {
        Preconditions.checkElementIndex(offset, data.length);

        return (short) (UNSAFE.getByte(data, BYTE_ARRAY_OFFSET + offset) & BYTE_TO_UNSIGNED_BYTE);
    }

    @Override
    public void putUnsignedByte(byte[] data, int offset, short val) {
        Preconditions.checkElementIndex(offset, data.length);
        Preconditions.checkExpression(val >= 0 && val < MAX_UNSIGNED_BYTE, "Unsigned Bytes must be between 0 and " + MAX_UNSIGNED_BYTE);

        UNSAFE.putByte(data, BYTE_ARRAY_OFFSET + offset, (byte) val);
    }

    @Override
    public short getShort(byte[] data, int offset) {
        Preconditions.checkElementIndex(offset, data.length - 1);

        return UNSAFE.getShort(data, BYTE_ARRAY_OFFSET + offset);
    }

    @Override
    public void putShort(byte[] data, int offset, short val) {
        Preconditions.checkElementIndex(offset, data.length - 1);

        UNSAFE.putShort(data, BYTE_ARRAY_OFFSET + offset, val);
    }

    @Override
    public int getUnsignedShort(byte[] data, int offset) {
        Preconditions.checkElementIndex(offset, data.length - 1);

        return UNSAFE.getShort(data, BYTE_ARRAY_OFFSET + offset) & SHORT_TO_UNSIGNED_SHORT;
    }

    @Override
    public void putUnsignedShort(byte[] data, int offset, int val) {
        Preconditions.checkElementIndex(offset, data.length - 1);
        Preconditions.checkExpression(val >= 0 && val < MAX_UNSIGNED_SHORT, "Unsigned Short must be between 0 and " + MAX_UNSIGNED_SHORT);

        UNSAFE.putShort(data, BYTE_ARRAY_OFFSET + offset, (short) val);
    }

    @Override
    public int getInt(byte[] data, int offset) {
        Preconditions.checkElementIndex(offset, data.length - 3);

        return UNSAFE.getInt(data, BYTE_ARRAY_OFFSET + offset);
    }

    @Override
    public void putInt(byte[] data, int offset, int value) {
        Preconditions.checkElementIndex(offset, data.length - 3);

        UNSAFE.putInt(data, BYTE_ARRAY_OFFSET + offset, value);
    }

    @Override
    public long getUnsignedInt(byte[] data, int offset) {
        Preconditions.checkElementIndex(offset, data.length - 3);

        return UNSAFE.getInt(data, BYTE_ARRAY_OFFSET + offset) & INT_TO_UNSIGNED_INT;
    }

    @Override
    public void putUnsignedInt(byte[] data, int offset, long value) {
        Preconditions.checkElementIndex(offset, data.length - 3);
        Preconditions.checkExpression(value >= 0 && value < MAX_UNSIGNED_INT, "Unsigned Int Must be between Zero and " + MAX_UNSIGNED_INT);

        UNSAFE.putInt(data, BYTE_ARRAY_OFFSET + offset, (int) value);
    }

    @Override
    public long getLong(byte[] data, int offset) {
        Preconditions.checkElementIndex(offset, data.length);

        return UNSAFE.getLong(data, BYTE_ARRAY_OFFSET + offset);
    }

    @Override
    public void putLong(byte[] data, int offset, long value) {
        Preconditions.checkElementIndex(offset, data.length);

        UNSAFE.putLong(data, BYTE_ARRAY_OFFSET + offset, value);
    }

    @Override
    public void copy(byte[] src, int srcOffset, byte[] dst, int dstOffset, int length) {
        Preconditions.checkElementIndex(srcOffset, src.length - length);
        Preconditions.checkElementIndex(dstOffset, dst.length - length);

        UNSAFE.copyMemory(src, BYTE_ARRAY_OFFSET + srcOffset, dst, BYTE_ARRAY_OFFSET + dstOffset, length);
    }
}
