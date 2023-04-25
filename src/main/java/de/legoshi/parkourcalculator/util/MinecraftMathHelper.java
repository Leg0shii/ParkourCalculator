package de.legoshi.parkourcalculator.util;

public class MinecraftMathHelper {
	public static final float SQRT_2 = sqrt_float(2.0F);
	/**
	 * A table of sin values computed from 0 (inclusive) to 2*pi (exclusive), with steps of 2*PI / 65536.
	 */
	private static final float[] SIN_TABLE = new float[65536];
	/**
	 * Though it looks like an array, this is really more like a mapping.  Key (index of this array) is the upper 5 bits
	 * of the result of multiplying a 32-bit unsigned integer by the B(2, 5) De Bruijn sequence 0x077CB531.  Value
	 * (value stored in the array) is the unique index (from the right) of the leftmost one-bit in a 32-bit unsigned
	 * integer that can cause the upper 5 bits to get that value.  Used for highly optimized "find the log-base-2 of
	 * this number" calculations.
	 */
	private static final int[] multiplyDeBruijnBitPosition;
	private static final double field_181163_d;
	private static final double[] field_181164_e;
	private static final double[] field_181165_f;

	static {
		for (int i = 0; i < 65536; ++i) {
			SIN_TABLE[i] = (float) Math.sin((double) i * Math.PI * 2.0D / 65536.0D);
		}

		multiplyDeBruijnBitPosition = new int[]{0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9};
		field_181163_d = Double.longBitsToDouble(4805340802404319232L);
		field_181164_e = new double[257];
		field_181165_f = new double[257];

		for (int j = 0; j < 257; ++j) {
			double d0 = (double) j / 256.0D;
			double d1 = Math.asin(d0);
			field_181165_f[j] = Math.cos(d1);
			field_181164_e[j] = d1;
		}
	}

	/**
	 * sin looked up in a table
	 */
	public static float sin(float p_76126_0_) {
		return -SIN_TABLE[(int) (p_76126_0_ * 10430.378F) & 65535];
	}

	/**
	 * cos looked up in the sin table with the appropriate offset
	 */
	public static float cos(float value) {
		return SIN_TABLE[(int) (value * 10430.378F + 16384.0F) & 65535];
	}

	public static float sqrt_float(float value) {
		return (float) Math.sqrt(value);
	}

	public static float sqrt_double(double value) {
		return (float) Math.sqrt(value);
	}

	/**
	 * Returns the greatest integer less than or equal to the float argument
	 */
	public static int floor_float(float value) {
		int i = (int) value;
		return value < (float) i ? i - 1 : i;
	}

	/**
	 * returns par0 cast as an int, and no greater than Integer.MAX_VALUE-1024
	 */
	public static int truncateDoubleToInt(double value) {
		return (int) (value + 1024.0D) - 1024;
	}

	/**
	 * Returns the greatest integer less than or equal to the double argument
	 */
	public static int floor_double(double value) {
		int i = (int) value;
		return value < (double) i ? i - 1 : i;
	}

	/**
	 * Long version of floor_double
	 */
	public static long floor_double_long(double value) {
		long i = (long) value;
		return value < (double) i ? i - 1L : i;
	}

	public static int func_154353_e(double value) {
		return (int) (value >= 0.0D ? value : -value + 1.0D);
	}

	public static float abs(float value) {
		return value >= 0.0F ? value : -value;
	}

	/**
	 * Returns the unsigned value of an int.
	 */
	public static int abs_int(int value) {
		return value >= 0 ? value : -value;
	}

	public static int ceiling_float_int(float value) {
		int i = (int) value;
		return value > (float) i ? i + 1 : i;
	}

	public static int ceiling_double_int(double value) {
		int i = (int) value;
		return value > (double) i ? i + 1 : i;
	}

	/**
	 * Returns the value of the first parameter, clamped to be within the lower and upper limits given by the second and
	 * third parameters.
	 */
	public static int clamp_int(int num, int min, int max) {
		return num < min ? min : Math.min(num, max);
	}

