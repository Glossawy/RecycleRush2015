package org.usfirst.frc.team1554.lib.math;

public class Matrix3 {

	public static final int M00 = 0, M01 = 3, M02 = 6;
	public static final int M10 = 1, M11 = 4, M12 = 7;
	public static final int M20 = 2, M21 = 5, M22 = 8;

	public double[] val = new double[9];
	private final double[] temp = new double[9];

	public Matrix3() {
		identity();
	}

	public void identity() {
		this.val[M00] = 1;
		this.val[M01] = 0;
		this.val[M02] = 0;
		this.val[M10] = 0;
		this.val[M11] = 1;
		this.val[M12] = 0;
		this.val[M20] = 0;
		this.val[M21] = 0;
		this.val[M22] = 1;
	}

	public Matrix3 mul(Matrix3 m) {
		final double v00 = this.val[M00] * m.val[M00] + this.val[M01] * m.val[M10] + this.val[M02] * m.val[M20];
		final double v01 = this.val[M00] * m.val[M01] + this.val[M01] * m.val[M11] + this.val[M02] * m.val[M21];
		final double v02 = this.val[M00] * m.val[M02] + this.val[M01] * m.val[M12] + this.val[M02] * m.val[M22];

		final double v10 = this.val[M10] * m.val[M00] + this.val[M11] * m.val[M10] + this.val[M12] * m.val[M20];
		final double v11 = this.val[M10] * m.val[M01] + this.val[M11] * m.val[M11] + this.val[M12] * m.val[M21];
		final double v12 = this.val[M10] * m.val[M02] + this.val[M11] * m.val[M12] + this.val[M12] * m.val[M22];

		final double v20 = this.val[M20] * m.val[M00] + this.val[M21] * m.val[M10] + this.val[M22] * m.val[M20];
		final double v21 = this.val[M20] * m.val[M01] + this.val[M21] * m.val[M11] + this.val[M22] * m.val[M21];
		final double v22 = this.val[M20] * m.val[M02] + this.val[M21] * m.val[M12] + this.val[M22] * m.val[M22];

		this.val[M00] = v00;
		this.val[M10] = v10;
		this.val[M20] = v20;
		this.val[M01] = v01;
		this.val[M11] = v11;
		this.val[M21] = v21;
		this.val[M02] = v02;
		this.val[M12] = v12;
		this.val[M22] = v22;

		return this;
	}

	private static void mul(double[] mata, double[] matb) {
		final double v00 = mata[M00] * matb[M00] + mata[M01] * matb[M10] + mata[M02] * matb[M20];
		final double v01 = mata[M00] * matb[M01] + mata[M01] * matb[M11] + mata[M02] * matb[M21];
		final double v02 = mata[M00] * matb[M02] + mata[M01] * matb[M12] + mata[M02] * matb[M22];

		final double v10 = mata[M10] * matb[M00] + mata[M11] * matb[M10] + mata[M12] * matb[M20];
		final double v11 = mata[M10] * matb[M01] + mata[M11] * matb[M11] + mata[M12] * matb[M21];
		final double v12 = mata[M10] * matb[M02] + mata[M11] * matb[M12] + mata[M12] * matb[M22];

		final double v20 = mata[M20] * matb[M00] + mata[M21] * matb[M10] + mata[M22] * matb[M20];
		final double v21 = mata[M20] * matb[M01] + mata[M21] * matb[M11] + mata[M22] * matb[M21];
		final double v22 = mata[M20] * matb[M02] + mata[M21] * matb[M12] + mata[M22] * matb[M22];

		mata[M00] = v00;
		mata[M10] = v10;
		mata[M20] = v20;
		mata[M01] = v01;
		mata[M11] = v11;
		mata[M21] = v21;
		mata[M02] = v02;
		mata[M12] = v12;
		mata[M22] = v22;
	}

	public Matrix3 setToRotation(double degrees) {
		return setToRotationRad(Math.toRadians(degrees));
	}

