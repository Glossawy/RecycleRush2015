package org.usfirst.frc.team1554.lib.math;

import org.usfirst.frc.team1554.lib.util.Preconditions;

public class Quaternion {

    public enum Pole {
        NORTH(1), SOUTH(-1), NONE(0);

        public final int POLE_VAL;

        private Pole(int assignment) {
            this.POLE_VAL = assignment;
        }
    }

    private static Quaternion temp1 = new Quaternion(0, 0, 0, 0);
    private static Quaternion temp2 = new Quaternion(0, 0, 0, 0);

    public double x, y, z, w;

    public Quaternion(double x, double y, double z, double w) {
        this.set(x, y, z, w);
    }

    public Quaternion() {
        identity();
    }

    public Quaternion(Quaternion quaternion) {
        this.set(quaternion);
    }

    public Quaternion(Vector3 axis, double rotation) {
        this.set(axis, rotation);
    }

    public Quaternion set(Vector3 axis, double rotation) {
        return setFromAxis(axis.x, axis.y, axis.z, rotation);
    }

    public Quaternion set(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;

        return this;
    }

    public Quaternion set(Quaternion quat) {
        return set(quat.x, quat.y, quat.z, quat.w);
    }

    public Quaternion setEulerAngles(double yaw, double pitch, double roll) {
        return setEulerAnglesRadians(Math.toRadians(yaw), Math.toRadians(pitch), Math.toRadians(roll));
    }

    public Quaternion setEulerAnglesRadians(double yaw, double pitch, double roll) {
        final double hr = roll / 2;
        final double sinhr = Math.sin(hr);
        final double coshr = Math.cos(hr);

        final double hp = pitch / 2;
        final double sinhp = Math.sin(hp);
        final double coshp = Math.cos(hp);

        final double hy = yaw / 2;
        final double sinhy = Math.sin(hy);
        final double coshy = Math.cos(hy);

        final double coshy_sinhp = coshy * sinhp;
        final double sinhy_coshp = sinhy * coshp;
        final double coshy_coshp = coshy * coshp;
        final double sinhy_sinhp = sinhy * sinhp;

        this.x = (coshy_sinhp * coshr) + (sinhy_coshp * sinhr);
        this.y = (sinhy_coshp * coshr) - (coshy_sinhp * sinhr);
        this.z = (coshy_coshp * sinhr) - (sinhy_sinhp * coshr);
        this.w = (coshy_coshp * coshr) + (sinhy_sinhp * sinhr);
        return this;
    }

    public Pole getGimbalPole() {
        final double det = (this.y * this.x) + (this.z * this.w);
        return det > 0.499 ? Pole.NORTH : det < 0.499 ? Pole.SOUTH : Pole.NONE;
    }

    public double getRoll() {
        return Math.toDegrees(getRollRadians());
    }

    public double getRollRadians() {
        final int p = getGimbalPole().POLE_VAL;
        return p == 0 ? Math.atan2(2 * ((this.w * this.z) + (this.y * this.x)), 1 - (2 * ((this.x * this.x) + (this.z * this.z)))) : p * 2 * Math.atan2(this.y, this.w);
    }

    public double getPitch() {
        return Math.toDegrees(getPitchRadians());
    }

    public double getPitchRadians() {
        final int p = getGimbalPole().POLE_VAL;
        return p == 0 ? Math.asin(MathUtils.clamp(2 * ((this.w * this.x) - (this.z * this.y)), -1, 1)) : p * Math.PI * 0.5;
    }

    public double getYaw() {
        return Math.toDegrees(getYawRadians());
    }

    public double getYawRadians() {
        final int p = getGimbalPole().POLE_VAL;
        return p == 0 ? Math.atan2(2 * ((this.y * this.w) + (this.x * this.z)), 1 - (2 * ((this.y * this.y) + (this.x * this.x)))) : 0;
    }

    public Quaternion cpy() {
        return new Quaternion(this);
    }

    public final static double len(double x, double y, double z, double w) {
        return Math.sqrt((x * x) + (y * y) + (z * z) + (w * w));
    }

    public double len() {
        return Quaternion.len(this.x, this.y, this.z, this.w);
    }

    public double len2() {
        return (this.x * this.x) + (this.y * this.y) + (this.z * this.z) + (this.w * this.w);
    }

    public Quaternion nor() {
        double len = len2();
        if ((len != 0) && !FloatingPoint.isEqual(len, 1.0)) {
            len = Math.sqrt(len);
            this.x /= len;
            this.y /= len;
            this.z /= len;
            this.w /= len;
        }

        return this;
    }