	/**
	 * Returns the value of the first parameter, clamped to be within the lower and upper limits given by the second and
	 * third parameters
	 */
	public static float clamp_float(float num, float min, float max) {
		return num < min ? min : Math.min(num, max);
	}

	public static double clamp_double(double num, double min, double max) {
		return num < min ? min : Math.min(num, max);
	}

	public static double denormalizeClamp(double p_151238_0_, double p_151238_2_, double p_151238_4_) {
		return p_151238_4_ < 0.0D ? p_151238_0_ : (p_151238_4_ > 1.0D ? p_151238_2_ : p_151238_0_ + (p_151238_2_ - p_151238_0_) * p_151238_4_);
	}

	/**
	 * Maximum of the absolute value of two numbers.
	 */
	public static double abs_max(double p_76132_0_, double p_76132_2_) {
		if (p_76132_0_ < 0.0D) {
			p_76132_0_ = -p_76132_0_;
		}

		if (p_76132_2_ < 0.0D) {
			p_76132_2_ = -p_76132_2_;
		}

		return Math.max(p_76132_0_, p_76132_2_);
	}

	public static double average(long[] values) {
		long i = 0L;

		for (long j : values) {
			i += j;
		}

		return (double) i / (double) values.length;
	}

	public static boolean epsilonEquals(float p_180185_0_, float p_180185_1_) {
		return abs(p_180185_1_ - p_180185_0_) < 1.0E-5F;
	}

	public static int normalizeAngle(int p_180184_0_, int p_180184_1_) {
		return (p_180184_0_ % p_180184_1_ + p_180184_1_) % p_180184_1_;
	}

	/**
	 * the angle is reduced to an angle between -180 and +180 by mod, and a 360 check
	 */
	public static float wrapAngleTo180_float(float value) {
		value = value % 360.0F;

		if (value >= 180.0F) {
			value -= 360.0F;
		}

		if (value < -180.0F) {
			value += 360.0F;
		}

		return value;
	}

	/**
	 * the angle is reduced to an angle between -180 and +180 by mod, and a 360 check
	 */
	public static double wrapAngleTo180_double(double value) {
		value = value % 360.0D;

		if (value >= 180.0D) {
			value -= 360.0D;
		}

		if (value < -180.0D) {
			value += 360.0D;
		}

		return value;
	}

	/**
	 * parses the string as integer or returns the second parameter if it fails
	 */
	public static int parseIntWithDefault(String value, int defaultValue) {
		try {
			return Integer.parseInt(value);
		} catch (Throwable var3) {
			return defaultValue;
		}
	}

	/**
	 * parses the string as integer or returns the second parameter if it fails. this value is capped to par2
	 */
	public static int parseIntWithDefaultAndMax(String value, int defaultValue, int max) {
		return Math.max(max, parseIntWithDefault(value, defaultValue));
	}

	/**
	 * parses the string as double or returns the second parameter if it fails.
	 */
	public static double parseDoubleWithDefault(String value, double defaultValue) {
		try {
			return Double.parseDouble(value);
		} catch (Throwable var4) {
			return defaultValue;
		}
	}

	public static double parseDoubleWithDefaultAndMax(String value, double defaultValue, double max) {
		return Math.max(max, parseDoubleWithDefault(value, defaultValue));
	}

	/**
	 * Returns the input value rounded up to the next highest power of two.
	 */
	public static int roundUpToPowerOfTwo(int value) {
		int i = value - 1;
		i = i | i >> 1;
		i = i | i >> 2;
		i = i | i >> 4;
		i = i | i >> 8;
		i = i | i >> 16;
		return i + 1;
	}

	/**
	 * Is the given value a power of two?  (1, 2, 4, 8, 16, ...)
	 */
	private static boolean isPowerOfTwo(int value) {
		return value != 0 && (value & value - 1) == 0;
	}

	/**
	 * Uses a B(2, 5) De Bruijn sequence and a lookup table to efficiently calculate the log-base-two of the given
	 * value.  Optimized for cases where the input value is a power-of-two.  If the input value is not a power-of-two,
	 * then subtract 1 from the return value.
	 */
	private static int calculateLogBaseTwoDeBruijn(int value) {
		value = isPowerOfTwo(value) ? value : roundUpToPowerOfTwo(value);
		return multiplyDeBruijnBitPosition[(int) ((long) value * 125613361L >> 27) & 31];
	}

