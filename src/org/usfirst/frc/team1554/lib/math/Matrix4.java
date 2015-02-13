package org.usfirst.frc.team1554.lib.math;

public class Matrix4 {

	public static final int M_00 = 0, M_10 = 1, M_20 = 2, M_30 = 3, M_01 = 4, M_11 = 5, M_21 = 6, M_31 = 7, M_02 = 8, M_12 = 9, M_22 = 10, M_32 = 11, M_03 = 12, M_13 = 13, M_23 = 14, M_33 = 15;

	public final double val[] = new double[16];

	public Matrix4() {
		this.val[M_00] = 1.0;
		this.val[M_11] = 1.0;
		this.val[M_22] = 1.0;
		this.val[M_33] = 1.0;
	}

	public Matrix4(Matrix4 mat) {
		this.set(mat);
	}

	public Matrix4(Quaternion quaternion) {
		this.set(quaternion);
	}

	public Matrix4(Vector3 position, Quaternion rotation, Vector3 scale) {
		this.set(position, rotation, scale);
	}

	public Matrix4 set(Matrix4 mat) {
		return set(mat.val);
	}

	public Matrix4 set(double[] values) {
		System.arraycopy(values, 0, this.val, 0, this.val.length);
		return this;
	}

	public Matrix4 set(Quaternion quaternion) {
		return set(quaternion.x, quaternion.y, quaternion.z, quaternion.w);
	}

	public Matrix4 set(Vector3 position, Quaternion rotation) {
		return set(position.x, position.y, position.z, rotation.x, rotation.y, rotation.z, rotation.w);
	}

	public Matrix4 set(double quaternionX, double quaternionY, double quaternionZ, double quaternionW) {
		return set(0.0, 0.0, 0.0, quaternionX, quaternionY, quaternionZ, quaternionW);
	}

	public Matrix4 set(double trnX, double trnY, double trnZ, double quatX, double quatY, double quatZ, double quatW) {
		final double xs = quatX * 2, ys = quatY * 2, zs = quatZ * 2;
		final double wx = quatW * xs, wy = quatW * ys, wz = quatW * zs;
		final double xx = quatX * xs, xy = quatX * ys, xz = quatX * zs;
		final double yy = quatY * ys, yz = quatY * zs, zz = quatZ * zs;

		this.val[M_00] = 1.0 - (yy + zz);
		this.val[M_01] = xy - wz;
		this.val[M_02] = xz + wy;
		this.val[M_03] = trnX;

		this.val[M_10] = xy + wz;
		this.val[M_11] = 1.0 - (xx + zz);
		this.val[M_12] = yz - wx;
		this.val[M_13] = trnY;

		this.val[M_20] = xz - wy;
		this.val[M_21] = yz + wx;
		this.val[M_22] = 1.0 - (xx + yy);
		this.val[M_23] = trnZ;

		this.val[M_30] = 0.0;
		this.val[M_31] = 0.0;
		this.val[M_32] = 0.0;
		this.val[M_33] = 1.0;
		return this;
	}

	public Matrix4 set(Vector3 pos, Quaternion rot, Vector3 scl) {
		return set(pos.x, pos.y, pos.z, rot.x, rot.y, rot.z, rot.w, scl.x, scl.y, scl.z);
	}

	public Matrix4 set(double trnX, double trnY, double trnZ, double quatX, double quatY, double quatZ, double quat, double sclX, double sclY, double sclZ) {
		final double xs = quatX * 2, ys = quatY * 2, zs = quatZ * 2;
		final double wx = quat * xs, wy = quat * ys, wz = quat * zs;
		final double xx = quatX * xs, xy = quatX * ys, xz = quatX * zs;
		final double yy = quatY * ys, yz = quatY * zs, zz = quatZ * zs;

		this.val[M_00] = sclX * (1.0 - (yy + zz));
		this.val[M_01] = sclY * (xy - wz);
		this.val[M_02] = sclZ * (xz + wy);
		this.val[M_03] = trnX;

		this.val[M_10] = sclX * (xy + wz);
		this.val[M_11] = sclY * (1.0 - (xx + zz));
		this.val[M_12] = sclZ * (yz - wx);
		this.val[M_13] = trnY;

		this.val[M_20] = sclX * (xz - wy);
		this.val[M_21] = sclY * (yz + wx);
		this.val[M_22] = sclZ * (1.0 - (xx + yy));
		this.val[M_23] = trnZ;

		this.val[M_30] = 0.0;
		this.val[M_31] = 0.0;
		this.val[M_32] = 0.0;
		this.val[M_33] = 1.0;
		return this;
	}

