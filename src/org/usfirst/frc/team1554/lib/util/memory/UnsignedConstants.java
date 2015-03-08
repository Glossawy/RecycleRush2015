package org.usfirst.frc.team1554.lib.util.memory;

/**
 * Created by Matthew on 2/23/2015.
 */
interface UnsignedConstants {

    // Simple 2^n - 1
    static final short MAX_UNSIGNED_BYTE = (1 << 8) - 1;
    static final int MAX_UNSIGNED_SHORT = (1 << 16) - 1;
    static final long MAX_UNSIGNED_INT = (1L << 32) - 1;

    // Integer Promotion Bitmasks
    // These are just Integer Promotion idioms. The idea is to force an implicit conversion.
    //
    // The value only has to be of the type you want to convert to and must be all 1's for the
    // size you are converting from. i.e. the first 4 bytes of the UNSIGNED_INT conversion must be all
    // 1's to the value of the signed int. Ultimately the twos-complement bit is dropped.
    //
    // These should be used as the second operand in a bitwise AND operation.
    // e.g. -25771 & INT_TO_UNSIGNED_INT := 4294941525
    // e.g. 128 & BYTE_TO_UNSIGNED_BYTE := 128 (But is now an Integer, not a Byte)

    static final short BYTE_TO_UNSIGNED_BYTE = 0xFF;
    static final int SHORT_TO_UNSIGNED_SHORT = 0xFFFF;
    static final long INT_TO_UNSIGNED_INT = 0xFFFFFFFFL;

    // Size of a Byte. If you need it.
    static final byte BYTE_SIZE = 8;
}
