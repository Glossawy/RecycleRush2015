package org.usfirst.frc.team1554.math;

public class FloatingPoint {

	public static final float ROUNDING_ERROR_32_BITS = 0.000001f;
	public static final double ROUNDING_ERROR_64_BITS = 0.000000001d;	// Maybe?

	public static final boolean isZero(float val) {
		return Math.abs(val) < ROUNDING_ERROR_32_BITS;
	}

	public static final boolean isZero(float val, float tolerance) {
		return Math.abs(val) < tolerance;
	}

	public static final boolean isZero(double val) {
		return Math.abs(val) < ROUNDING_ERROR_64_BITS;
	}

	public static final boolean isZero(double val, double tolerance) {
		return Math.abs(val) < tolerance;
	}

	public static final int toIntBits(float val) {
		return Float.floatToIntBits(val);
	}

	public static final long toLongBits(double val) {
		return Double.doubleToLongBits(val);
	}

	public static final float toFloatBits(int val) {
		return Float.intBitsToFloat(val);
	}

	public static final double toDoubleBits(long val) {
		return Double.longBitsToDouble(val);
	}
}
