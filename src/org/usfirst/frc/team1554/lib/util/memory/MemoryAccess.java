package org.usfirst.frc.team1554.lib.util.memory;

import org.usfirst.frc.team1554.lib.Disposable;
import org.usfirst.frc.team1554.lib.RobotExecutionException;
import org.usfirst.frc.team1554.lib.RobotReflectionException;
import org.usfirst.frc.team1554.lib.util.Preconditions;
import org.usfirst.frc.team1554.lib.util.ReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Matthew on 2/23/2015.
 */
public abstract class MemoryAccess implements Freeable, Disposable {

    /**
     * Allocates either Unsafe Native or Direct Native Memory (Off-The-Heap vs. Native Heap).
     * If the Unsafe class cannot be loaded, then the Direct Memory Accessor is provided. <br />
     * <br />
     * Please see {@link MemoryAccess#allocateDirectMemory(int)} and {@link MemoryAccess#allocateUnsafeMemory(int)} for
     * extra information.<br />
     * <br />
     * If you desire a simpler safe implementation, pease use {@link MemoryAccess#allocateHeapMemory(ByteHandler, int)} which
     * will manage a byte array instead of JNI or Stack memory. Much safer and much less critical to free.
     *
     * @param bytes
     * @return Unsafe Memory if allowed, Direct Memory otherwise.
     * @see #allocateDirectMemory(int) Direct Memory Allocator
     * @see #allocateUnsafeMemory(int) Unsafe Off-The-Heap Memory Allocator
     * @see #allocateHeapMemory(ByteHandler, int) Heap Allocator
     */
    public static final MemoryAccess allocateMemory(int bytes) {
        MemoryAccess ma;
        try {
            ma = allocateUnsafeMemory(bytes);
        } catch (Exception e) {
            if (e instanceof InvocationTargetException &&
                    e.getCause() != null &&
                    e.getCause() instanceof OutOfMemoryError)
                throw new RobotExecutionException("Out of Memory!", e);

            ma = allocateDirectMemory(bytes);
        }

        return ma;
    }

    /**
     * Allocate Unsafe, off the Heap and on to the Stack, memory. This uses Unsafe operations provided in the
     * Sun API's to manipulate bytes in memory directly. This is a very convenient wrapper for neat Memory Use. <br />
     * <Br />
     * This does not aim to BE a Native Array Wrapper, it is an interface that simplifies Native Memory operations.
     *
     * @param bytes
     * @return
     * @throws RobotReflectionException
     */
    public static final MemoryAccess allocateUnsafeMemory(int bytes) throws RobotReflectionException {
        try {
            String pack = MemoryAccess.class.getPackage().getName();
            Class<? extends MemoryAccess> unsafeClass = UnsafeStackMemoryAccess.class.asSubclass(MemoryAccess.class);
            return ReflectionHelper.newInstance(unsafeClass, ReflectionHelper.CallParameters.createConstructorCall().add(bytes, int.class));
        } catch (Exception e) {
            throw new RobotReflectionException("Failed to load UnsafeStackMemory Class!", e);
        }
    }

    /**
     * Allocates Direct Memory (e.g. Native) using DirectByteBuffer. Noticeably faster than Heap Memory,
     * but only recommended for processes that can afford the extra Java Native Interface overhead. <br />
     * <br />
     * Not Recommended for typical use.
     *
     * @param bytes
     * @return
     */
    public static final MemoryAccess allocateDirectMemory(int bytes) {
        return new DirectStackMemoryAccess(bytes);
    }

    /**
     * Allocate memory on the Heap. This is identical to a typical array allocation. Nothing special
     * except a Byte Memory management data structure.
     *
     * @param byteHandler
     * @param bytes
     * @return
     */
    public static final MemoryAccess allocateHeapMemory(ByteHandler byteHandler, int bytes) {
        return new HeapMemoryAccess(byteHandler, bytes);
    }

    private final AtomicBoolean disposed = new AtomicBoolean(false);
    private final int capacity;

    MemoryAccess(int capacity) {
        Preconditions.checkExpression(capacity <= Integer.MAX_VALUE, "The MemoryAccess Framework does not support 64-bit Storage");
        this.capacity = capacity;
    }

    @Override
    public final void free() {
        if (!disposed.compareAndSet(false, true)) {
            freeMemory();
        }
    }

    @Override
    public final void dispose() {
        free();
    }

    public final boolean isDisposed() {
        return disposed.get();
    }

    public final int length() {
        return capacity;
    }

    public abstract boolean isUnsafe();

    public abstract void put(int memoryOffset, byte[] buffer, int bufferOffset, int bytes);

    public abstract void put(int memoryOffset, byte[] buffer);

    public abstract void get(int memoryOffset, byte[] buffer, int bufferOffset, int bytes);

    public abstract void get(int memoryOffset, byte[] buffer);

    public abstract byte getByte(int offset);

    public abstract void putByte(int offset, byte val);

    public abstract short getUnsignedByte(int offset);

    public abstract void putUnsignedByte(int offset, short val);

    public abstract short getShort(int offset);

    public abstract void putShort(int offset, short val);

    public abstract int getUnsignedShort(int offset);

    public abstract void putUnsignedShort(int offset, int val);

    public abstract int getInt(int offset);

    public abstract void putInt(int offset, int value);

    public abstract long getUnsignedInt(int offset);

    public abstract void putUnsignedInt(int offset, long value);

    public abstract long getLong(int offset);

    public abstract void putLong(int offset, long value);

    public abstract void copy(int offset, MemoryAccess dest, int destOffset, int length);

    public abstract MemoryAccess clone();

    protected abstract void freeMemory();

    @Override
    protected final void finalize() throws Exception {
        free();
    }
}
