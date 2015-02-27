package org.usfirst.frc.team1554.lib.util.memory;

/**
 * Created by Matthew on 2/23/2015.
 */
interface UnsignedConstants {

    static final short MAX_UNSIGNED_BYTE = 1 << 8;
    static final int MAX_UNSIGNED_SHORT = 1 << 16;
    static final long MAX_UNSIGNED_INT = 1L << 32;

    static final short BYTE_TO_UNSIGNED_BYTE = 0xFF;
    static final int SHORT_TO_UNSIGNED_SHORT = 0xFFFF;
    static final long INT_TO_UNSIGNED_INT = 0xFFFFFFL;

    static final byte BYTE_SIZE = 8;

}