    public Quaternion conjugate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        return this;
    }

    public Vector3 transform(Vector3 v) {
        temp2.set(this);
        temp2.conjugate();
        temp2.mulLeft(temp1.set(v.x, v.y, v.z, 0)).mulLeft(this);

        v.x = temp2.x;
        v.y = temp2.y;
        v.z = temp2.z;
        return v;
    }

    public Quaternion mul(Quaternion q) {
        final double newx = ((this.w * q.x) + (this.x * q.w) + (this.y * q.z)) - (this.z * q.y);
        final double newy = ((this.w * q.y) + (this.y * q.w) + (this.z * q.x)) - (this.x * q.z);
        final double newz = ((this.w * q.z) + (this.z * q.w) + (this.x * q.y)) - (this.y * q.x);
        final double neww = (this.w * q.w) - (this.x * q.x) - (this.y * q.y) - (this.z * q.z);

        this.x = newx;
        this.y = newy;
        this.z = newz;
        this.w = neww;
        return this;
    }

    public Quaternion mul(double x, double y, double z, double w) {
        return mul(new Quaternion(x, y, z, w));
    }

    public Quaternion mulLeft(Quaternion q) {
        final double newx = ((q.w * this.x) + (q.x * this.w) + (q.y * this.z)) - (q.z * this.y);
        final double newy = ((q.w * this.y) + (q.y * this.w) + (q.z * this.x)) - (q.x * this.z);
        final double newz = ((q.w * this.z) + (q.z * this.w) + (q.x * this.y)) - (q.y * this.x);
        final double neww = (q.w * this.w) - (q.x * this.x) - (q.y * this.y) - (q.z * this.z);

        this.x = newx;
        this.y = newy;
        this.z = newz;
        this.w = neww;
        return this;
    }

    public Quaternion mulLeft(double x, double y, double z, double w) {
        return mulLeft(new Quaternion(x, y, z, w));
    }

    public Quaternion add(Quaternion quaternion) {
        this.x += quaternion.x;
        this.y += quaternion.y;
        this.z += quaternion.z;
        this.w += quaternion.w;
        return this;
    }

    public Quaternion add(double qx, double qy, double qz, double qw) {
        this.x += qx;
        this.y += qy;
        this.z += qz;
        this.w += qw;
        return this;
    }

    public void toMatrix(double[] matrix) {
        Preconditions.checkExpression(matrix.length == 16, "Matrix Must be of size 16! Matrix Not Large Enough!: Size = " + matrix.length);

        final double xx = this.x * this.x;
        final double xy = this.x * this.y;
        final double xz = this.x * this.z;
        final double xw = this.x * this.w;
        final double yy = this.y * this.y;
        final double yz = this.y * this.z;
        final double yw = this.y * this.w;
        final double zz = this.z * this.z;
        final double zw = this.z * this.w;

        matrix[Matrix4.M_00] = 1 - (2 * (yy + zz));
        matrix[Matrix4.M_01] = 2 * (xy - zw);
        matrix[Matrix4.M_02] = 2 * (xz + yw);
        matrix[Matrix4.M_03] = 0;
        matrix[Matrix4.M_10] = 2 * (xy + zw);
        matrix[Matrix4.M_11] = 1 - (2 * (xx + zz));
        matrix[Matrix4.M_12] = 2 * (yz - xw);
        matrix[Matrix4.M_13] = 0;
        matrix[Matrix4.M_20] = 2 * (xz - yw);
        matrix[Matrix4.M_21] = 2 * (yz + xw);
        matrix[Matrix4.M_22] = 1 - (2 * (xx + yy));
        matrix[Matrix4.M_23] = 0;
        matrix[Matrix4.M_30] = 0;
        matrix[Matrix4.M_31] = 0;
        matrix[Matrix4.M_32] = 0;
        matrix[Matrix4.M_33] = 1;

    }

    public Quaternion identity() {
        return this.set(0, 0, 0, 1);
    }

    public boolean isIdentity() {
        return FloatingPoint.isZero(this.x) && FloatingPoint.isZero(this.y) && FloatingPoint.isZero(this.z) && FloatingPoint.isEqual(this.w, 1.0);
    }

    public boolean isIdentity(double tolerance) {
        return FloatingPoint.isZero(this.x, tolerance) && FloatingPoint.isZero(this.y, tolerance) && FloatingPoint.isZero(this.z, tolerance) && FloatingPoint.isEqual(this.w, 1.0, tolerance);
    }

    public Quaternion setFromAxis(Vector3 axis, double degrees) {
        return setFromAxis(axis.x, axis.y, axis.z, degrees);
    }

    public Quaternion setFromAxisRad(Vector3 axis, double radians) {
        return setFromAxisRad(axis.x, axis.y, axis.z, radians);
    }

    public Quaternion setFromAxis(double x, double y, double z, double degrees) {
        return setFromAxisRad(x, y, z, Math.toRadians(degrees));
    }

    public Quaternion setFromAxisRad(double x, double y, double z, double rad) {
        final double det = Vector3.len(x, y, z);
        if (det == 0.0) return identity();

        final double PI2 = Math.PI * 2;
        final double lenAng = rad < 0 ? PI2 - (-rad % PI2) : rad % PI2;
        final double lenSin = Math.sin(lenAng / 2);
        final double lenCos = Math.cos(lenAng / 2);

        return set(det * x * lenSin, det * y * lenSin, det * z * lenSin, lenCos).nor();
    }

    public Quaternion setFromMatrix(boolean normalizeAxes, Matrix4 matrix) {
        return setFromAxes(normalizeAxes, matrix.val[Matrix4.M_00], matrix.val[Matrix4.M_01], matrix.val[Matrix4.M_02], matrix.val[Matrix4.M_10], matrix.val[Matrix4.M_11], matrix.val[Matrix4.M_12], matrix.val[Matrix4.M_20], matrix.val[Matrix4.M_21], matrix.val[Matrix4.M_22]);
    }

    public Quaternion setFromMatrix(Matrix4 matrix) {
        return setFromMatrix(false, matrix);
    }

    public Quaternion setFromMatrix(boolean normalizeAxes, Matrix3 matrix) {
        return setFromAxes(normalizeAxes, matrix.val[Matrix3.M00], matrix.val[Matrix3.M01], matrix.val[Matrix3.M02], matrix.val[Matrix3.M10], matrix.val[Matrix3.M11], matrix.val[Matrix3.M12], matrix.val[Matrix3.M20], matrix.val[Matrix3.M21], matrix.val[Matrix3.M22]);
    }

    public Quaternion setFromMatrix(Matrix3 matrix) {
        return setFromMatrix(false, matrix);
    }

    @Override
    public String toString() {
        return String.format("(%.03f, %.03f, %.03f, %.03f)", this.x, this.y, this.z, this.w);
    }

    public Quaternion setFromAxes(double xx, double xy, double xz, double yx, double yy, double yz, double zx, double zy, double zz) {
        return setFromAxes(false, xx, xy, xz, yx, yy, yz, zx, zy, zz);
    }

    public Quaternion setFromAxes(boolean normalizeAxes, double xx, double xy, double xz, double yx, double yy, double yz, double zx, double zy, double zz) {
        if (normalizeAxes) {
            final double lx = 1 / Vector3.len(xx, xy, xz);
            final double ly = 1 / Vector3.len(yx, yy, yz);
            final double lz = 1 / Vector3.len(zx, zy, zz);
            xx *= lx;
            xy *= lx;
            xz *= lx;
            yz *= ly;
            yy *= ly;
            yz *= ly;
            zx *= lz;
            zy *= lz;
            zz *= lz;
        }
        // the trace is the sum of the diagonal elements; see
        // http://mathworld.wolfram.com/MatrixTrace.html
        final double t = xx + yy + zz;

        // we protect the division by s by ensuring that s>=1
        if (t >= 0) { // |w| >= .5
            double s = Math.sqrt(t + 1); // |s|>=1 ...
            this.w = 0.5 * s;
            s = 0.5 / s; // so this division isn't bad
            this.x = (zy - yz) * s;
            this.y = (xz - zx) * s;
            this.z = (yx - xy) * s;
        } else if ((xx > yy) && (xx > zz)) {
            double s = Math.sqrt((1.0 + xx) - yy - zz); // |s|>=1
            this.x = s * 0.5; // |x| >= .5
            s = 0.5 / s;
            this.y = (yx + xy) * s;
            this.z = (xz + zx) * s;
            this.w = (zy - yz) * s;
        } else if (yy > zz) {
            double s = Math.sqrt((1.0 + yy) - xx - zz); // |s|>=1
            this.y = s * 0.5f; // |y| >= .5
            s = 0.5 / s;
            this.x = (yx + xy) * s;
            this.z = (zy + yz) * s;
            this.w = (xz - zx) * s;
        } else {
            double s = Math.sqrt((1.0 + zz) - xx - yy); // |s|>=1
            this.z = s * 0.5f; // |z| >= .5
            s = 0.5 / s;
            this.x = (xz + zx) * s;
            this.y = (zy + yz) * s;
            this.w = (yx - xy) * s;
        }

        return this;
    }

    public Quaternion setFromCross(final Vector3 v1, final Vector3 v2) {
        final double dot = MathUtils.clamp(v1.dot(v2), -1, 1);
        final double angle = Math.acos(dot);
        return setFromAxisRad((v1.y * v2.z) - (v1.z * v2.y), (v1.z * v2.x) - (v1.x * v2.z), (v1.x * v2.y) - (v1.y * v2.x), angle);
    }

    public Quaternion setFromCross(final double x1, final double y1, final double z1, final double x2, final double y2, final double z2) {
        final double dot = MathUtils.clamp(Vector3.dot(x1, y1, z1, x2, y2, z2), -1, 1);
        final double angle = Math.acos(dot);
        return setFromAxisRad((y1 * z2) - (z1 * y2), (z1 * x2) - (x1 * z2), (x1 * y2) - (y1 * x2), angle);
    }

    public Quaternion slerp(Quaternion end, double alpha) {
        final double dot = dot(end);
        final double absDot = dot < 0.0 ? -dot : dot;

        // Set the first and second scale for the interpolation
        double scale0 = 1 - alpha;
        double scale1 = alpha;

        // Check if the angle between the 2 quaternions was big enough to
        // warrant such calculations
        if ((1 - absDot) > 0.1) {// Get the angle between the 2 quaternions,
            // and then store the sin() of that angle
            final double angle = Math.acos(absDot);
            final double invSinTheta = 1f / Math.sin(angle);

            // Calculate the scale for q1 and q2, according to the angle and
            // it's sine value
            scale0 = Math.sin((1 - alpha) * angle) * invSinTheta;
            scale1 = Math.sin(alpha * angle) * invSinTheta;
        }

        if (dot < 0.0) {
            scale1 = -scale1;
        }

        // Calculate the x, y, z and w values for the quaternion by using a
        // special form of linear interpolation for quaternions.
        this.x = (scale0 * this.x) + (scale1 * end.x);
        this.y = (scale0 * this.y) + (scale1 * end.y);
        this.z = (scale0 * this.z) + (scale1 * end.z);
        this.w = (scale0 * this.w) + (scale1 * end.w);

        // Return the interpolated quaternion
        return this;
    }

    public Quaternion slerp(Quaternion[] q) {

        // Calculate exponents and multiply everything from left to right
        final double w = 1.0 / q.length;
        set(q[0]).exp(w);
        for (int i = 1; i < q.length; i++) {
            mul(temp1.set(q[i]).exp(w));
        }
        nor();
        return this;
    }

    public Quaternion slerp(Quaternion[] q, double[] w) {

        // Calculate exponents and multiply everything from left to right
        set(q[0]).exp(w[0]);
        for (int i = 1; i < q.length; i++) {
            mul(temp1.set(q[i]).exp(w[i]));
        }
        nor();
        return this;
    }

    public Quaternion exp(double alpha) {

        // Calculate |q|^alpha
        final double norm = len();
        final double normExp = Math.pow(norm, alpha);

        // Calculate theta
        final double theta = Math.acos(this.w / norm);

        // Calculate coefficient of basis elements
        double coeff = 0;
        if (Math.abs(theta) < 0.001) {
            coeff = (normExp * alpha) / norm;
        } else {
            coeff = (normExp * Math.sin(alpha * theta)) / (norm * Math.sin(theta));
        }

        // Write results
        this.w = normExp * Math.cos(alpha * theta);
        this.x *= coeff;
        this.y *= coeff;
        this.z *= coeff;

        nor();

        return this;
    }

    public final static double dot(final double x1, final double y1, final double z1, final double w1, final double x2, final double y2, final double z2, final double w2) {
        return (x1 * x2) + (y1 * y2) + (z1 * z2) + (w1 * w2);
    }

    public double dot(final Quaternion other) {
        return (this.x * other.x) + (this.y * other.y) + (this.z * other.z) + (this.w * other.w);
    }

    public double dot(final double x, final double y, final double z, final double w) {
        return (this.x * x) + (this.y * y) + (this.z * z) + (this.w * w);
    }

    public Quaternion mul(double scalar) {
        this.x *= scalar;
        this.y *= scalar;
        this.z *= scalar;
        this.w *= scalar;
        return this;
    }
}
