package org.usfirst.frc.team1554.lib.util.memory;

import org.usfirst.frc.team1554.lib.RobotReflectionException;

/**
 * Created by Matthew on 2/23/2015.
 */
public abstract class ByteHandler {

    public static ByteHandler get() {
        ByteHandler bh;
        try {
            bh = unsafeByteHandler();
        } catch (Exception e) {
            bh = safeByteHandler();
        }

        return bh;
    }

    public static ByteHandler safeByteHandler() {
        return new BitShiftByteHandler();
    }

    public static ByteHandler unsafeByteHandler() throws RobotReflectionException {
        try {
            Class<? extends ByteHandler> klass = ByteHandler.class.getClassLoader()
                    .loadClass(ByteHandler.class.getPackage().getName() + ".UnsafeByteHandler")
                    .asSubclass(ByteHandler.class);

            return klass.newInstance();
        } catch (Exception e) {
            throw new RobotReflectionException("Failed to load UnsafeByteHandler!", e);
        }
    }

    public abstract boolean isUnsafe();

    public abstract byte getByte(byte[] data, int offset);

    public abstract void putByte(byte[] data, int offset, byte val);

    public abstract short getUnsignedByte(byte[] data, int offset);

    public abstract void putUnsignedByte(byte[] data, int offset, short val);

    public abstract short getShort(byte[] data, int offset);

    public abstract void putShort(byte[] data, int offset, short val);

    public abstract int getUnsignedShort(byte[] data, int offset);

    public abstract void putUnsignedShort(byte[] data, int offset, int val);

    public abstract int getInt(byte[] data, int offset);

    public abstract void putInt(byte[] data, int offset, int value);

    public abstract long getUnsignedInt(byte[] data, int offset);

    public abstract void putUnsignedInt(byte[] data, int offset, long value);

    public abstract long getLong(byte[] data, int offset);

    public abstract void putLong(byte[] data, int offset, long value);

    public abstract void copy(byte[] src, int srcOffset, byte[] dst, int dstOffset, int length);
}