	public Matrix4 set(Vector3 xAxis, Vector3 yAxis, Vector3 zAxis, Vector3 pos) {
		this.val[M_00] = xAxis.x;
		this.val[M_01] = xAxis.y;
		this.val[M_02] = xAxis.z;
		this.val[M_10] = yAxis.x;
		this.val[M_11] = yAxis.y;
		this.val[M_12] = yAxis.z;
		this.val[M_20] = zAxis.x;
		this.val[M_21] = zAxis.y;
		this.val[M_22] = zAxis.z;
		this.val[M_03] = pos.x;
		this.val[M_13] = pos.y;
		this.val[M_23] = pos.z;
		this.val[M_30] = 0;
		this.val[M_31] = 0;
		this.val[M_32] = 0;
		this.val[M_33] = 1;
		return this;
	}

	public Matrix4 cpy() {
		return new Matrix4(this);
	}

	public Matrix4 trn(Vector3 vector) {
		this.val[M_03] += vector.x;
		this.val[M_13] += vector.y;
		this.val[M_23] += vector.z;
		return this;
	}

	public Matrix4 trn(double x, double y, double z) {
		this.val[M_03] += x;
		this.val[M_13] += y;
		this.val[M_23] += z;
		return this;
	}

	public double[] getValues() {
		return this.val;
	}

	public Matrix4 tra() {
		final double[] tmp = new double[16];
		tmp[M_00] = this.val[M_00];
		tmp[M_01] = this.val[M_10];
		tmp[M_02] = this.val[M_20];
		tmp[M_03] = this.val[M_30];
		tmp[M_10] = this.val[M_01];
		tmp[M_11] = this.val[M_11];
		tmp[M_12] = this.val[M_21];
		tmp[M_13] = this.val[M_31];
		tmp[M_20] = this.val[M_02];
		tmp[M_21] = this.val[M_12];
		tmp[M_22] = this.val[M_22];
		tmp[M_23] = this.val[M_32];
		tmp[M_30] = this.val[M_03];
		tmp[M_31] = this.val[M_13];
		tmp[M_32] = this.val[M_23];
		tmp[M_33] = this.val[M_33];
		return set(tmp);
	}

	public Matrix4 idt() {
		this.val[M_00] = 1;
		this.val[M_01] = 0;
		this.val[M_02] = 0;
		this.val[M_03] = 0;
		this.val[M_10] = 0;
		this.val[M_11] = 1;
		this.val[M_12] = 0;
		this.val[M_13] = 0;
		this.val[M_20] = 0;
		this.val[M_21] = 0;
		this.val[M_22] = 1;
		this.val[M_23] = 0;
		this.val[M_30] = 0;
		this.val[M_31] = 0;
		this.val[M_32] = 0;
		this.val[M_33] = 1;
		return this;
	}

