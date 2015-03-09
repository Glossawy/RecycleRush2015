package org.usfirst.frc.team1554.lib.util;

import org.usfirst.frc.team1554.lib.collect.Array;
import org.usfirst.frc.team1554.lib.common.Console;
import org.usfirst.frc.team1554.lib.common.ex.RobotExecutionException;
import sun.misc.Cleaner;
import sun.nio.ch.DirectBuffer;

import java.nio.*;
import java.util.Arrays;

import static org.usfirst.frc.team1554.lib.util.ReflectionHelper.getInstanceField;

/**
 * A Utility to Create and Manage NIO Buffers. In almost all cases,
 * this class will use DirectByteBuffer's for speed. If you desire a
 * normal ByteBuffer you should use {@link java.nio.ByteBuffer#allocate(int)
 * ByteBuffer#allocate} or {@link #newSafeByteBuffer(int)}. <br />
 * <br />
 * Even calling {@link #newByteBuffer(int)} will createMethodCall a
 * DirectByteBuffer, you must use {@link #newSafeByteBuffer(int)}.
 * "safe" ByteBuffers will NOT be managed by this class and do not
 * need to be disposed, they will not be disposed when {@link
 * #disposeAllBuffers()} is called.
 *
 * @author Matthew
 */
public final class BufferUtils {

    private BufferUtils() {
    }

    private static final Array<ByteBuffer> unsafeBuffers = new Array<>(ByteBuffer.class);
    private static int unsafeAllocated = 0;

    /**
     * Create a new Memory Mapped FloatBuffer
     *
     * @param numFloats
     * @return
     */
    public static FloatBuffer newFloatBuffer(int numFloats) {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(numFloats * 4);
        buffer.order(ByteOrder.nativeOrder());
        return buffer.asFloatBuffer();
    }

    /**
     * Create a new Memory Mapped DoubleBuffer
     *
     * @param numDoubles
     * @return
     */
    public static DoubleBuffer newDoubleBuffer(int numDoubles) {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(numDoubles * 8);
        buffer.order(ByteOrder.nativeOrder());
        return buffer.asDoubleBuffer();
    }

    /**
     * Create a new Memory Mapped ByteBuffer
     *
     * @param numBytes
     * @return
     */
    public static ByteBuffer newByteBuffer(int numBytes) {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(numBytes);
        buffer.order(ByteOrder.nativeOrder());
        return buffer;
    }

    /**
     * Create a new Memory Mapped ShortBuffer
     *
     * @param numShorts
     * @return
     */
    public static ShortBuffer newShortBuffer(int numShorts) {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(numShorts * 2);
        buffer.order(ByteOrder.nativeOrder());
        return buffer.asShortBuffer();
    }

    /**
     * Create a new Memory Mapped CharBuffer
     *
     * @param numChars
     * @return
     */
    public static CharBuffer newCharBuffer(int numChars) {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(numChars * 2);
        buffer.order(ByteOrder.nativeOrder());
        return buffer.asCharBuffer();
    }

    /**
     * Create a new Memory Mapped IntBuffer
     *
     * @param numInts
     * @return
     */
    public static IntBuffer newIntBuffer(int numInts) {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(numInts * 4);
        buffer.order(ByteOrder.nativeOrder());
        return buffer.asIntBuffer();
    }

    /**
     * Create a new Memory Mapped LongBuffer
     *
     * @param numLongs
     * @return
     */
    public static LongBuffer newLongBuffer(int numLongs) {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(numLongs * 8);
        buffer.order(ByteOrder.nativeOrder());
        return buffer.asLongBuffer();
    }

    /**
     * Create a new, standard, ByteBuffer on the Heap.
     *
     * @param numBytes
     * @return
     */
    public static ByteBuffer newSafeByteBuffer(int numBytes) {
        return ByteBuffer.allocate(numBytes);
    }

    /**
     * Creates a Direct ByteBuffer and keeps track of it for later
     * automatic disposal.
     *
     * @param numBytes
     * @return
     */
    public static ByteBuffer newUnsafeByteBuffer(int numBytes) {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(numBytes);
        buffer.order(ByteOrder.nativeOrder());
        unsafeAllocated += numBytes;

        synchronized (unsafeBuffers) {
            unsafeBuffers.add(buffer);
        }

        return buffer;
    }

    /**
     * Create a new Memory Mapped ByteBuffer at a specific memory
     * address.
     *
     * @param addr
     * @param numBytes
     * @return
     */
    public static ByteBuffer newUnsafeByteBuffer(long addr, int numBytes) {
        final ByteBuffer buffer = unsafeAllocate(addr, numBytes);
        buffer.order(ByteOrder.nativeOrder());
        unsafeAllocated += numBytes;

        synchronized (unsafeBuffers) {
            unsafeBuffers.add(buffer);
        }

        return buffer;
    }

