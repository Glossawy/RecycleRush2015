package org.usfirst.frc.team1554.lib.util.memory;

/**
 * Created by Matthew on 2/23/2015.
 */
@SuppressWarnings("ALL")
class HeapMemoryAccess extends MemoryAccess {

    private final ByteHandler bits;
    private byte[] memory;

    HeapMemoryAccess(ByteHandler bits, int capacity) {
        super(capacity);

        this.bits = bits;
        this.memory = new byte[capacity];
    }

    @Override
    public boolean isUnsafe() {
        return false;
    }

    @Override
    public void put(int memoryOffset, byte[] buffer, int bufferOffset, int bytes) {
        bits.copy(buffer, bufferOffset, memory, memoryOffset, bytes);
    }

    @Override
    public void put(int memoryOffset, byte[] buffer) {
        bits.copy(buffer, 0, memory, memoryOffset, buffer.length);
    }

    @Override
    public void get(int memoryOffset, byte[] buffer, int bufferOffset, int bytes) {
        bits.copy(memory, memoryOffset, buffer, bufferOffset, bytes);
    }

    @Override
    public void get(int memoryOffset, byte[] buffer) {
        bits.copy(memory, memoryOffset, buffer, 0, buffer.length);
    }

    @Override
    public byte getByte(int offset) {
        return bits.getByte(memory, offset);
    }

    @Override
    public void putByte(int offset, byte val) {
        bits.putByte(memory, offset, val);
    }

    @Override
    public short getUnsignedByte(int offset) {
        return bits.getUnsignedByte(memory, offset);
    }

    @Override
    public void putUnsignedByte(int offset, short val) {
        bits.putUnsignedByte(memory, offset, val);
    }

    @Override
    public short getShort(int offset) {
        return bits.getShort(memory, offset);
    }

    @Override
    public void putShort(int offset, short val) {
        bits.putShort(memory, offset, val);
    }

    @Override
    public int getUnsignedShort(int offset) {
        return bits.getUnsignedShort(memory, offset);
    }

    @Override
    public void putUnsignedShort(int offset, int val) {
        bits.putUnsignedShort(memory, offset, val);
    }

    @Override
    public int getInt(int offset) {
        return bits.getInt(memory, offset);
    }

    @Override
    public void putInt(int offset, int value) {
        bits.putInt(memory, offset, value);
    }

    @Override
    public long getUnsignedInt(int offset) {
        return bits.getUnsignedInt(memory, offset);
    }

    @Override
    public void putUnsignedInt(int offset, long value) {
        bits.putUnsignedInt(memory, offset, value);
    }

    @Override
    public long getLong(int offset) {
        return bits.getLong(memory, offset);
    }

    @Override
    public void putLong(int offset, long value) {
        bits.putLong(memory, offset, value);
    }

    @Override
    public void copy(int offset, MemoryAccess dest, int destOffset, int length) {
        if (dest instanceof HeapMemoryAccess) {
            bits.copy(memory, offset, ((HeapMemoryAccess) dest).memory, destOffset, length);
        } else
            dest.put(destOffset, memory, offset, length);
    }

    byte[] memory() {
        return memory;
    }

    @Override
    public MemoryAccess clone() {
        HeapMemoryAccess mem = new HeapMemoryAccess(bits, memory.length);
        this.copy(0, mem, 0, memory.length);
        return mem;
    }

    @Override
    protected void freeMemory() {
        memory = null;
    }
}