	public Matrix4 inv() {
		final double[] tmp = new double[16];
		final double l_det = this.val[M_30] * this.val[M_21] * this.val[M_12] * this.val[M_03] - this.val[M_20] * this.val[M_31] * this.val[M_12] * this.val[M_03] - this.val[M_30] * this.val[M_11] * this.val[M_22] * this.val[M_03] + this.val[M_10] * this.val[M_31] * this.val[M_22] * this.val[M_03] + this.val[M_20] * this.val[M_11] * this.val[M_32] * this.val[M_03] - this.val[M_10] * this.val[M_21] * this.val[M_32] * this.val[M_03] - this.val[M_30] * this.val[M_21] * this.val[M_02] * this.val[M_13] + this.val[M_20] * this.val[M_31] * this.val[M_02] * this.val[M_13] + this.val[M_30] * this.val[M_01] * this.val[M_22] * this.val[M_13] - this.val[M_00] * this.val[M_31] * this.val[M_22] * this.val[M_13] - this.val[M_20] * this.val[M_01] * this.val[M_32] * this.val[M_13] + this.val[M_00] * this.val[M_21] * this.val[M_32] * this.val[M_13] + this.val[M_30] * this.val[M_11] * this.val[M_02] * this.val[M_23] - this.val[M_10] * this.val[M_31] * this.val[M_02] * this.val[M_23] - this.val[M_30]
				* this.val[M_01] * this.val[M_12] * this.val[M_23] + this.val[M_00] * this.val[M_31] * this.val[M_12] * this.val[M_23] + this.val[M_10] * this.val[M_01] * this.val[M_32] * this.val[M_23] - this.val[M_00] * this.val[M_11] * this.val[M_32] * this.val[M_23] - this.val[M_20] * this.val[M_11] * this.val[M_02] * this.val[M_33] + this.val[M_10] * this.val[M_21] * this.val[M_02] * this.val[M_33] + this.val[M_20] * this.val[M_01] * this.val[M_12] * this.val[M_33] - this.val[M_00] * this.val[M_21] * this.val[M_12] * this.val[M_33] - this.val[M_10] * this.val[M_01] * this.val[M_22] * this.val[M_33] + this.val[M_00] * this.val[M_11] * this.val[M_22] * this.val[M_33];
		if (l_det == 0f)
			throw new RuntimeException("non-invertible matrix");
		final double inv_det = 1.0f / l_det;
		tmp[M_00] = this.val[M_12] * this.val[M_23] * this.val[M_31] - this.val[M_13] * this.val[M_22] * this.val[M_31] + this.val[M_13] * this.val[M_21] * this.val[M_32] - this.val[M_11] * this.val[M_23] * this.val[M_32] - this.val[M_12] * this.val[M_21] * this.val[M_33] + this.val[M_11] * this.val[M_22] * this.val[M_33];
		tmp[M_01] = this.val[M_03] * this.val[M_22] * this.val[M_31] - this.val[M_02] * this.val[M_23] * this.val[M_31] - this.val[M_03] * this.val[M_21] * this.val[M_32] + this.val[M_01] * this.val[M_23] * this.val[M_32] + this.val[M_02] * this.val[M_21] * this.val[M_33] - this.val[M_01] * this.val[M_22] * this.val[M_33];
		tmp[M_02] = this.val[M_02] * this.val[M_13] * this.val[M_31] - this.val[M_03] * this.val[M_12] * this.val[M_31] + this.val[M_03] * this.val[M_11] * this.val[M_32] - this.val[M_01] * this.val[M_13] * this.val[M_32] - this.val[M_02] * this.val[M_11] * this.val[M_33] + this.val[M_01] * this.val[M_12] * this.val[M_33];
		tmp[M_03] = this.val[M_03] * this.val[M_12] * this.val[M_21] - this.val[M_02] * this.val[M_13] * this.val[M_21] - this.val[M_03] * this.val[M_11] * this.val[M_22] + this.val[M_01] * this.val[M_13] * this.val[M_22] + this.val[M_02] * this.val[M_11] * this.val[M_23] - this.val[M_01] * this.val[M_12] * this.val[M_23];
		tmp[M_10] = this.val[M_13] * this.val[M_22] * this.val[M_30] - this.val[M_12] * this.val[M_23] * this.val[M_30] - this.val[M_13] * this.val[M_20] * this.val[M_32] + this.val[M_10] * this.val[M_23] * this.val[M_32] + this.val[M_12] * this.val[M_20] * this.val[M_33] - this.val[M_10] * this.val[M_22] * this.val[M_33];
		tmp[M_11] = this.val[M_02] * this.val[M_23] * this.val[M_30] - this.val[M_03] * this.val[M_22] * this.val[M_30] + this.val[M_03] * this.val[M_20] * this.val[M_32] - this.val[M_00] * this.val[M_23] * this.val[M_32] - this.val[M_02] * this.val[M_20] * this.val[M_33] + this.val[M_00] * this.val[M_22] * this.val[M_33];
		tmp[M_12] = this.val[M_03] * this.val[M_12] * this.val[M_30] - this.val[M_02] * this.val[M_13] * this.val[M_30] - this.val[M_03] * this.val[M_10] * this.val[M_32] + this.val[M_00] * this.val[M_13] * this.val[M_32] + this.val[M_02] * this.val[M_10] * this.val[M_33] - this.val[M_00] * this.val[M_12] * this.val[M_33];
		tmp[M_13] = this.val[M_02] * this.val[M_13] * this.val[M_20] - this.val[M_03] * this.val[M_12] * this.val[M_20] + this.val[M_03] * this.val[M_10] * this.val[M_22] - this.val[M_00] * this.val[M_13] * this.val[M_22] - this.val[M_02] * this.val[M_10] * this.val[M_23] + this.val[M_00] * this.val[M_12] * this.val[M_23];
		tmp[M_20] = this.val[M_11] * this.val[M_23] * this.val[M_30] - this.val[M_13] * this.val[M_21] * this.val[M_30] + this.val[M_13] * this.val[M_20] * this.val[M_31] - this.val[M_10] * this.val[M_23] * this.val[M_31] - this.val[M_11] * this.val[M_20] * this.val[M_33] + this.val[M_10] * this.val[M_21] * this.val[M_33];
		tmp[M_21] = this.val[M_03] * this.val[M_21] * this.val[M_30] - this.val[M_01] * this.val[M_23] * this.val[M_30] - this.val[M_03] * this.val[M_20] * this.val[M_31] + this.val[M_00] * this.val[M_23] * this.val[M_31] + this.val[M_01] * this.val[M_20] * this.val[M_33] - this.val[M_00] * this.val[M_21] * this.val[M_33];
		tmp[M_22] = this.val[M_01] * this.val[M_13] * this.val[M_30] - this.val[M_03] * this.val[M_11] * this.val[M_30] + this.val[M_03] * this.val[M_10] * this.val[M_31] - this.val[M_00] * this.val[M_13] * this.val[M_31] - this.val[M_01] * this.val[M_10] * this.val[M_33] + this.val[M_00] * this.val[M_11] * this.val[M_33];
		tmp[M_23] = this.val[M_03] * this.val[M_11] * this.val[M_20] - this.val[M_01] * this.val[M_13] * this.val[M_20] - this.val[M_03] * this.val[M_10] * this.val[M_21] + this.val[M_00] * this.val[M_13] * this.val[M_21] + this.val[M_01] * this.val[M_10] * this.val[M_23] - this.val[M_00] * this.val[M_11] * this.val[M_23];
		tmp[M_30] = this.val[M_12] * this.val[M_21] * this.val[M_30] - this.val[M_11] * this.val[M_22] * this.val[M_30] - this.val[M_12] * this.val[M_20] * this.val[M_31] + this.val[M_10] * this.val[M_22] * this.val[M_31] + this.val[M_11] * this.val[M_20] * this.val[M_32] - this.val[M_10] * this.val[M_21] * this.val[M_32];
		tmp[M_31] = this.val[M_01] * this.val[M_22] * this.val[M_30] - this.val[M_02] * this.val[M_21] * this.val[M_30] + this.val[M_02] * this.val[M_20] * this.val[M_31] - this.val[M_00] * this.val[M_22] * this.val[M_31] - this.val[M_01] * this.val[M_20] * this.val[M_32] + this.val[M_00] * this.val[M_21] * this.val[M_32];
		tmp[M_32] = this.val[M_02] * this.val[M_11] * this.val[M_30] - this.val[M_01] * this.val[M_12] * this.val[M_30] - this.val[M_02] * this.val[M_10] * this.val[M_31] + this.val[M_00] * this.val[M_12] * this.val[M_31] + this.val[M_01] * this.val[M_10] * this.val[M_32] - this.val[M_00] * this.val[M_11] * this.val[M_32];
		tmp[M_33] = this.val[M_01] * this.val[M_12] * this.val[M_20] - this.val[M_02] * this.val[M_11] * this.val[M_20] + this.val[M_02] * this.val[M_10] * this.val[M_21] - this.val[M_00] * this.val[M_12] * this.val[M_21] - this.val[M_01] * this.val[M_10] * this.val[M_22] + this.val[M_00] * this.val[M_11] * this.val[M_22];
		this.val[M_00] = tmp[M_00] * inv_det;
		this.val[M_01] = tmp[M_01] * inv_det;
		this.val[M_02] = tmp[M_02] * inv_det;
		this.val[M_03] = tmp[M_03] * inv_det;
		this.val[M_10] = tmp[M_10] * inv_det;
		this.val[M_11] = tmp[M_11] * inv_det;
		this.val[M_12] = tmp[M_12] * inv_det;
		this.val[M_13] = tmp[M_13] * inv_det;
		this.val[M_20] = tmp[M_20] * inv_det;
		this.val[M_21] = tmp[M_21] * inv_det;
		this.val[M_22] = tmp[M_22] * inv_det;
		this.val[M_23] = tmp[M_23] * inv_det;
		this.val[M_30] = tmp[M_30] * inv_det;
		this.val[M_31] = tmp[M_31] * inv_det;
		this.val[M_32] = tmp[M_32] * inv_det;
		this.val[M_33] = tmp[M_33] * inv_det;
		return this;
	}

