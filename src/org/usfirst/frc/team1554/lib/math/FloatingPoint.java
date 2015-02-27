package org.usfirst.frc.team1554.lib.math;

public class FloatingPoint {

    public static final float ERROR_32_BITS = 1.0f / (1 << 24);
    public static final double ERROR_64_BITS = 1.0 / (1L << 53);

    public static boolean isZero(float val) {
        return Math.abs(val) < ERROR_32_BITS;
    }

    public static boolean isZero(float val, float tolerance) {
        return Math.abs(val) < tolerance;
    }

    public static boolean isZero(double val) {
        return Math.abs(val) < ERROR_64_BITS;
    }

    public static boolean isZero(double val, double tolerance) {
        return Math.abs(val) < tolerance;
    }

    public static int toIntBits(float val) {
        return Float.floatToIntBits(val);
    }

    public static long toLongBits(double val) {
        return Double.doubleToLongBits(val);
    }

    public static float toFloatBits(int val) {
        return Float.intBitsToFloat(val);
    }

    public static double toDoubleBits(long val) {
        return Double.longBitsToDouble(val);
    }

    public static boolean isEqual(float a, float b) {
        return Math.abs(a - b) < ERROR_32_BITS;
    }

    public static boolean isEqual(float a, float b, float tolerance) {
        return Math.abs(a - b) < tolerance;
    }

    public static boolean isEqual(double val1, double val2) {
        return Math.abs(val1 - val2) < ERROR_64_BITS;
    }

    public static boolean isEqual(double val1, double val2, double tolerance) {
        return Math.abs(val1 - val2) < tolerance;
    }

}
