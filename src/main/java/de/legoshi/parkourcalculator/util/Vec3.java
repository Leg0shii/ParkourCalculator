package de.legoshi.parkourcalculator.util;

import java.util.Objects;

public class Vec3 {

	public static final Vec3 ZERO;

	static {
		ZERO = new Vec3(0.0D, 0.0D, 0.0D);
	}

	public double x;
	public double y;
	public double z;

	public Vec3(double x, double y, double z) {
		if (x == -0.0D) {
			x = 0.0D;
		}

		if (y == -0.0D) {
			y = 0.0D;
		}

		if (z == -0.0D) {
			z = 0.0D;
		}

		this.x = x;
		this.y = y;
		this.z = z;
	}

	/* public Vec3(Vec3i p_i46377_1_) {
		this((double) p_i46377_1_.getX(), (double) p_i46377_1_.getY(), (double) p_i46377_1_.getZ());
	} */

	/**
	 * Returns a new vector with the result of the specified vector minus this.
	 */
	public Vec3 subtractReverse(Vec3 vec) {
		return new Vec3(vec.x - this.x, vec.y - this.y, vec.z - this.z);
	}

	public double dot(Vec3 other) {
		return this.x * other.x +
				this.y * other.y +
				this.z * other.z;
	}

	/**
	 * Normalizes the vector to a length of 1 (except if it is the zero vector)
	 */
	public Vec3 normalize() {
		double d0 = (double) MinecraftMathHelper.sqrt_double(this.x * this.x + this.y * this.y + this.z * this.z);
		return d0 < 1.0E-4D ? new Vec3(0.0D, 0.0D, 0.0D) : new Vec3(this.x / d0, this.y / d0, this.z / d0);
	}

	public double dotProduct(Vec3 vec) {
		return this.x * vec.x + this.y * vec.y + this.z * vec.z;
	}

	/**
	 * Returns a new vector with the result of this vector x the specified vector.
	 */
	public Vec3 crossProduct(Vec3 vec) {
		return new Vec3(this.y * vec.z - this.z * vec.y, this.z * vec.x - this.x * vec.z, this.x * vec.y - this.y * vec.x);
	}

	public void subtract(Vec3 vec) {
		this.subtract(vec.x, vec.y, vec.z);
	}

	public void subtract(double x, double y, double z) {
		this.addVector(-x, -y, -z);
	}

	public void add(Vec3 vec) {
		this.addVector(vec.x, vec.y, vec.z);
	}

	public void addVector(double x, double y, double z) {
		this.x = this.x + x;
		this.y = this.y + y;
		this.z = this.z + z;
	}