	public Matrix3 setToRotationRad(double radians) {
		final double cos = Math.cos(radians);
		final double sin = Math.sin(radians);

		this.val[M00] = cos;
		this.val[M10] = sin;
		this.val[M20] = 0;

		this.val[M01] = -sin;
		this.val[M11] = cos;
		this.val[M21] = 0;

		this.val[M02] = 0;
		this.val[M12] = 0;
		this.val[M22] = 1;

		return this;
	}

	public Matrix3 setToTranslation(double x, double y) {
		this.val[M00] = 1;
		this.val[M10] = 0;
		this.val[M20] = 0;

		this.val[M01] = 0;
		this.val[M11] = 1;
		this.val[M21] = 0;

		this.val[M02] = x;
		this.val[M12] = y;
		this.val[M22] = 1;

		return this;
	}

	public Matrix3 setToTranslation(Vector2 translation) {
		this.val[M00] = 1;
		this.val[M10] = 0;
		this.val[M20] = 0;

		this.val[M01] = 0;
		this.val[M11] = 1;
		this.val[M21] = 0;

		this.val[M02] = translation.x;
		this.val[M12] = translation.y;
		this.val[M22] = 1;

		return this;
	}

	public Matrix3 setToScaling(float scaleX, float scaleY) {
		this.val[M00] = scaleX;
		this.val[M10] = 0;
		this.val[M20] = 0;
		this.val[M01] = 0;
		this.val[M11] = scaleY;
		this.val[M21] = 0;
		this.val[M02] = 0;
		this.val[M12] = 0;
		this.val[M22] = 1;
		return this;
	}

	public Matrix3 setToScaling(Vector2 scale) {
		this.val[M00] = scale.x;
		this.val[M10] = 0;
		this.val[M20] = 0;
		this.val[M01] = 0;
		this.val[M11] = scale.y;
		this.val[M21] = 0;
		this.val[M02] = 0;
		this.val[M12] = 0;
		this.val[M22] = 1;
		return this;
	}

	@Override
	public String toString() {
		return "[" + this.val[0] + "|" + this.val[3] + "|" + this.val[6] + "]\n" + "[" + this.val[1] + "|" + this.val[4] + "|" + this.val[7] + "]\n" + "[" + this.val[2] + "|" + this.val[5] + "|" + this.val[8] + "]";
	}

	public double det() {
		return this.val[M00] * this.val[M11] * this.val[M22] + this.val[M01] * this.val[M12] * this.val[M20] + this.val[M02] * this.val[M10] * this.val[M21] - this.val[M00] * this.val[M12] * this.val[M21] - this.val[M01] * this.val[M10] * this.val[M22] - this.val[M02] * this.val[M11] * this.val[M20];
	}

	public Matrix3 inv() {
		final double det = det();
		if (det == 0)
			throw new RuntimeException("Can't invert a singular matrix");

		final double inv_det = 1.0 / det;

		this.temp[M00] = this.val[M11] * this.val[M22] - this.val[M21] * this.val[M12];
		this.temp[M10] = this.val[M20] * this.val[M12] - this.val[M10] * this.val[M22];
		this.temp[M20] = this.val[M10] * this.val[M21] - this.val[M20] * this.val[M11];
		this.temp[M01] = this.val[M21] * this.val[M02] - this.val[M01] * this.val[M22];
		this.temp[M11] = this.val[M00] * this.val[M22] - this.val[M20] * this.val[M02];
		this.temp[M21] = this.val[M20] * this.val[M01] - this.val[M00] * this.val[M21];
		this.temp[M02] = this.val[M01] * this.val[M12] - this.val[M11] * this.val[M02];
		this.temp[M12] = this.val[M10] * this.val[M02] - this.val[M00] * this.val[M12];
		this.temp[M22] = this.val[M00] * this.val[M11] - this.val[M10] * this.val[M01];

		this.val[M00] = inv_det * this.temp[M00];
		this.val[M10] = inv_det * this.temp[M10];
		this.val[M20] = inv_det * this.temp[M20];
		this.val[M01] = inv_det * this.temp[M01];
		this.val[M11] = inv_det * this.temp[M11];
		this.val[M21] = inv_det * this.temp[M21];
		this.val[M02] = inv_det * this.temp[M02];
		this.val[M12] = inv_det * this.temp[M12];
		this.val[M22] = inv_det * this.temp[M22];

		return this;
	}

