package org.usfirst.frc.team1554.lib.math;

/**
 * A Two Dimensional Vector
 *
 * @author Matthew Crocco
 */
public class Vector2 implements Vector<Vector2> {

    public static final Vector2 UNIT_VECTOR = new Vector2(1, 1);
    /**
     * A Vector Podoubleing in the Positive X Direction (Right)
     */
    public static final Vector2 X_DIRECTION = new Vector2(1, 0);
    /**
     * A Vector Podoubleing in the Positive Y Direction (Up)
     */
    public static final Vector2 Y_DIRECTION = new Vector2(0, 1);
    /**
     * A Vector of Length 0
     */
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

    @Override
    public Vector2 cpy() {
        return new Vector2(this.x, this.y);
    }

    @Override
    public double len() {
        return Math.sqrt((this.x * this.x) + (this.y * this.y));
    }

    @Override
    public double len2() {
        return (this.x * this.x) + (this.y * this.y);
    }

    public double dst(double x, double y) {
        final double dy = y - this.y;
        final double dx = x - this.x;

        return Math.sqrt((dx * dx) + (dy * dy));
    }

    public double dst2(double x, double y) {
        final double dy = y - this.y;
        final double dx = x - this.x;

        return (dx * dx) + (dy * dy);
    }

    @Override
    public double dst(Vector2 v) {
        return dst(v.x, v.y);
    }

    @Override
    public double dst2(Vector2 v) {
        return dst2(v.x, v.y);
    }

    @Override
    public Vector2 limit(double lim) {
        return limit2(lim * lim);
    }

    @Override
    public Vector2 limit2(double limit2) {
        final double l2 = len2();

        if (l2 > limit2) {
            scale(Math.sqrt(limit2 / l2));
        }

        return this;
    }

    @Override
    public Vector2 setLength(double len) {
        return setLength2(len * len);
    }

    @Override
    public Vector2 setLength2(double len2) {
        final double l2 = len2();

        return (l2 == 0) || (l2 == len2) ? this : scale(Math.sqrt(len2 / l2));
    }

    @Override
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

    @Override
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

    @Override
    public Vector2 add(Vector2 v) {
        this.x += v.x;
        this.y += v.y;
        return this;
    }

    @Override
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

    @Override
    public double dot(Vector2 v) {
        return (this.x * v.x) + (this.y * v.y);
    }

    /**
     * Returns the Cross Product of this Vector and another Vector
     *
     * @param v
     * @return
     */
    public double crs(Vector2 v) {
        return (this.x * v.y) - (this.y * v.x);
    }

    /**
     * Returns the Cross Product of this Vector and another Vector of components (x, y)
     *
     * @param x
     * @param y
     * @return
     */
    public double crs(double x, double y) {
        return (this.x * y) - (this.y * x);
    }

    @Override
    public Vector2 mulAdd(Vector2 v, double scalar) {
        this.x += v.x * scalar;
        this.y += v.y * scalar;

        return this;
    }

    @Override
    public Vector2 mulAdd(Vector2 v, Vector2 scl) {
        this.x += v.x * scl.x;
        this.y += v.y * scl.y;

        return this;
    }

    /**
     * Returns the Angle of this Vector relative to the X-Axis In Degrees.
     * <p>
     * 0 degrees is to the right (realtive to origin) and angles move positively in a counter-clockwise direction. This makes the left (relative to origin) 180 degrees
     *
     * @return Angle of This Vector Relative to X-Axis in Degrees
     */
    public double angleDegrees() {
        double angle = Math.toDegrees(Math.atan2(this.y, this.x));

        if (angle < 0)
            angle += 360;

        return angle;
    }

    public double angleDegrees(Vector2 ref) {
        return Math.toDegrees(Math.atan2(crs(ref), dot(ref)));
    }

    /**
     * Returns the Angle of this Vector relative to the X-Axis In Radians.
     * <p>
     * 0 radians is to the right (realtive to origin) and angles move positively in a counter-clockwise direction. This makes the left (relative to origin) pi radians
     *
     * @return Angle of This Vector Relative to X-Axis in Radians
     */
    public double angleRadians() {
        double angle = Math.atan2(this.y, this.x);

        if (angle < 0)
            angle += 2 * Math.PI;

        return angle;
    }