	public double det() {
		return this.val[M_30] * this.val[M_21] * this.val[M_12] * this.val[M_03] - this.val[M_20] * this.val[M_31] * this.val[M_12] * this.val[M_03] - this.val[M_30] * this.val[M_11] * this.val[M_22] * this.val[M_03] + this.val[M_10] * this.val[M_31] * this.val[M_22] * this.val[M_03] + this.val[M_20] * this.val[M_11] * this.val[M_32] * this.val[M_03] - this.val[M_10] * this.val[M_21] * this.val[M_32] * this.val[M_03] - this.val[M_30] * this.val[M_21] * this.val[M_02] * this.val[M_13] + this.val[M_20] * this.val[M_31] * this.val[M_02] * this.val[M_13] + this.val[M_30] * this.val[M_01] * this.val[M_22] * this.val[M_13] - this.val[M_00] * this.val[M_31] * this.val[M_22] * this.val[M_13] - this.val[M_20] * this.val[M_01] * this.val[M_32] * this.val[M_13] + this.val[M_00] * this.val[M_21] * this.val[M_32] * this.val[M_13] + this.val[M_30] * this.val[M_11] * this.val[M_02] * this.val[M_23] - this.val[M_10] * this.val[M_31] * this.val[M_02] * this.val[M_23] - this.val[M_30] * this.val[M_01]
				* this.val[M_12] * this.val[M_23] + this.val[M_00] * this.val[M_31] * this.val[M_12] * this.val[M_23] + this.val[M_10] * this.val[M_01] * this.val[M_32] * this.val[M_23] - this.val[M_00] * this.val[M_11] * this.val[M_32] * this.val[M_23] - this.val[M_20] * this.val[M_11] * this.val[M_02] * this.val[M_33] + this.val[M_10] * this.val[M_21] * this.val[M_02] * this.val[M_33] + this.val[M_20] * this.val[M_01] * this.val[M_12] * this.val[M_33] - this.val[M_00] * this.val[M_21] * this.val[M_12] * this.val[M_33] - this.val[M_10] * this.val[M_01] * this.val[M_22] * this.val[M_33] + this.val[M_00] * this.val[M_11] * this.val[M_22] * this.val[M_33];
	}

