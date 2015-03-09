package org.usfirst.frc.team1554.lib.util.memory;

import org.usfirst.frc.team1554.lib.util.BufferUtils;
import org.usfirst.frc.team1554.lib.util.Preconditions;

import java.nio.ByteBuffer;

import static org.usfirst.frc.team1554.lib.util.memory.UnsignedConstants.*;

/**
 * Needs Documentation
 *
 * @author Matthew
 *         Created 2/23/2015 at 5:43 PM
 */
@SuppressWarnings("ALL")
class DirectStackMemoryAccess extends MemoryAccess {

    private final ByteBuffer buf;

    DirectStackMemoryAccess(int bytes) {
        super(bytes);

        buf = BufferUtils.newUnsafeByteBuffer(bytes);
    }

    @Override
    public boolean isUnsafe() {
        return false;
    }

    @Override
    public void put(int memoryOffset, byte[] buffer, int bufferOffset, int bytes) {
        buf.clear().position(memoryOffset);
        buf.put(buffer, bufferOffset, bytes);
    }

    @Override
    public void put(int memoryOffset, byte[] buffer) {
        buf.clear().position(memoryOffset);
        buf.put(buffer);
    }

    @Override
    public void get(int memoryOffset, byte[] buffer, int bufferOffset, int bytes) {
        buf.clear().position(memoryOffset);
        buf.get(buffer, bufferOffset, bytes);
    }

    @Override
    public void get(int memoryOffset, byte[] buffer) {
        buf.clear().position(memoryOffset);
        buf.get(buffer);
    }

    @Override
    public byte getByte(int offset) {
        return buf.get(offset);
    }

    @Override
    public void putByte(int offset, byte val) {
        buf.put(offset, val);
    }

    @Override
    public short getUnsignedByte(int offset) {
        return (short) (buf.get(offset) & BYTE_TO_UNSIGNED_BYTE);
    }

    @Override
    public void putUnsignedByte(int offset, short val) {
        buf.put(offset, (byte) val);
    }

    @Override
    public short getShort(int offset) {
        return buf.getShort(offset);
    }

    @Override
    public void putShort(int offset, short val) {
        buf.putShort(offset, val);
    }

    @Override
    public int getUnsignedShort(int offset) {
        return buf.getShort(offset) & SHORT_TO_UNSIGNED_SHORT;
    }

    @Override
    public void putUnsignedShort(int offset, int val) {
        buf.putShort(offset, (short) val);
    }

    @Override
    public int getInt(int offset) {
        return buf.getInt(offset);
    }

    @Override
    public void putInt(int offset, int value) {
        buf.putInt(offset, value);
    }

    @Override
    public long getUnsignedInt(int offset) {
        return buf.getInt(offset) & INT_TO_UNSIGNED_INT;
    }

    @Override
    public void putUnsignedInt(int offset, long value) {
        buf.putInt(offset, (int) value);
    }

    @Override
    public long getLong(int offset) {
        return buf.getLong(offset);
    }

    @Override
    public void putLong(int offset, long value) {
        buf.putLong(offset, value);
    }

    @Override
    public void copy(int offset, MemoryAccess dest, int destOffset, int length) {
        if (dest instanceof DirectStackMemoryAccess) {
            DirectStackMemoryAccess mem = (DirectStackMemoryAccess) dest;
            buf.clear().position(offset);
            mem.buf.clear().position(destOffset);
            mem.buf.put(buf);
        } else if (dest instanceof HeapMemoryAccess) {
            HeapMemoryAccess mem = (HeapMemoryAccess) dest;
            get(offset, mem.memory(), destOffset, length);
        } else {
            Preconditions.checkElementIndex(offset, length() - length - 1);
            Preconditions.checkElementIndex(destOffset, dest.length() - length - 1);

            byte[] data = new byte[length];
            get(offset, data, 0, length);
            dest.put(destOffset, data);
        }
    }

    @Deprecated
    @Override
    public MemoryAccess clone() {
        return makeCopy();
    }

    @Override
    public DirectStackMemoryAccess makeCopy() {
        DirectStackMemoryAccess mem = new DirectStackMemoryAccess(length());
        copy(0, mem, 0, length());
        return mem;
    }

    @Override
    protected void freeMemory() {
        BufferUtils.disposeUnsafeByteBuffer(buf);
    }
}