    public double angleRadians(Vector2 ref) {
        return Math.atan2(crs(ref), dot(ref));
    }

    /**
     * Set The Angle of this Vector, in Degrees. The angle is absolute, not relative to the current angle.
     *
     * @param degrees
     * @return This Vector
     */
    public Vector2 setAngle(double degrees) {
        return setAngleRadians(Math.toRadians(degrees));
    }

    /**
     * Set The Angle of this Vector, in Radians. The angle is absolute, not relative to the current angle.
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
     * Rotate this Vector 90 Degrees either Clockwise or Counter-Clockwise
     *
     * @param clockwise - Rotate Clockwise if True, Counter-Clockwise if False.
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

    @Override
    public Vector2 nor() {
        final double len = len();
        if (len != 0) {
            this.x = this.x / len;
            this.y = this.x / len;
        }

        return this;
    }

    @Override
    public Vector2 scale(double s) {
        this.x = this.x * s;
        this.y = this.x * s;
        return this;
    }

    /**
     * Scale this Vector to 2 Scalar Values, One per Component
     *
     * @param x - Scalar value for X
     * @param y - Scalar value for Y
     * @return This Vector, scaled.
     */
    public Vector2 scale(double x, double y) {
        this.x = this.x * x;
        this.y = this.y * y;
        return this;
    }

    @Override
    public Vector2 scale(Vector2 v) {
        return scale(v.x, v.y);
    }

    @Override
    public Vector2 lerp(Vector2 v, double alpha) {
        final double inverse = 1.0f - alpha;

        this.x = (this.x * inverse) + (v.x * inverse);
        this.y = (this.y * inverse) + (v.y * inverse);
        return this;
    }

    @Override
    public Vector2 zero() {
        this.x = 0;
        this.y = 0;
        return this;
    }

    @Override
    public boolean isZero() {
        return (this.x == 0) && (this.y == 0);
    }

    @Override
    public boolean isZero(double error) {
        return len2() < (error * error);
    }

    @Override
    public boolean isUnit() {
        return this.isUnit(0.000000001);
    }

    @Override
    public boolean isUnit(double epsilon) {
        return FloatingPoint.isZero(Math.abs(len() - 1), epsilon);
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

    @Override
    public boolean equalsEpsilon(Vector2 v, double epsilon) {
        return (v != null) && (Math.abs(this.x - v.x) < epsilon) && (Math.abs(this.y - v.y) < epsilon);
    }

    public boolean equals(double tx, double ty, double epsilon) {
        return (Math.abs(this.x - tx) < epsilon) && (Math.abs(this.y - ty) < epsilon);
    }

    @Override
    public boolean isInline(Vector2 v) {
        return FloatingPoint.isZero((this.x * v.y) - (this.y * v.x));
    }

    @Override
    public boolean isInline(Vector2 v, double epsilon) {
        return FloatingPoint.isZero((this.x * v.y) - (this.y * v.x), epsilon);
    }

    @Override
    public boolean isColinear(Vector2 v) {
        return this.isInline(v) && (this.dot(v) > 0);
    }

    @Override
    public boolean isColinear(Vector2 v, double epsilon) {
        return this.isInline(v, epsilon) && (this.dot(v) > 0);
    }

    @Override
    public boolean isColinearOpposite(Vector2 v) {
        return this.isInline(v) && (this.dot(v) < 0);
    }

    @Override
    public boolean isColinearOpposite(Vector2 v, double epsilon) {
        return this.isInline(v, epsilon) && (this.dot(v) < 0);
    }

    @Override
    public boolean isPerpendicular(Vector2 v) {
        return FloatingPoint.isZero(dot(v));
    }

    @Override
    public boolean isPerpendicular(Vector2 v, double epsilon) {
        return FloatingPoint.isZero(dot(v), epsilon);
    }

    @Override
    public boolean inSameDirection(Vector2 v) {
        return dot(v) > 0;
    }

    @Override
    public boolean inOppositeDirection(Vector2 v) {
        return dot(v) < 0;
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", this.x, this.y);
    }
}