	public double det3x3() {
		return this.val[M_00] * this.val[M_11] * this.val[M_22] + this.val[M_01] * this.val[M_12] * this.val[M_20] + this.val[M_02] * this.val[M_10] * this.val[M_21] - this.val[M_00] * this.val[M_12] * this.val[M_21] - this.val[M_01] * this.val[M_10] * this.val[M_22] - this.val[M_02] * this.val[M_11] * this.val[M_20];
	}

	public Matrix4 setToProjection(double near, double far, double fovy, double aspectRatio) {
		idt();
		final double l_fd = 1.0 / Math.tan(fovy * (Math.PI / 180) / 2.0);
		final double l_a1 = (far + near) / (near - far);
		final double l_a2 = 2 * far * near / (near - far);
		this.val[M_00] = l_fd / aspectRatio;
		this.val[M_10] = 0;
		this.val[M_20] = 0;
		this.val[M_30] = 0;
		this.val[M_01] = 0;
		this.val[M_11] = l_fd;
		this.val[M_21] = 0;
		this.val[M_31] = 0;
		this.val[M_02] = 0;
		this.val[M_12] = 0;
		this.val[M_22] = l_a1;
		this.val[M_32] = -1;
		this.val[M_03] = 0;
		this.val[M_13] = 0;
		this.val[M_23] = l_a2;
		this.val[M_33] = 0;

		return this;
	}

