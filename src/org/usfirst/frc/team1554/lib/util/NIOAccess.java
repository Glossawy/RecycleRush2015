package org.usfirst.frc.team1554.lib.util;

import sun.misc.Unsafe;

import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;

import static org.usfirst.frc.team1554.lib.util.ReflectionHelper.CallParameters.createMethodCall;
import static org.usfirst.frc.team1554.lib.util.ReflectionHelper.*;

// I got annoyed so I decided to have some fun. TODO Remove Beelzebub References Eventually
// See bottom of Class File for REAL documentation

/**
 * If Satan drives out Satan, he is divided against himself. How then can his kingdom stand?
 * And if I drive out demons by Beelzebul, by whom do your people drive them out? So then,
 * they will be your judges. But if I drive out demons by the Spirit of God, then the kingdom of
 * God has come upon you.<br/>
 * <pre>
 *                                  — Matthew 12:25-28
 * </pre>
 *
 * @author Matthew
 */
final class NIOAccess {

    private NIOAccess() {
    }

    // ¯\_(ツ)_/¯
    // All Hail
    static final String NAME_OF_BEELZEBUB = "theUnsafe";
    static final Unsafe ohGodPleaseDoNotEverUse;
    static final Class<?> SCARY_VM_CLASS;
    static final Class<?> SCARY_NATIVE_BITS_CLASS;
    static final Class<?> SCARY_NATIVE_BUFFER_CLASS;

    // Beelzebub Demands Dolphin Blood!
    static final Constructor<?> DANGEROUS_CONSTRUCTOR_DONT_TOUCH_OR_DOLPHINS_DIE;

    // Beelzebubs Minions Rise!
    private static final String THE_CAIN_OF_KLASS = "sun.misc.VM"; // Oh no! A Sun API Reference!
    private static final String THAT_WHICH_REIGNS_IN_PURGATORY = "java.nio.Bits"; // Oh dear! The horror!
    private static final String THE_LESSER_OF_EVILS = "java.nio.DirectByteBuffer"; // Please Stop!

    // Beelzebubs Tasks:
    private static final String MALIGN_GOOD_PEOPLE = "isDirectMemoryPageAligned";
    private static final String READ_THE_BAD_TEXT = "pageSize";
    private static final String FREE_RESERVED_DINNER_TABLE = "unreserveMemory";
    private static final String GPS_LOCATION_OF_LESSER_EVIL = "address";

    static {
        try {
            Class<?>[] cArgs = {long.class, int.class};

            ohGodPleaseDoNotEverUse = getStaticField(Unsafe.class, NAME_OF_BEELZEBUB, Unsafe.class);
            SCARY_VM_CLASS = Class.forName(THE_CAIN_OF_KLASS);
            SCARY_NATIVE_BITS_CLASS = Class.forName(THAT_WHICH_REIGNS_IN_PURGATORY);
            SCARY_NATIVE_BUFFER_CLASS = Class.forName(THE_LESSER_OF_EVILS);

            // All bets are off at this point
            DANGEROUS_CONSTRUCTOR_DONT_TOUCH_OR_DOLPHINS_DIE = SCARY_NATIVE_BUFFER_CLASS.getDeclaredConstructor(cArgs);
            DANGEROUS_CONSTRUCTOR_DONT_TOUCH_OR_DOLPHINS_DIE.setAccessible(true);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Free a Direct ByteBuffer manually using Unsafe and the VM/Bits system. Mimicry of DirectByteBuffers Deallocator
     * implementation. <br />
     * <br />
     * Should only be called if the Buffer is a DirectByteBuffer, yet does not have a Cleaner implementation. <br />
     * <br />
     * <p>
     * {@code ((DirectBuffer) buffer).cleaner() would be a better option.}
     *
     * @param addr
     * @param buffer
     */
    static final void magicallyFreeBuffer_LikeABlackBoxOfDarkWizardry_ExceptItSolvesProblems(long addr, ByteBuffer buffer) {
        boolean aligned = invokeStaticMethod(SCARY_VM_CLASS, createMethodCall(MALIGN_GOOD_PEOPLE), Boolean.class);
        int pageSize = invokeStaticMethod(SCARY_NATIVE_BITS_CLASS, createMethodCall(READ_THE_BAD_TEXT), Integer.class);
        int cap = buffer.capacity();
        long size = Math.max(1L, (long) cap + (aligned ? pageSize : 0));

        ohGodPleaseDoNotEverUse.freeMemory(addr);
        setInstanceField(SCARY_NATIVE_BUFFER_CLASS, buffer, GPS_LOCATION_OF_LESSER_EVIL, 0L, Long.class);
        invokeStaticMethod(SCARY_NATIVE_BITS_CLASS, createMethodCall(FREE_RESERVED_DINNER_TABLE).add(size, Long.class).add(cap, Integer.class));
    }

    /*
    This class is meant to remove all the really annoying unclean references to inaccessible classes
    in ReflectionHelper. It statically initializes all the required global variables or throws an ExceptionInInitializerError.

    Hopefully that never happens, it hasn't changed since 1.4 but who knows? If so then the names need to be changed
    as appropriate for Sun's VM Class, NIO's Bits class and NIO's DirectByteBuffer Class.

    This follows, nearly to the method-by-method calls found in DirectByteBuffers Deallocator class. It
    Frees Memory using Unsafe after finding the required information for the Direct Memory Page and determining
    the buffers total size. After freeing memory, DirectByteBuffer passes reserved memory information to Bits
    so that NIO can keep track of reserved memory. To free that information we call unreserveMemory with the
    size of the buffer and it's total capacity. Otherwise we would free the native memory without notifying the VM
    about it.

    The Field change before the invocation of unreserveMemory is purely to prevent it from being freed again in the
    future (Java Devs called this paranoia, it just prevents someone from freeing memory twice, the second time
    of which may delete VM Critical data).

    This should only ever be used if retrieving a DirectBuffer Cleaner fails.
     */
}
