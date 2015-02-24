package org.usfirst.frc.team1554.lib.util.memory;

import org.usfirst.frc.team1554.lib.util.Preconditions;

import static org.usfirst.frc.team1554.lib.util.memory.UnsignedConstants.*;

/**
 * Created by Matthew on 2/23/2015.
 */
class BitShiftByteHandler extends ByteHandler {

    @Override
    public boolean isUnsafe() {
        return false;
    }

    @Override
    public byte getByte(byte[] data, int offset) {
        return data[offset];
    }

    @Override
    public void putByte(byte[] data, int offset, byte val) {
        data[offset] = val;
    }

    @Override
    public short getUnsignedByte(byte[] data, int offset) {
        return (short) (data[offset] & BYTE_TO_UNSIGNED_BYTE);
    }

    @Override
    public void putUnsignedByte(byte[] data, int offset, short val) {
        Preconditions.checkExpression(val >= 0 && val < MAX_UNSIGNED_BYTE, "Unsigned Byte must be between 0 and " + MAX_UNSIGNED_BYTE);

        data[offset] = (byte) val;
    }

    @Override
    public short getShort(byte[] data, int offset) {
        return (short) ((data[offset + 0] & BYTE_TO_UNSIGNED_BYTE) |
                (data[offset + 1] & BYTE_TO_UNSIGNED_BYTE << BYTE_SIZE));
    }

    @Override
    public void putShort(byte[] data, int offset, short val) {
        data[offset + 0] = (byte) (val);
        data[offset + 1] = (byte) (val >>> BYTE_SIZE);
    }

    @Override
    public int getUnsignedShort(byte[] data, int offset) {
        return (((data[offset + 0] & BYTE_TO_UNSIGNED_BYTE) << BYTE_SIZE * 0) |
                ((data[offset + 1] & BYTE_TO_UNSIGNED_BYTE) << BYTE_SIZE * 1))
                & SHORT_TO_UNSIGNED_SHORT;
    }

    @Override
    public void putUnsignedShort(byte[] data, int offset, int val) {
        Preconditions.checkExpression(val >= 0 && val < MAX_UNSIGNED_SHORT, "Unsigned Short must be between 0 and " + MAX_UNSIGNED_SHORT);

        data[offset + 0] = (byte) (val >>> BYTE_SIZE * 0);
        data[offset + 1] = (byte) (val >>> BYTE_SIZE * 1);
    }

    @Override
    public int getInt(byte[] data, int offset) {
        return (data[offset + 0] & BYTE_TO_UNSIGNED_BYTE) << BYTE_SIZE * 0 |
                (data[offset + 1] & BYTE_TO_UNSIGNED_BYTE) << BYTE_SIZE * 1 |
                (data[offset + 2] & BYTE_TO_UNSIGNED_BYTE) << BYTE_SIZE * 2 |
                (data[offset + 3] & BYTE_TO_UNSIGNED_BYTE) << BYTE_SIZE * 3;
    }

    @Override
    public void putInt(byte[] data, int offset, int value) {
        data[offset + 0] = (byte) (value >>> BYTE_SIZE * 0);
        data[offset + 1] = (byte) (value >>> BYTE_SIZE * 1);
        data[offset + 2] = (byte) (value >>> BYTE_SIZE * 2);
        data[offset + 3] = (byte) (value >>> BYTE_SIZE * 3);
    }

    @Override
    public long getUnsignedInt(byte[] data, int offset) {
        return ((data[offset + 0] & BYTE_TO_UNSIGNED_BYTE) << BYTE_SIZE * 0 |
                (data[offset + 1] & BYTE_TO_UNSIGNED_BYTE) << BYTE_SIZE * 1 |
                (data[offset + 2] & BYTE_TO_UNSIGNED_BYTE) << BYTE_SIZE * 2 |
                (data[offset + 3] & BYTE_TO_UNSIGNED_BYTE) << BYTE_SIZE * 3) & INT_TO_UNSIGNED_INT;
    }

    @Override
    public void putUnsignedInt(byte[] data, int offset, long value) {
        Preconditions.checkExpression(value >= 0 && value < MAX_UNSIGNED_INT, "Unsigned Int must be between 0 and " + MAX_UNSIGNED_INT);

        data[offset + 0] = (byte) (value >>> BYTE_SIZE * 0);
        data[offset + 1] = (byte) (value >>> BYTE_SIZE * 1);
        data[offset + 2] = (byte) (value >>> BYTE_SIZE * 2);
        data[offset + 3] = (byte) (value >>> BYTE_SIZE * 3);
    }

    @Override
    public long getLong(byte[] data, int offset) {
        return (data[offset + 0] & BYTE_TO_UNSIGNED_BYTE) << BYTE_SIZE * 0 |
                (data[offset + 1] & BYTE_TO_UNSIGNED_BYTE) << BYTE_SIZE * 1 |
                (data[offset + 2] & BYTE_TO_UNSIGNED_BYTE) << BYTE_SIZE * 2 |
                (data[offset + 3] & BYTE_TO_UNSIGNED_BYTE) << BYTE_SIZE * 3 |
                (data[offset + 4] & BYTE_TO_UNSIGNED_BYTE) << BYTE_SIZE * 4 |
                (data[offset + 5] & BYTE_TO_UNSIGNED_BYTE) << BYTE_SIZE * 5 |
                (data[offset + 6] & BYTE_TO_UNSIGNED_BYTE) << BYTE_SIZE * 6 |
                (data[offset + 7] & BYTE_TO_UNSIGNED_BYTE) << BYTE_SIZE * 7;
    }

    @Override
    public void putLong(byte[] data, int offset, long value) {
        data[offset + 0] = (byte) (value >>> BYTE_SIZE * 0);
        data[offset + 1] = (byte) (value >>> BYTE_SIZE * 1);
        data[offset + 2] = (byte) (value >>> BYTE_SIZE * 2);
        data[offset + 3] = (byte) (value >>> BYTE_SIZE * 3);
        data[offset + 4] = (byte) (value >>> BYTE_SIZE * 4);
        data[offset + 5] = (byte) (value >>> BYTE_SIZE * 5);
        data[offset + 6] = (byte) (value >>> BYTE_SIZE * 6);
        data[offset + 7] = (byte) (value >>> BYTE_SIZE * 7);
    }

    @Override
    public void copy(byte[] src, int srcOffset, byte[] dst, int dstOffset, int length) {
        System.arraycopy(src, srcOffset, dst, dstOffset, length);
    }
}