	public Matrix4 setToOrtho2D(double x, double y, double width, double height) {
		setToOrtho(x, x + width, y, y + height, 0, 1);
		return this;
	}

	public Matrix4 setToOrtho2D(double x, double y, double width, double height, double near, double far) {
		setToOrtho(x, x + width, y, y + height, near, far);
		return this;
	}

	public Matrix4 setToOrtho(double left, double right, double bottom, double top, double near, double far) {

		idt();
		final double x_orth = 2 / (right - left);
		final double y_orth = 2 / (top - bottom);
		final double z_orth = -2 / (far - near);

		final double tx = -(right + left) / (right - left);
		final double ty = -(top + bottom) / (top - bottom);
		final double tz = -(far + near) / (far - near);

		this.val[M_00] = x_orth;
		this.val[M_10] = 0;
		this.val[M_20] = 0;
		this.val[M_30] = 0;
		this.val[M_01] = 0;
		this.val[M_11] = y_orth;
		this.val[M_21] = 0;
		this.val[M_31] = 0;
		this.val[M_02] = 0;
		this.val[M_12] = 0;
		this.val[M_22] = z_orth;
		this.val[M_32] = 0;
		this.val[M_03] = tx;
		this.val[M_13] = ty;
		this.val[M_23] = tz;
		this.val[M_33] = 1;

		return this;
	}

	public Matrix4 setTranslation(Vector3 vector) {
		this.val[M_03] = vector.x;
		this.val[M_13] = vector.y;
		this.val[M_23] = vector.z;
		return this;
	}

	public Matrix4 setTranslation(double x, double y, double z) {
		this.val[M_03] = x;
		this.val[M_13] = y;
		this.val[M_23] = z;
		return this;
	}

	public Matrix4 setToTranslation(Vector3 vector) {
		idt();
		this.val[M_03] = vector.x;
		this.val[M_13] = vector.y;
		this.val[M_23] = vector.z;
		return this;
	}

	public Matrix4 setToTranslation(double x, double y, double z) {
		idt();
		this.val[M_03] = x;
		this.val[M_13] = y;
		this.val[M_23] = z;
		return this;
	}

	public Matrix4 setToTranslationAndScaling(Vector3 translation, Vector3 scaling) {
		idt();
		this.val[M_03] = translation.x;
		this.val[M_13] = translation.y;
		this.val[M_23] = translation.z;
		this.val[M_00] = scaling.x;
		this.val[M_11] = scaling.y;
		this.val[M_22] = scaling.z;
		return this;
	}

	public Matrix4 setToTranslationAndScaling(double translationX, double translationY, double translationZ, double scalingX, double scalingY, double scalingZ) {
		idt();
		this.val[M_03] = translationX;
		this.val[M_13] = translationY;
		this.val[M_23] = translationZ;
		this.val[M_00] = scalingX;
		this.val[M_11] = scalingY;
		this.val[M_22] = scalingZ;
		return this;
	}

	static Quaternion quat = new Quaternion();
	static Quaternion quat2 = new Quaternion();

