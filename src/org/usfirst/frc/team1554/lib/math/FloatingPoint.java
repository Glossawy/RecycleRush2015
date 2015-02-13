package org.usfirst.frc.team1554.lib.math;

public class FloatingPoint {

	public static final float ROUNDING_ERROR_32_BITS = 1.0f / (1 << 24);
	public static final double ROUNDING_ERROR_64_BITS = 1.0 / (1L << 53); // Maybe?

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

	public static final boolean isEqual(float a, float b) {
		return Math.abs(a - b) < ROUNDING_ERROR_32_BITS;
	}

	public static final boolean isEqual(float a, float b, float tolerance) {
		return Math.abs(a - b) < tolerance;
	}

	public static final boolean isEqual(double val1, double val2) {
		return Math.abs(val1 - val2) < ROUNDING_ERROR_64_BITS;
	}

	public static final boolean isEqual(double val1, double val2, double tolerance) {
		return Math.abs(val1 - val2) < tolerance;
	}

}