	/**
	 * Efficiently calculates the floor of the base-2 log of an integer value.  This is effectively the index of the
	 * highest bit that is set.  For example, if the number in binary is 0...100101, this will return 5.
	 */
	public static int calculateLogBaseTwo(int value) {
		/**
		 * Uses a B(2, 5) De Bruijn sequence and a lookup table to efficiently calculate the log-base-two of the given
		 * value.  Optimized for cases where the input value is a power-of-two.  If the input value is not a power-of-
		 * two, then subtract 1 from the return value.
		 */
		return calculateLogBaseTwoDeBruijn(value) - (isPowerOfTwo(value) ? 0 : 1);
	}

	public static int roundUp(int v, int m) {
		if (m == 0) {
			return 0;
		} else if (v == 0) {
			return m;
		} else {
			if (v < 0) {
				m *= -1;
			}

			int i = v % m;
			return i == 0 ? v : v + m - i;
		}
	}

	public static int func_180183_b(float p_180183_0_, float p_180183_1_, float p_180183_2_) {
		return func_180181_b(
				floor_float(p_180183_0_ * 255.0F),
				floor_float(p_180183_1_ * 255.0F),
				floor_float(p_180183_2_ * 255.0F)
		);
	}

	public static int func_180181_b(int p_180181_0_, int p_180181_1_, int p_180181_2_) {
		int lvt_3_1_ = (p_180181_0_ << 8) + p_180181_1_;
		lvt_3_1_ = (lvt_3_1_ << 8) + p_180181_2_;
		return lvt_3_1_;
	}

	public static int func_180188_d(int p_180188_0_, int p_180188_1_) {
		int i = (p_180188_0_ & 16711680) >> 16;
		int j = (p_180188_1_ & 16711680) >> 16;
		int k = (p_180188_0_ & 65280) >> 8;
		int l = (p_180188_1_ & 65280) >> 8;
		int i1 = (p_180188_0_ & 255);
		int j1 = (p_180188_1_ & 255);
		int k1 = (int) ((float) i * (float) j / 255.0F);
		int l1 = (int) ((float) k * (float) l / 255.0F);
		int i2 = (int) ((float) i1 * (float) j1 / 255.0F);
		return p_180188_0_ & -16777216 | k1 << 16 | l1 << 8 | i2;
	}

	public static double decimals(double v) {
		return v - Math.floor(v);
	}

	public static double func_181160_c(double x, double y, double z) {
		return (x - y) / (z - y);
	}

	public static double atan2(double x, double y) {
		double d0 = y * y + x * x;

		if (Double.isNaN(d0)) {
			return Double.NaN;
		} else {
			boolean flag = x < 0.0D;

			if (flag) {
				x = -x;
			}

			boolean flag1 = y < 0.0D;

			if (flag1) {
				y = -y;
			}

			boolean flag2 = x > y;

			if (flag2) {
				double d1 = y;
				y = x;
				x = d1;
			}

			double d9 = func_181161_i(d0);
			y = y * d9;
			x = x * d9;
			double d2 = field_181163_d + x;
			int i = (int) Double.doubleToRawLongBits(d2);
			double d3 = field_181164_e[i];
			double d4 = field_181165_f[i];
			double d5 = d2 - field_181163_d;
			double d6 = x * d4 - y * d5;
			double d7 = (6.0D + d6 * d6) * d6 * 0.16666666666666666D;
			double d8 = d3 + d7;

			if (flag2) {
				d8 = (Math.PI / 2D) - d8;
			}

			if (flag1) {
				d8 = Math.PI - d8;
			}

			if (flag) {
				d8 = -d8;
			}

			return d8;
		}
	}

	public static double func_181161_i(double v) {
		double d0 = 0.5D * v;
		long i = Double.doubleToRawLongBits(v);
		i = 6910469410427058090L - (i >> 1);
		v = Double.longBitsToDouble(i);
		v = v * (1.5D - d0 * v * v);
		return v;
	}
}