	public Matrix4 setToRotation(Vector3 axis, double degrees) {
		if (degrees == 0) {
			idt();
			return this;
		}
		return set(quat.set(axis, degrees));
	}

	public Matrix4 setToRotationRad(Vector3 axis, double radians) {
		if (radians == 0) {
			idt();
			return this;
		}
		return set(quat.setFromAxisRad(axis, radians));
	}

	public Matrix4 setToRotation(double axisX, double axisY, double axisZ, double degrees) {
		if (degrees == 0) {
			idt();
			return this;
		}
		return set(quat.setFromAxis(axisX, axisY, axisZ, degrees));
	}

	public Matrix4 setToRotationRad(double axisX, double axisY, double axisZ, double radians) {
		if (radians == 0) {
			idt();
			return this;
		}
		return set(quat.setFromAxisRad(axisX, axisY, axisZ, radians));
	}

	public Matrix4 setToRotation(final Vector3 v1, final Vector3 v2) {
		return set(quat.setFromCross(v1, v2));
	}

	public Matrix4 setToRotation(final double x1, final double y1, final double z1, final double x2, final double y2, final double z2) {
		return set(quat.setFromCross(x1, y1, z1, x2, y2, z2));
	}

	public Matrix4 setFromEulerAngles(double yaw, double pitch, double roll) {
		quat.setEulerAngles(yaw, pitch, roll);
		return set(quat);
	}

	public Matrix4 setToScaling(Vector3 vector) {
		idt();
		this.val[M_00] = vector.x;
		this.val[M_11] = vector.y;
		this.val[M_22] = vector.z;
		return this;
	}

	public Matrix4 setToScaling(double x, double y, double z) {
		idt();
		this.val[M_00] = x;
		this.val[M_11] = y;
		this.val[M_22] = z;
		return this;
	}

	static final Vector3 l_vez = new Vector3();
	static final Vector3 l_vex = new Vector3();
	static final Vector3 l_vey = new Vector3();

	public Matrix4 setToLookAt(Vector3 direction, Vector3 up) {
		l_vez.set(direction).nor();
		l_vex.set(direction).nor();
		l_vex.crs(up).nor();
		l_vey.set(l_vex).crs(l_vez).nor();
		idt();
		this.val[M_00] = l_vex.x;
		this.val[M_01] = l_vex.y;
		this.val[M_02] = l_vex.z;
		this.val[M_10] = l_vey.x;
		this.val[M_11] = l_vey.y;
		this.val[M_12] = l_vey.z;
		this.val[M_20] = -l_vez.x;
		this.val[M_21] = -l_vez.y;
		this.val[M_22] = -l_vez.z;

		return this;
	}

	static final Vector3 tmpVec = new Vector3();
	static final Matrix4 tmpMat = new Matrix4();

	static final Vector3 right = new Vector3();
	static final Vector3 tmpForward = new Vector3();
	static final Vector3 tmpUp = new Vector3();

	public Matrix4 setToWorld(Vector3 position, Vector3 forward, Vector3 up) {
		tmpForward.set(forward).nor();
		right.set(tmpForward).crs(up).nor();
		tmpUp.set(right).crs(tmpForward).nor();

		this.set(right, tmpUp, tmpForward.scale(-1), position);
		return this;
	}

	@Override
	public String toString() {
		return "[" + this.val[M_00] + "|" + this.val[M_01] + "|" + this.val[M_02] + "|" + this.val[M_03] + "]\n" + "[" + this.val[M_10] + "|" + this.val[M_11] + "|" + this.val[M_12] + "|" + this.val[M_13] + "]\n" + "[" + this.val[M_20] + "|" + this.val[M_21] + "|" + this.val[M_22] + "|" + this.val[M_23] + "]\n" + "[" + this.val[M_30] + "|" + this.val[M_31] + "|" + this.val[M_32] + "|" + this.val[M_33] + "]\n";
	}

	public Matrix4 lerp(Matrix4 matrix, double alpha) {
		for (int i = 0; i < 16; i++) {
			this.val[i] = this.val[i] * (1 - alpha) + matrix.val[i] * alpha;
		}
		return this;
	}