	public Matrix3 set(Matrix3 mat) {
		System.arraycopy(mat.val, 0, this.val, 0, this.val.length);
		return this;
	}

	public Matrix3 set(double[] values) {
		System.arraycopy(values, 0, this.val, 0, this.val.length);
		return this;
	}

	public Matrix3 trn(Vector2 vector) {
		this.val[M02] += vector.x;
		this.val[M12] += vector.y;
		return this;
	}

	public Matrix3 trn(double x, double y) {
		this.val[M02] += x;
		this.val[M12] += y;
		return this;
	}

	public Matrix3 transpose() {
		// Where MXY you do not have to change MXX
		final double v01 = this.val[M10];
		final double v02 = this.val[M20];
		final double v10 = this.val[M01];
		final double v12 = this.val[M21];
		final double v20 = this.val[M02];
		final double v21 = this.val[M12];
		this.val[M01] = v01;
		this.val[M02] = v02;
		this.val[M10] = v10;
		this.val[M12] = v12;
		this.val[M20] = v20;
		this.val[M21] = v21;
		return this;
	}

	public Matrix3 translate(double x, double y) {
		this.temp[M00] = 1;
		this.temp[M10] = 0;
		this.temp[M20] = 0;

		this.temp[M01] = 0;
		this.temp[M11] = 1;
		this.temp[M21] = 0;

		this.temp[M02] = x;
		this.temp[M12] = y;
		this.temp[M22] = 1;
		mul(this.val, this.temp);
		return this;
	}

	public Matrix3 translate(Vector2 translation) {
		this.temp[M00] = 1;
		this.temp[M10] = 0;
		this.temp[M20] = 0;

		this.temp[M01] = 0;
		this.temp[M11] = 1;
		this.temp[M21] = 0;

		this.temp[M02] = translation.x;
		this.temp[M12] = translation.y;
		this.temp[M22] = 1;
		mul(this.val, this.temp);
		return this;
	}

	public Matrix3 rotate(double degrees) {
		return rotateRad(Math.toRadians(degrees));
	}

	public Matrix3 rotateRad(double radians) {
		if (radians == 0)
			return this;
		final double cos = Math.cos(radians);
		final double sin = Math.sin(radians);

		this.temp[M00] = cos;
		this.temp[M10] = sin;
		this.temp[M20] = 0;

		this.temp[M01] = -sin;
		this.temp[M11] = cos;
		this.temp[M21] = 0;

		this.temp[M02] = 0;
		this.temp[M12] = 0;
		this.temp[M22] = 1;
		mul(this.val, this.temp);
		return this;
	}

	public double[] getValues() {
		return this.val;
	}

	public Vector2 getTranslation(Vector2 position) {
		position.x = this.val[M02];
		position.y = this.val[M12];
		return position;
	}

	public Vector2 getScale(Vector2 scale) {
		scale.x = Math.sqrt(this.val[M00] * this.val[M00] + this.val[M01] * this.val[M01]);
		scale.y = Math.sqrt(this.val[M10] * this.val[M10] + this.val[M11] * this.val[M11]);
		return scale;
	}

	public double getRotation() {
		return Math.toDegrees(Math.atan2(this.val[M10], this.val[M00]));
	}

	public double getRotationRad() {
		return Math.atan2(this.val[M10], this.val[M00]);
	}

}
