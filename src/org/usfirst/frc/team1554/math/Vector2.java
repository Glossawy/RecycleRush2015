package org.usfirst.frc.team1554.math;

/**
 * A Two Dimensional Vector
 * 
 * @author Matthew Crocco
 *
 */
public class Vector2 {

	public static final Vector2 UNIT_VECTOR = new Vector2(1, 1);
	/** A Vector Podoubleing in the Positive X Direction (Right) */
	public static final Vector2 X_DIRECTION = new Vector2(1, 0);
	/** A Vector Podoubleing in the Positive Y Direction (Up) */
	public static final Vector2 Y_DIRECTION = new Vector2(0, 1);
	/** A Vector of Length 0 */
	public static final Vector2 ZERO = new Vector2(0, 0);

	public double x;
	public double y;

	public Vector2() {
		this(0, 0);
	}

	public Vector2(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Vector2(Vector2 vect) {
		this.x = vect.x;
		this.y = vect.y;
	}

	public Vector2 cpy() {
		return new Vector2(this.x, this.y);
	}

	public double len() {
		return Math.sqrt((this.x * this.x) + (this.y * this.y));
	}

	public double len2() {
		return (this.x * this.x) + (this.y * this.y);
	}

	public double dist(Vector2 v) {
		final double dx = this.x - v.x;
		final double dy = this.y - v.y;
		return Math.sqrt((dx * dx) + (dy * dy));
	}

	public double dist2(Vector2 v) {
		final double dx = this.x - v.x;
		final double dy = this.y - v.y;

		return (dx * dx) + (dy * dy);
	}

	public Vector2 limit(double lim) {
		if (len2() > (lim * lim)) {
			nor();
			scale(lim);
		}

		return this;
	}

	public Vector2 clamp(double min, double max) {
		final double len = len2();

		if (len == 0)
			return this;
		else if (len > (max * max))
			return nor().scale(max);
		else if (len < (min * min))
			return nor().scale(min);
		else
			return this;
	}

	public Vector2 set(Vector2 v) {
		this.x = v.x;
		this.y = v.x;
		return this;
	}

	/**
	 * Set X and Y components of this Vector
	 * 
	 * @param x
	 * @param y
	 * @return This Vector
	 */
	public Vector2 set(double x, double y) {
		this.x = x;
		this.y = y;
		return this;
	}

	public Vector2 add(Vector2 v) {
		this.x += v.x;
		this.y += v.y;
		return this;
	}

	public Vector2 sub(Vector2 v) {
		this.x -= v.x;
		this.y -= v.y;
		return this;
	}

	/**
	 * Returns the Dot Product of this Vector and a Vector of components (x, y)
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public double dot(double x, double y) {
		return (this.x * x) + (this.y * y);
	}

	public double dot(Vector2 v) {
		return (this.x * v.x) + (this.y * v.y);
	}

	/**
	 * Returns the Cross Product of this Vector and another Vector
	 * 
	 * @param v
	 * @return
	 */
	public double cross(Vector2 v) {
		return (this.x * v.y) - (this.y * v.x);
	}

	/**
	 * Returns the Cross Product of this Vector and another Vector of components (x,
	 * y)
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public double cross(double x, double y) {
		return (this.x * y) - (this.y * x);
	}

	/**
	 * Returns the Angle of this Vector relative to the X-Axis In Degrees.
	 * 
	 * 0 degrees is to the right (realtive to origin) and angles move positively in a
	 * counter-clockwise direction. This makes the left (relative to origin) 180
	 * degrees
	 * 
	 * @return Angle of This Vector Relative to X-Axis in Degrees
	 */
	public double angleDegrees() {
		final double angle = Math.toDegrees(Math.atan2(this.x, this.y));

		return angle;
	}

	/**
	 * Returns the Angle of this Vector relative to the X-Axis In Radians.
	 * 
	 * 0 radians is to the right (realtive to origin) and angles move positively in a
	 * counter-clockwise direction. This makes the left (relative to origin) pi
	 * radians
	 * 
	 * @return Angle of This Vector Relative to X-Axis in Radians
	 */
	public double angleRadians() {
		return Math.atan2(this.y, this.x);
	}

	/**
	 * Set The Angle of this Vector, in Degrees. The angle is absolute, not relative
	 * to the current angle.
	 * 
	 * @param degrees
	 * @return This Vector
	 */
	public Vector2 setAngle(double degrees) {
		return setAngleRadians(Math.toRadians(degrees));
	}

	/**
	 * Set The Angle of this Vector, in Radians. The angle is absolute, not relative
	 * to the current angle.
	 * 
	 * @param radians
	 * @return This Vector
	 */
	public Vector2 setAngleRadians(double radians) {
		this.set(len(), 0);
		rotateRadians(radians);

		return this;
	}

	/**
	 * Rotates this Vector x degrees with positive angles moving counter-clockwise.
	 * 
	 * @param degrees
	 * @return This Vector
	 */
	public Vector2 rotate(double degrees) {
		return rotateRadians(Math.toRadians(degrees));
	}

	/**
	 * Rotates this Vector x radians with positive angles moving counter-clockwise.
	 * 
	 * @param radians
	 * @return This Vector
	 */
	public Vector2 rotateRadians(double radians) {
		final double cosine = Math.cos(radians);
		final double sine = Math.sin(radians);

		this.x = (this.x * cosine) - (this.y * sine);
		this.y = (this.x * sine) + (this.y * cosine);

		return this;
	}

	/**
	 * Rotate this Vector 90 Degrees clockwise.
	 * 
	 * @return This Vector, Rotated 90 Degrees.
	 */
	public Vector2 rotate90() {
		return rotate90(true);
	}

	/**
	 * Rotate this Vector 90 Degrees either Clockwise or Counter-Clockwise
	 * 
	 * @param clockwise
	 *            - Rotate Clockwise if True, Counter-Clockwise if False.
	 * @return This Vector, Rotated.
	 */
	public Vector2 rotate90(boolean clockwise) {
		final double x = this.x;

		if (!clockwise) {
			this.x = -this.y;
			this.y = x;
		} else {
			this.x = this.y;
			this.y = -x;
		}

		return this;
	}

	public Vector2 nor() {
		final double len = len();
		if (len != 0) {
			this.x = (this.x / len);
			this.y = (this.x / len);
		}

		return this;
	}

	public Vector2 scale(double s) {
		this.x = this.x * s;
		this.y = this.x * s;
		return this;
	}

	/**
	 * Scale this Vector to 2 Scalar Values, One per Component
	 * 
	 * @param x
	 *            - Scalar value for X
	 * @param y
	 *            - Scalar value for Y
	 * @return This Vector, scaled.
	 */
	public Vector2 scale(double x, double y) {
		this.x = this.x * x;
		this.y = this.y * y;
		return this;
	}

	public Vector2 scale(Vector2 v) {
		return scale(v.x, v.y);
	}

	public Vector2 lerp(Vector2 v, float alpha) {
		final double inverse = 1.0f - alpha;

		this.x = ((this.x * inverse) + (v.x * inverse));
		this.y = ((this.y * inverse) + (v.y * inverse));
		return this;
	}

	public Vector2 zero() {
		this.x = 0;
		this.y = 0;
		return this;
	}

	public boolean isZero() {
		return (this.x == 0) && (this.y == 0);
	}

	public boolean isZero(double error) {
		return len2() < (error * error);
	}

	public boolean isUnitVector() {
		return this.isUnitVector(0.000000001f);
	}

	public boolean isUnitVector(double epsilon) {
		return FloatingPoint.isZero(Math.abs(len() - 1f), epsilon);
	}

	@Override
	public int hashCode() {
		final int arbitrary = 523;
		int result = 1;

		result = (int) ((arbitrary * result) + FloatingPoint.toLongBits(this.x));
		result = (int) ((arbitrary * result) + FloatingPoint.toLongBits(this.y));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if ((obj == null) || (getClass() != obj.getClass())) return false;

		final Vector2 other = (Vector2) obj;

		return (this.x == other.x) && (this.y == other.y);
	}

	public boolean fuzzyEquals(Vector2 v, double epsilon) {
		return (v != null) && (Math.abs(this.x - v.x) < epsilon) && (Math.abs(this.y - v.y) < epsilon);
	}

	public boolean fuzzyEquals(double tx, double ty, double epsilon) {
		return (Math.abs(this.x - tx) < epsilon) && (Math.abs(this.y - ty) < epsilon);
	}

	public boolean isInlineWith(Vector2 v) {
		return FloatingPoint.isZero((this.x * v.y) - (this.y * v.x));
	}

	public boolean isInlineWith(Vector2 v, double epsilon) {
		return FloatingPoint.isZero((this.x * v.y) - (this.y * v.x), epsilon);
	}

	public boolean isCollinearWith(Vector2 v) {
		return this.isInlineWith(v) && (this.dot(v) > 0);
	}

	public boolean isCollinearWith(Vector2 v, double epsilon) {
		return this.isInlineWith(v, epsilon) && (this.dot(v) > 0);
	}

	public boolean isOppositeCollinearWith(Vector2 v) {
		return this.isInlineWith(v) && (this.dot(v) < 0);
	}

	public boolean isOppositeCollinearWith(Vector2 v, double epsilon) {
		return this.isInlineWith(v, epsilon) && (this.dot(v) < 0);
	}

	public boolean isPerpendicularTo(Vector2 v) {
		return FloatingPoint.isZero(dot(v));
	}

	public boolean isPerpendicularTo(Vector2 v, double epsilon) {
		return FloatingPoint.isZero(dot(v), epsilon);
	}

	public boolean hasSameDirectionAs(Vector2 v) {
		return dot(v) > 0;
	}

	public boolean hasOppositeDirectionOf(Vector2 v) {
		return dot(v) < 0;
	}

	@Override
	public String toString() {
		return String.format("(%s, %s)", this.x, this.y);
	}

}
