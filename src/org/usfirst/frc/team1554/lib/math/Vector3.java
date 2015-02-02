package org.usfirst.frc.team1554.lib.math;

public class Vector3 implements Vector<Vector3> {

	public double x;
	public double y;
	public double z;

	public final static Vector3 X = new Vector3(1, 0, 0);
	public final static Vector3 Y = new Vector3(0, 1, 0);
	public final static Vector3 Z = new Vector3(0, 0, 1);
	public final static Vector3 Zero = new Vector3(0, 0, 0);

	private final static Matrix4 tmpMat = new Matrix4();

	public Vector3() {
	}

	public Vector3(double x, double y, double z) {
		this.set(x, y, z);
	}

	public Vector3(final Vector3 vector) {
		this.set(vector);
	}

	public Vector3(final double[] values) {
		this.set(values[0], values[1], values[2]);
	}

	public Vector3(final Vector2 vector, double z) {
		this.set(vector.x, vector.y, z);
	}

	public Vector3 set(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	@Override
	public Vector3 set(final Vector3 vector) {
		return this.set(vector.x, vector.y, vector.z);
	}

	public Vector3 set(final double[] values) {
		return this.set(values[0], values[1], values[2]);
	}

	public Vector3 set(final Vector2 vector, double z) {
		return this.set(vector.x, vector.y, z);
	}

	@Override
	public Vector3 cpy() {
		return new Vector3(this);
	}

	@Override
	public Vector3 add(final Vector3 vector) {
		return this.add(vector.x, vector.y, vector.z);
	}

	public Vector3 add(double x, double y, double z) {
		return this.set(this.x + x, this.y + y, this.z + z);
	}

	public Vector3 add(double values) {
		return this.set(this.x + values, this.y + values, this.z + values);
	}

	@Override
	public Vector3 sub(final Vector3 a_vec) {
		return this.sub(a_vec.x, a_vec.y, a_vec.z);
	}

	public Vector3 sub(double x, double y, double z) {
		return this.set(this.x - x, this.y - y, this.z - z);
	}

	public Vector3 sub(double value) {
		return this.set(this.x - value, this.y - value, this.z - value);
	}

	@Override
	public Vector3 scale(double scalar) {
		return this.set(this.x * scalar, this.y * scalar, this.z * scalar);
	}

	@Override
	public Vector3 scale(final Vector3 other) {
		return this.set(this.x * other.x, this.y * other.y, this.z * other.z);
	}

	public Vector3 scale(double vx, double vy, double vz) {
		return this.set(this.x * vx, this.y * vy, this.z * vz);
	}

	@Override
	public Vector3 mulAdd(Vector3 vec, double scalar) {
		this.x += vec.x * scalar;
		this.y += vec.y * scalar;
		this.z += vec.z * scalar;
		return this;
	}

	@Override
	public Vector3 mulAdd(Vector3 vec, Vector3 mulVec) {
		this.x += vec.x * mulVec.x;
		this.y += vec.y * mulVec.y;
		this.z += vec.z * mulVec.z;
		return this;
	}

	public static double len(final double x, final double y, final double z) {
		return Math.sqrt((x * x) + (y * y) + (z * z));
	}

	@Override
	public double len() {
		return Math.sqrt((this.x * this.x) + (this.y * this.y) + (this.z * this.z));
	}

	public static double len2(final double x, final double y, final double z) {
		return (x * x) + (y * y) + (z * z);
	}

	@Override
	public double len2() {
		return (this.x * this.x) + (this.y * this.y) + (this.z * this.z);
	}

	public boolean idt(final Vector3 vector) {
		return (this.x == vector.x) && (this.y == vector.y) && (this.z == vector.z);
	}

	public static double dst(final double x1, final double y1, final double z1, final double x2, final double y2, final double z2) {
		final double a = x2 - x1;
		final double b = y2 - y1;
		final double c = z2 - z1;
		return Math.sqrt((a * a) + (b * b) + (c * c));
	}

	@Override
	public double dst(final Vector3 vector) {
		final double a = vector.x - this.x;
		final double b = vector.y - this.y;
		final double c = vector.z - this.z;
		return Math.sqrt((a * a) + (b * b) + (c * c));
	}

	public double dst(double x, double y, double z) {
		final double a = x - this.x;
		final double b = y - this.y;
		final double c = z - this.z;
		return Math.sqrt((a * a) + (b * b) + (c * c));
	}

	public static double dst2(final double x1, final double y1, final double z1, final double x2, final double y2, final double z2) {
		final double a = x2 - x1;
		final double b = y2 - y1;
		final double c = z2 - z1;
		return (a * a) + (b * b) + (c * c);
	}

	@Override
	public double dst2(Vector3 point) {
		final double a = point.x - this.x;
		final double b = point.y - this.y;
		final double c = point.z - this.z;
		return (a * a) + (b * b) + (c * c);
	}

	public double dst2(double x, double y, double z) {
		final double a = x - this.x;
		final double b = y - this.y;
		final double c = z - this.z;
		return (a * a) + (b * b) + (c * c);
	}

	@Override
	public Vector3 nor() {
		final double len2 = this.len2();
		if ((len2 == 0f) || (len2 == 1f)) return this;
		return this.scale(1f / Math.sqrt(len2));
	}

	public static double dot(double x1, double y1, double z1, double x2, double y2, double z2) {
		return (x1 * x2) + (y1 * y2) + (z1 * z2);
	}

	@Override
	public double dot(final Vector3 vector) {
		return (this.x * vector.x) + (this.y * vector.y) + (this.z * vector.z);
	}

	public double dot(double x, double y, double z) {
		return (this.x * x) + (this.y * y) + (this.z * z);
	}

	public Vector3 crs(final Vector3 vector) {
		return this.set((this.y * vector.z) - (this.z * vector.y), (this.z * vector.x) - (this.x * vector.z), (this.x * vector.y) - (this.y * vector.x));
	}

	public Vector3 crs(double x, double y, double z) {
		return this.set((this.y * z) - (this.z * y), (this.z * x) - (this.x * z), (this.x * y) - (this.y * x));
	}

	public Vector3 mul4x3(double[] matrix) {
		return set((this.x * matrix[0]) + (this.y * matrix[3]) + (this.z * matrix[6]) + matrix[9], (this.x * matrix[1]) + (this.y * matrix[4]) + (this.z * matrix[7]) + matrix[10], (this.x * matrix[2]) + (this.y * matrix[5]) + (this.z * matrix[8]) + matrix[11]);
	}

	public Vector3 mul(final Matrix4 matrix) {
		final double l_mat[] = matrix.val;
		return this.set((this.x * l_mat[Matrix4.M_00]) + (this.y * l_mat[Matrix4.M_01]) + (this.z * l_mat[Matrix4.M_02]) + l_mat[Matrix4.M_03], (this.x * l_mat[Matrix4.M_10]) + (this.y * l_mat[Matrix4.M_11]) + (this.z * l_mat[Matrix4.M_12]) + l_mat[Matrix4.M_13], (this.x * l_mat[Matrix4.M_20]) + (this.y * l_mat[Matrix4.M_21]) + (this.z * l_mat[Matrix4.M_22]) + l_mat[Matrix4.M_23]);
	}

	public Vector3 traMul(final Matrix4 matrix) {
		final double l_mat[] = matrix.val;
		return this.set((this.x * l_mat[Matrix4.M_00]) + (this.y * l_mat[Matrix4.M_10]) + (this.z * l_mat[Matrix4.M_20]) + l_mat[Matrix4.M_30], (this.x * l_mat[Matrix4.M_01]) + (this.y * l_mat[Matrix4.M_11]) + (this.z * l_mat[Matrix4.M_21]) + l_mat[Matrix4.M_31], (this.x * l_mat[Matrix4.M_02]) + (this.y * l_mat[Matrix4.M_12]) + (this.z * l_mat[Matrix4.M_22]) + l_mat[Matrix4.M_32]);
	}

	public Vector3 mul(Matrix3 matrix) {
		final double l_mat[] = matrix.val;
		return set((this.x * l_mat[Matrix3.M00]) + (this.y * l_mat[Matrix3.M01]) + (this.z * l_mat[Matrix3.M02]), (this.x * l_mat[Matrix3.M10]) + (this.y * l_mat[Matrix3.M11]) + (this.z * l_mat[Matrix3.M12]), (this.x * l_mat[Matrix3.M20]) + (this.y * l_mat[Matrix3.M21]) + (this.z * l_mat[Matrix3.M22]));
	}

	public Vector3 traMul(Matrix3 matrix) {
		final double l_mat[] = matrix.val;
		return set((this.x * l_mat[Matrix3.M00]) + (this.y * l_mat[Matrix3.M10]) + (this.z * l_mat[Matrix3.M20]), (this.x * l_mat[Matrix3.M01]) + (this.y * l_mat[Matrix3.M11]) + (this.z * l_mat[Matrix3.M21]), (this.x * l_mat[Matrix3.M02]) + (this.y * l_mat[Matrix3.M12]) + (this.z * l_mat[Matrix3.M22]));
	}

	//
	// public Vector3 mul (final Quaternion quat) {
	// return quat.transform(this);
	// }

	public Vector3 prj(final Matrix4 matrix) {
		final double l_mat[] = matrix.val;
		final double l_w = 1f / ((this.x * l_mat[Matrix4.M_30]) + (this.y * l_mat[Matrix4.M_31]) + (this.z * l_mat[Matrix4.M_32]) + l_mat[Matrix4.M_33]);
		return this.set(((this.x * l_mat[Matrix4.M_00]) + (this.y * l_mat[Matrix4.M_01]) + (this.z * l_mat[Matrix4.M_02]) + l_mat[Matrix4.M_03]) * l_w, ((this.x * l_mat[Matrix4.M_10]) + (this.y * l_mat[Matrix4.M_11]) + (this.z * l_mat[Matrix4.M_12]) + l_mat[Matrix4.M_13]) * l_w, ((this.x * l_mat[Matrix4.M_20]) + (this.y * l_mat[Matrix4.M_21]) + (this.z * l_mat[Matrix4.M_22]) + l_mat[Matrix4.M_23]) * l_w);
	}

	public Vector3 rot(final Matrix4 matrix) {
		final double l_mat[] = matrix.val;
		return this.set((this.x * l_mat[Matrix4.M_00]) + (this.y * l_mat[Matrix4.M_01]) + (this.z * l_mat[Matrix4.M_02]), (this.x * l_mat[Matrix4.M_10]) + (this.y * l_mat[Matrix4.M_11]) + (this.z * l_mat[Matrix4.M_12]), (this.x * l_mat[Matrix4.M_20]) + (this.y * l_mat[Matrix4.M_21]) + (this.z * l_mat[Matrix4.M_22]));
	}

	public Vector3 unrotate(final Matrix4 matrix) {
		final double l_mat[] = matrix.val;
		return this.set((this.x * l_mat[Matrix4.M_00]) + (this.y * l_mat[Matrix4.M_10]) + (this.z * l_mat[Matrix4.M_20]), (this.x * l_mat[Matrix4.M_01]) + (this.y * l_mat[Matrix4.M_11]) + (this.z * l_mat[Matrix4.M_21]), (this.x * l_mat[Matrix4.M_02]) + (this.y * l_mat[Matrix4.M_12]) + (this.z * l_mat[Matrix4.M_22]));
	}

	public Vector3 untransform(final Matrix4 matrix) {
		final double l_mat[] = matrix.val;
		this.x -= l_mat[Matrix4.M_03];
		this.y -= l_mat[Matrix4.M_03];
		this.z -= l_mat[Matrix4.M_03];
		return this.set((this.x * l_mat[Matrix4.M_00]) + (this.y * l_mat[Matrix4.M_10]) + (this.z * l_mat[Matrix4.M_20]), (this.x * l_mat[Matrix4.M_01]) + (this.y * l_mat[Matrix4.M_11]) + (this.z * l_mat[Matrix4.M_21]), (this.x * l_mat[Matrix4.M_02]) + (this.y * l_mat[Matrix4.M_12]) + (this.z * l_mat[Matrix4.M_22]));
	}

	public Vector3 rotate(double degrees, double axisX, double axisY, double axisZ) {
		return this.mul(tmpMat.setToRotation(axisX, axisY, axisZ, degrees));
	}

	public Vector3 rotateRad(double radians, double axisX, double axisY, double axisZ) {
		return this.mul(tmpMat.setToRotationRad(axisX, axisY, axisZ, radians));
	}

	public Vector3 rotate(final Vector3 axis, double degrees) {
		tmpMat.setToRotation(axis, degrees);
		return this.mul(tmpMat);
	}

	public Vector3 rotateRad(final Vector3 axis, double radians) {
		tmpMat.setToRotationRad(axis, radians);
		return this.mul(tmpMat);
	}

	@Override
	public boolean isUnit() {
		return isUnit(0.000000001);
	}

	@Override
	public boolean isUnit(final double margin) {
		return Math.abs(len2() - 1) < margin;
	}

	@Override
	public boolean isZero() {
		return (this.x == 0) && (this.y == 0) && (this.z == 0);
	}

	@Override
	public boolean isZero(final double margin) {
		return len2() < margin;
	}

	@Override
	public boolean isInline(Vector3 other, double epsilon) {
		return len2((this.y * other.z) - (this.z * other.y), (this.z * other.x) - (this.x * other.z), (this.x * other.y) - (this.y * other.x)) <= epsilon;
	}

	@Override
	public boolean isInline(Vector3 other) {
		return len2((this.y * other.z) - (this.z * other.y), (this.z * other.x) - (this.x * other.z), (this.x * other.y) - (this.y * other.x)) <= FloatingPoint.ROUNDING_ERROR_64_BITS;
	}

	@Override
	public boolean isColinear(Vector3 other, double epsilon) {
		return isInline(other, epsilon) && inSameDirection(other);
	}

	@Override
	public boolean isColinear(Vector3 other) {
		return isInline(other) && inSameDirection(other);
	}

	@Override
	public boolean isColinearOpposite(Vector3 other, double epsilon) {
		return isInline(other, epsilon) && inOppositeDirection(other);
	}

	@Override
	public boolean isColinearOpposite(Vector3 other) {
		return isInline(other) && inOppositeDirection(other);
	}

	@Override
	public boolean isPerpendicular(Vector3 vector) {
		return FloatingPoint.isZero(dot(vector));
	}

	@Override
	public boolean isPerpendicular(Vector3 vector, double epsilon) {
		return FloatingPoint.isZero(dot(vector), epsilon);
	}

	@Override
	public boolean inSameDirection(Vector3 vector) {
		return dot(vector) > 0;
	}

	@Override
	public boolean inOppositeDirection(Vector3 vector) {
		return dot(vector) < 0;
	}

	@Override
	public Vector3 lerp(final Vector3 target, double alpha) {
		scale(1.0f - alpha);
		add(target.x * alpha, target.y * alpha, target.z * alpha);
		return this;
	}

	public Vector3 slerp(final Vector3 target, double alpha) {
		final double dot = dot(target);
		// If the inputs are too close for comfort, simply linearly interpolate.
		if ((dot > 0.9995) || (dot < -0.9995)) return lerp(target, alpha);

		// theta0 = angle between input vectors
		final double theta0 = Math.acos(dot);
		// theta = angle between this vector and result
		final double theta = theta0 * alpha;

		final double st = Math.sin(theta);
		final double tx = target.x - (this.x * dot);
		final double ty = target.y - (this.y * dot);
		final double tz = target.z - (this.z * dot);
		final double l2 = (tx * tx) + (ty * ty) + (tz * tz);
		final double dl = st * ((l2 < 0.0001f) ? 1f : 1f / Math.sqrt(l2));

		return scale(Math.cos(theta)).add(tx * dl, ty * dl, tz * dl).nor();
	}

	@Override
	public String toString() {
		return "[" + this.x + ", " + this.y + ", " + this.z + "]";
	}

	@Override
	public Vector3 limit(double limit) {
		return limit2(limit * limit);
	}

	@Override
	public Vector3 limit2(double limit2) {
		final double len2 = len2();
		if (len2 > limit2) {
			scale(limit2 / len2);
		}
		return this;
	}

	@Override
	public Vector3 setLength(double len) {
		return setLength2(len * len);
	}

	@Override
	public Vector3 setLength2(double len2) {
		final double oldLen2 = len2();
		return ((oldLen2 == 0) || (oldLen2 == len2)) ? this : scale(Math.sqrt(len2 / oldLen2));
	}

	@Override
	public Vector3 clamp(double min, double max) {
		final double len2 = len2();
		if (len2 == 0f) return this;
		final double max2 = max * max;
		if (len2 > max2) return scale(Math.sqrt(max2 / len2));
		final double min2 = min * min;
		if (len2 < min2) return scale(Math.sqrt(min2 / len2));
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (int) ((prime * result) + FloatingPoint.toLongBits(this.x));
		result = (int) ((prime * result) + FloatingPoint.toLongBits(this.y));
		result = (int) ((prime * result) + FloatingPoint.toLongBits(this.z));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final Vector3 other = (Vector3) obj;
		if (FloatingPoint.toLongBits(this.x) != FloatingPoint.toLongBits(other.x)) return false;
		if (FloatingPoint.toLongBits(this.y) != FloatingPoint.toLongBits(other.y)) return false;
		if (FloatingPoint.toLongBits(this.z) != FloatingPoint.toLongBits(other.z)) return false;
		return true;
	}

	@Override
	public boolean equals(final Vector3 other, double epsilon) {
		if (other == null) return false;
		if (Math.abs(other.x - this.x) > epsilon) return false;
		if (Math.abs(other.y - this.y) > epsilon) return false;
		if (Math.abs(other.z - this.z) > epsilon) return false;
		return true;
	}

	public boolean equals(double x, double y, double z, double epsilon) {
		if (Math.abs(x - this.x) > epsilon) return false;
		if (Math.abs(y - this.y) > epsilon) return false;
		if (Math.abs(z - this.z) > epsilon) return false;
		return true;
	}

	@Override
	public Vector3 zero() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
		return this;
	}

}