    /**
     * Get the address of a memory mapped Buffer
     *
     * @param buffer
     * @return
     */
    public static long getUnsafeBufferAddress(ByteBuffer buffer) {
        unsafeBBChecks(buffer);

        return getInstanceField(Buffer.class, buffer, "address", Long.class);
    }

    /**
     * Mark a ByteBuffer as Unsafe and keep track of it for later
     * disposal. <br />
     * The ByteBuffer must be a DirectByteBuffer.
     *
     * @param buffer
     * @return
     */
    public static ByteBuffer newUnsafeByteBuffer(ByteBuffer buffer) {
        unsafeBBChecks(buffer);

        unsafeAllocated += buffer.capacity();
        synchronized (unsafeBuffers) {
            unsafeBuffers.add(buffer);
        }

        return buffer;
    }

    /**
     * Dispose ByteBuffer and free the memory it is using up
     * immediately.
     *
     * @param buffer
     */
    public static void disposeUnsafeByteBuffer(ByteBuffer buffer) {
        unsafeBBChecks(buffer);
        final int size = buffer.capacity();

        synchronized (unsafeBuffers) {
            Preconditions.checkExpression(unsafeBuffers.removeValue(buffer, true), "Buffer Not Allocated with newUnsafeByteBuffer() or Already Disposed!");
        }

        unsafeAllocated -= size;
        freeMemory(buffer);
    }

    /**
     * Clear ByteBuffer by filling it with 0's
     *
     * @param buffer
     */
    public static void clear(ByteBuffer buffer) {
        if (!buffer.hasArray() || buffer.isReadOnly()) {
            buffer.position(0);
            buffer.put(new byte[buffer.capacity()]);
        } else {
            Arrays.fill(buffer.array(), (byte) 0);
        }

        buffer.clear();
    }

    /**
     * Get the number of bytes currently reserved by Unsafe
     * ByteBuffers.
     *
     * @return
     */
    public static int getAllocatedUnsafeBytes() {
        return unsafeAllocated;
    }

    /**
     * Dispose all Unsafe Buffers. This should be called at the
     * termination of a program.
     */
    public static void disposeAllBuffers() {
        final int itemCount = unsafeBuffers.size;
        final ByteBuffer[] tmp = new ByteBuffer[itemCount];
        System.arraycopy(unsafeBuffers.items, 0, tmp, 0, itemCount);

        for (final ByteBuffer buf : tmp) {
            disposeUnsafeByteBuffer(buf);
        }
    }

    // Free Memory by accessing the Sun Implemented Cleaner
    // For this to be actually effective, the buffer must be a DirectBuffer.
    private static void freeMemory(ByteBuffer buffer) {
        if (!(buffer instanceof DirectBuffer))
            return;

        final Cleaner cleaner = ((DirectBuffer) buffer).cleaner();

        if (cleaner == null) {
            // Sun provided us with a nice cleanup implementation as long
            // as we use a DirectBuffer cast. An alternative cleanup using Unsafe operations
            // is provided which is meant to mimic the operation of
            cleaner.clean();
        } else {
            long address = getUnsafeBufferAddress(buffer);
            if (address != 0) {
                try {
                    NIOAccess.magicallyFreeBuffer_LikeABlackBoxOfDarkWizardry_ExceptItSolvesProblems(address, buffer);
                } catch (RobotExecutionException e) {
                    Console.exception(e, "Error in freeing Buffer Memory, likely unintentional!");
                    RoboUtils.writeToDS("Error In Freeing Buffer Memory...\n[See RIOConsole]");
                }
            } else {
                Console.warn("Attempted to free memory in a DirectByteBuffer but the address was 0... Was it not Unsafe or was it already freed?");
            }
        }
    }

    // Allocates a DirectByteBuffer at the specific Address with a capacity of numBytes
    private static ByteBuffer unsafeAllocate(long addr, int numBytes) {
        try {
            return (ByteBuffer) NIOAccess.DANGEROUS_CONSTRUCTOR_DONT_TOUCH_OR_DOLPHINS_DIE.newInstance(addr, numBytes);
        } catch (final Exception e) {
            throw new RobotExecutionException("Failed to Create Direct Byte Buffer at " + Long.toHexString(addr) + " of size " + numBytes + "b", e);
        }
    }

    // Perform DirectByteBuffer checks.
    private static void unsafeBBChecks(ByteBuffer bb) {
        Preconditions.checkNotNull(bb);
        Preconditions.checkExpression(NIOAccess.SCARY_NATIVE_BUFFER_CLASS.isInstance(bb), "ByteBuffer is NOT An Unsafe/Direct ByteBuffer!");
    }

}