	/**
	 * Euclidean distance between this and the specified vector, returned as double.
	 */
	public double distanceTo(Vec3 vec) {
		double d0 = vec.x - this.x;
		double d1 = vec.y - this.y;
		double d2 = vec.z - this.z;
		return (double) MinecraftMathHelper.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);
	}

	/**
	 * The square of the Euclidean distance between this and the specified vector.
	 */
	public double squareDistanceTo(Vec3 vec) {
		double d0 = vec.x - this.x;
		double d1 = vec.y - this.y;
		double d2 = vec.z - this.z;
		return d0 * d0 + d1 * d1 + d2 * d2;
	}

	/**
	 * Returns the length of the vector.
	 */
	public double lengthVector() {
		return (double) MinecraftMathHelper.sqrt_double(this.x * this.x + this.y * this.y + this.z * this.z);
	}

	/**
	 * Returns a new vector with x value equal to the second parameter, along the line between this vector and the
	 * passed in vector, or null if not possible.
	 */
	public Vec3 getIntermediateWithXValue(Vec3 vec, double x) {
		double d0 = vec.x - this.x;
		double d1 = vec.y - this.y;
		double d2 = vec.z - this.z;

		if (d0 * d0 < 1.0000000116860974E-7D) {
			return null;
		} else {
			double d3 = (x - this.x) / d0;
			return d3 >= 0.0D && d3 <= 1.0D ? new Vec3(this.x + d0 * d3, this.y + d1 * d3, this.z + d2 * d3) : null;
		}
	}

	/**
	 * Returns a new vector with y value equal to the second parameter, along the line between this vector and the
	 * passed in vector, or null if not possible.
	 */
	public Vec3 getIntermediateWithYValue(Vec3 vec, double y) {
		double d0 = vec.x - this.x;
		double d1 = vec.y - this.y;
		double d2 = vec.z - this.z;

		if (d1 * d1 < 1.0000000116860974E-7D) {
			return null;
		} else {
			double d3 = (y - this.y) / d1;
			return d3 >= 0.0D && d3 <= 1.0D ? new Vec3(this.x + d0 * d3, this.y + d1 * d3, this.z + d2 * d3) : null;
		}
	}

	/**
	 * Returns a new vector with z value equal to the second parameter, along the line between this vector and the
	 * passed in vector, or null if not possible.
	 */
	public Vec3 getIntermediateWithZValue(Vec3 vec, double z) {
		double d0 = vec.x - this.x;
		double d1 = vec.y - this.y;
		double d2 = vec.z - this.z;

		if (d2 * d2 < 1.0000000116860974E-7D) {
			return null;
		} else {
			double d3 = (z - this.z) / d2;
			return d3 >= 0.0D && d3 <= 1.0D ? new Vec3(this.x + d0 * d3, this.y + d1 * d3, this.z + d2 * d3) : null;
		}
	}

	public Vec3 scale(double var1) {
		return this.multiply(var1, var1, var1);
	}

	public Vec3 multiply(Vec3 var1) {
		return this.multiply(var1.x, var1.y, var1.z);
	}

	public Vec3 multiply(double var1, double var3, double var5) {
		return new Vec3(this.x * var1, this.y * var3, this.z * var5);
	}

	public double horizontalDistance() {
		return Math.sqrt(this.x * this.x + this.z * this.z);
	}

	public double horizontalDistanceSqr() {
		return this.x * this.x + this.z * this.z;
	}

	public double length() {
		return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
	}

	public double lengthSqr() {
		return this.x * this.x + this.y * this.y + this.z * this.z;
	}

	public String toString() {
		return "(" + this.x + ", " + this.y + ", " + this.z + ")";
	}

	public Vec3 rotatePitch(float pitch) {
		float f = MinecraftMathHelper.cos(pitch);
		float f1 = MinecraftMathHelper.sin(pitch);
		double d0 = this.x;
		double d1 = this.y * (double) f + this.z * (double) f1;
		double d2 = this.z * (double) f - this.y * (double) f1;
		return new Vec3(d0, d1, d2);
	}

	public Vec3 rotateYaw(float yaw) {
		float f = MinecraftMathHelper.cos(yaw);
		float f1 = MinecraftMathHelper.sin(yaw);
		double d0 = this.x * (double) f + this.z * (double) f1;
		double d1 = this.y;
		double d2 = this.z * (double) f - this.x * (double) f1;
		return new Vec3(d0, d1, d2);
	}

	public Vec3 copy() {
		return new Vec3(this.x, this.y, this.z);
	}

	public static Vec3 containing(double var0, double var2, double var4) {
		return new Vec3(MinecraftMathHelper_1_20_4.floor(var0), MinecraftMathHelper_1_20_4.floor(var2), MinecraftMathHelper_1_20_4.floor(var4));
	}

	public double distanceToSqr(Vec3 var1) {
		double var2 = var1.x - this.x;
		double var4 = var1.y - this.y;
		double var6 = var1.z - this.z;
		return var2 * var2 + var4 * var4 + var6 * var6;
	}

	public double distanceToSqr(double var1, double var3, double var5) {
		double var7 = var1 - this.x;
		double var9 = var3 - this.y;
		double var11 = var5 - this.z;
		return var7 * var7 + var9 * var9 + var11 * var11;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Vec3 other)) return false;

        double epsilon = 1e-6;
		return Math.abs(this.x - other.x) < epsilon &&
				Math.abs(this.y - other.y) < epsilon &&
				Math.abs(this.z - other.z) < epsilon;
	}

	public int getX() {
		return (int) Math.floor(x);
	}

	public int getY() {
		return (int) Math.floor(y);
	}

	public int getZ() {
		return (int) Math.floor(z);
	}

	@Override
	public int hashCode() {
		double epsilon = 1e-6;
		int hashX = Double.valueOf(Math.round(this.x / epsilon)).hashCode();
		int hashY = Double.valueOf(Math.round(this.y / epsilon)).hashCode();
		int hashZ = Double.valueOf(Math.round(this.z / epsilon)).hashCode();
		return 31 * (31 * hashX + hashY) + hashZ;
	}

}