	public Matrix4 set(Matrix3 mat) {
		this.val[0] = mat.val[0];
		this.val[1] = mat.val[1];
		this.val[2] = mat.val[2];
		this.val[3] = 0;
		this.val[4] = mat.val[3];
		this.val[5] = mat.val[4];
		this.val[6] = mat.val[5];
		this.val[7] = 0;
		this.val[8] = 0;
		this.val[9] = 0;
		this.val[10] = 1;
		this.val[11] = 0;
		this.val[12] = mat.val[6];
		this.val[13] = mat.val[7];
		this.val[14] = 0;
		this.val[15] = mat.val[8];
		return this;
	}

	public Matrix4 setAsAffine(Matrix4 mat) {
		this.val[M_00] = mat.val[M_00];
		this.val[M_10] = mat.val[M_10];
		this.val[M_01] = mat.val[M_01];
		this.val[M_11] = mat.val[M_11];
		this.val[M_03] = mat.val[M_03];
		this.val[M_13] = mat.val[M_13];
		return this;
	}

	public Matrix4 scl(Vector3 scale) {
		this.val[M_00] *= scale.x;
		this.val[M_11] *= scale.y;
		this.val[M_22] *= scale.z;
		return this;
	}

	public Matrix4 scl(double x, double y, double z) {
		this.val[M_00] *= x;
		this.val[M_11] *= y;
		this.val[M_22] *= z;
		return this;
	}

	public Matrix4 scl(double scale) {
		this.val[M_00] *= scale;
		this.val[M_11] *= scale;
		this.val[M_22] *= scale;
		return this;
	}

	public Vector3 getTranslation(Vector3 position) {
		position.x = this.val[M_03];
		position.y = this.val[M_13];
		position.z = this.val[M_23];
		return position;
	}

	public Quaternion getRotation(Quaternion rotation, boolean normalizeAxes) {
		return rotation.setFromMatrix(normalizeAxes, this);
	}

	public Quaternion getRotation(Quaternion rotation) {
		return rotation.setFromMatrix(this);
	}

	public double getScaleXSquared() {
		return this.val[Matrix4.M_00] * this.val[Matrix4.M_00] + this.val[Matrix4.M_01] * this.val[Matrix4.M_01] + this.val[Matrix4.M_02] * this.val[Matrix4.M_02];
	}

	public double getScaleYSquared() {
		return this.val[Matrix4.M_10] * this.val[Matrix4.M_10] + this.val[Matrix4.M_11] * this.val[Matrix4.M_11] + this.val[Matrix4.M_12] * this.val[Matrix4.M_12];
	}

	public double getScaleZSquared() {
		return this.val[Matrix4.M_20] * this.val[Matrix4.M_20] + this.val[Matrix4.M_21] * this.val[Matrix4.M_21] + this.val[Matrix4.M_22] * this.val[Matrix4.M_22];
	}

	public double getScaleX() {
		return FloatingPoint.isZero(this.val[Matrix4.M_01]) && FloatingPoint.isZero(this.val[Matrix4.M_02]) ? Math.abs(this.val[Matrix4.M_00]) : (double) Math.sqrt(getScaleXSquared());
	}

	public double getScaleY() {
		return FloatingPoint.isZero(this.val[Matrix4.M_10]) && FloatingPoint.isZero(this.val[Matrix4.M_12]) ? Math.abs(this.val[Matrix4.M_11]) : (double) Math.sqrt(getScaleYSquared());
	}

	public double getScaleZ() {
		return FloatingPoint.isZero(this.val[Matrix4.M_20]) && FloatingPoint.isZero(this.val[Matrix4.M_21]) ? Math.abs(this.val[Matrix4.M_22]) : (double) Math.sqrt(getScaleZSquared());
	}

	public Vector3 getScale(Vector3 scale) {
		return scale.set(getScaleX(), getScaleY(), getScaleZ());
	}

	public Matrix4 toNormalMatrix() {
		this.val[M_03] = 0;
		this.val[M_13] = 0;
		this.val[M_23] = 0;
		return inv().tra();
	}

}
