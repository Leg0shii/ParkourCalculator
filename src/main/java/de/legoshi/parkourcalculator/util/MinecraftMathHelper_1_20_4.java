package de.legoshi.parkourcalculator.util;

import java.util.Locale;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;

public class MinecraftMathHelper_1_20_4 {

    private static final long UUID_VERSION = 61440L;
    private static final long UUID_VERSION_TYPE_4 = 16384L;
    private static final long UUID_VARIANT = -4611686018427387904L;
    private static final long UUID_VARIANT_2 = Long.MIN_VALUE;
    public static final float PI = 3.1415927F;
    public static final float HALF_PI = 1.5707964F;
    public static final float TWO_PI = 6.2831855F;
    public static final float DEG_TO_RAD = 0.017453292F;
    public static final float RAD_TO_DEG = 57.295776F;
    public static final float EPSILON = 1.0E-5F;
    public static final float SQRT_OF_TWO = sqrt(2.0F);
    private static final float SIN_SCALE = 10430.378F;
    private static final float[] SIN = (float[])make(new float[65536], (var0x) -> {
        for(int var1 = 0; var1 < var0x.length; ++var1) {
            var0x[var1] = (float)Math.sin((double)var1 * 3.141592653589793D * 2.0D / 65536.0D);
        }

    });

    private static final int[] MULTIPLY_DE_BRUIJN_BIT_POSITION = new int[]{0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9};
    private static final double ONE_SIXTH = 0.16666666666666666D;
    private static final int FRAC_EXP = 8;
    private static final int LUT_SIZE = 257;
    private static final double FRAC_BIAS = Double.longBitsToDouble(4805340802404319232L);
    private static final double[] ASIN_TAB = new double[257];
    private static final double[] COS_TAB = new double[257];

    public static <T> T make(T var0, Consumer<? super T> var1) {
        var1.accept(var0);
        return var0;
    }

    public static float sin(float var0) {
        return SIN[(int)(var0 * 10430.378F) & '\uffff'];
    }

    public static float cos(float var0) {
        return SIN[(int)(var0 * 10430.378F + 16384.0F) & '\uffff'];
    }

    public static float sqrt(float var0) {
        return (float)Math.sqrt((double)var0);
    }

    public static int floor(float var0) {
        int var1 = (int)var0;
        return var0 < (float)var1 ? var1 - 1 : var1;
    }

    public static int floor(double var0) {
        int var2 = (int)var0;
        return var0 < (double)var2 ? var2 - 1 : var2;
    }

    public static long lfloor(double var0) {
        long var2 = (long)var0;
        return var0 < (double)var2 ? var2 - 1L : var2;
    }

    public static float abs(float var0) {
        return Math.abs(var0);
    }

    public static int abs(int var0) {
        return Math.abs(var0);
    }

    public static int ceil(float var0) {
        int var1 = (int)var0;
        return var0 > (float)var1 ? var1 + 1 : var1;
    }

    public static int ceil(double var0) {
        int var2 = (int)var0;
        return var0 > (double)var2 ? var2 + 1 : var2;
    }

    public static int clamp(int var0, int var1, int var2) {
        return Math.min(Math.max(var0, var1), var2);
    }

    public static long clamp(long var0, long var2, long var4) {
        return Math.min(Math.max(var0, var2), var4);
    }

    public static float clamp(float var0, float var1, float var2) {
        return var0 < var1 ? var1 : Math.min(var0, var2);
    }

    public static double clamp(double var0, double var2, double var4) {
        return var0 < var2 ? var2 : Math.min(var0, var4);
    }

    public static double clampedLerp(double var0, double var2, double var4) {
        if (var4 < 0.0D) {
            return var0;
        } else {
            return var4 > 1.0D ? var2 : lerp(var4, var0, var2);
        }
    }

    public static float clampedLerp(float var0, float var1, float var2) {
        if (var2 < 0.0F) {
            return var0;
        } else {
            return var2 > 1.0F ? var1 : lerp(var2, var0, var1);
        }
    }

    public static double absMax(double var0, double var2) {
        if (var0 < 0.0D) {
            var0 = -var0;
        }

        if (var2 < 0.0D) {
            var2 = -var2;
        }

        return Math.max(var0, var2);
    }

    public static int floorDiv(int var0, int var1) {
        return Math.floorDiv(var0, var1);
    }

    public static boolean equal(float var0, float var1) {
        return Math.abs(var1 - var0) < 1.0E-5F;
    }

    public static boolean equal(double var0, double var2) {
        return Math.abs(var2 - var0) < 9.999999747378752E-6D;
    }

    public static int positiveModulo(int var0, int var1) {
        return Math.floorMod(var0, var1);
    }

    public static float positiveModulo(float var0, float var1) {
        return (var0 % var1 + var1) % var1;
    }

    public static double positiveModulo(double var0, double var2) {
        return (var0 % var2 + var2) % var2;
    }

    public static boolean isMultipleOf(int var0, int var1) {
        return var0 % var1 == 0;
    }

    public static int wrapDegrees(int var0) {
        int var1 = var0 % 360;
        if (var1 >= 180) {
            var1 -= 360;
        }

        if (var1 < -180) {
            var1 += 360;
        }

        return var1;
    }

    public static float wrapDegrees(float var0) {
        float var1 = var0 % 360.0F;
        if (var1 >= 180.0F) {
            var1 -= 360.0F;
        }

        if (var1 < -180.0F) {
            var1 += 360.0F;
        }

        return var1;
    }

    public static double wrapDegrees(double var0) {
        double var2 = var0 % 360.0D;
        if (var2 >= 180.0D) {
            var2 -= 360.0D;
        }

        if (var2 < -180.0D) {
            var2 += 360.0D;
        }

        return var2;
    }

    public static float degreesDifference(float var0, float var1) {
        return wrapDegrees(var1 - var0);
    }

    public static float degreesDifferenceAbs(float var0, float var1) {
        return abs(degreesDifference(var0, var1));
    }

    public static float rotateIfNecessary(float var0, float var1, float var2) {
        float var3 = degreesDifference(var0, var1);
        float var4 = clamp(var3, -var2, var2);
        return var1 - var4;
    }

    public static float approach(float var0, float var1, float var2) {
        var2 = abs(var2);
        return var0 < var1 ? clamp(var0 + var2, var0, var1) : clamp(var0 - var2, var1, var0);
    }

    public static float approachDegrees(float var0, float var1, float var2) {
        float var3 = degreesDifference(var0, var1);
        return approach(var0, var0 + var3, var2);
    }

    public static int getInt(String var0, int var1) {
        return 0; //NumberUtils.toInt(var0, var1);
    }

    public static int smallestEncompassingPowerOfTwo(int var0) {
        int var1 = var0 - 1;
        var1 |= var1 >> 1;
        var1 |= var1 >> 2;
        var1 |= var1 >> 4;
        var1 |= var1 >> 8;
        var1 |= var1 >> 16;
        return var1 + 1;
    }

    public static boolean isPowerOfTwo(int var0) {
        return var0 != 0 && (var0 & var0 - 1) == 0;
    }

    public static int ceillog2(int var0) {
        var0 = isPowerOfTwo(var0) ? var0 : smallestEncompassingPowerOfTwo(var0);
        return MULTIPLY_DE_BRUIJN_BIT_POSITION[(int)((long)var0 * 125613361L >> 27) & 31];
    }

    public static int log2(int var0) {
        return ceillog2(var0) - (isPowerOfTwo(var0) ? 0 : 1);
    }


    public static float frac(float var0) {
        return var0 - (float)floor(var0);
    }

    public static double frac(double var0) {
        return var0 - (double)lfloor(var0);
    }


    /** @deprecated */
    @Deprecated
    public static long getSeed(int var0, int var1, int var2) {
        long var3 = (long)(var0 * 3129871) ^ (long)var2 * 116129781L ^ (long)var1;
        var3 = var3 * var3 * 42317861L + var3 * 11L;
        return var3 >> 16;
    }

    public static double inverseLerp(double var0, double var2, double var4) {
        return (var0 - var2) / (var4 - var2);
    }

    public static float inverseLerp(float var0, float var1, float var2) {
        return (var0 - var1) / (var2 - var1);
    }

    public static double atan2(double var0, double var2) {
        double var4 = var2 * var2 + var0 * var0;
        if (Double.isNaN(var4)) {
            return Double.NaN;
        } else {
            boolean var6 = var0 < 0.0D;
            if (var6) {
                var0 = -var0;
            }

            boolean var7 = var2 < 0.0D;
            if (var7) {
                var2 = -var2;
            }

            boolean var8 = var0 > var2;
            double var9;
            if (var8) {
                var9 = var2;
                var2 = var0;
                var0 = var9;
            }

            var9 = fastInvSqrt(var4);
            var2 *= var9;
            var0 *= var9;
            double var11 = FRAC_BIAS + var0;
            int var13 = (int)Double.doubleToRawLongBits(var11);
            double var14 = ASIN_TAB[var13];
            double var16 = COS_TAB[var13];
            double var18 = var11 - FRAC_BIAS;
            double var20 = var0 * var16 - var2 * var18;
            double var22 = (6.0D + var20 * var20) * var20 * 0.16666666666666666D;
            double var24 = var14 + var22;
            if (var8) {
                var24 = 1.5707963267948966D - var24;
            }

            if (var7) {
                var24 = 3.141592653589793D - var24;
            }

            if (var6) {
                var24 = -var24;
            }

            return var24;
        }
    }

    /** @deprecated */
    @Deprecated
    public static double fastInvSqrt(double var0) {
        double var2 = 0.5D * var0;
        long var4 = Double.doubleToRawLongBits(var0);
        var4 = 6910469410427058090L - (var4 >> 1);
        var0 = Double.longBitsToDouble(var4);
        var0 *= 1.5D - var2 * var0 * var0;
        return var0;
    }

    public static float fastInvCubeRoot(float var0) {
        int var1 = Float.floatToIntBits(var0);
        var1 = 1419967116 - var1 / 3;
        float var2 = Float.intBitsToFloat(var1);
        var2 = 0.6666667F * var2 + 1.0F / (3.0F * var2 * var2 * var0);
        var2 = 0.6666667F * var2 + 1.0F / (3.0F * var2 * var2 * var0);
        return var2;
    }

    public static int murmurHash3Mixer(int var0) {
        var0 ^= var0 >>> 16;
        var0 *= -2048144789;
        var0 ^= var0 >>> 13;
        var0 *= -1028477387;
        var0 ^= var0 >>> 16;
        return var0;
    }

    public static int binarySearch(int var0, int var1, IntPredicate var2) {
        int var3 = var1 - var0;

        while(var3 > 0) {
            int var4 = var3 / 2;
            int var5 = var0 + var4;
            if (var2.test(var5)) {
                var3 = var4;
            } else {
                var0 = var5 + 1;
                var3 -= var4 + 1;
            }
        }

        return var0;
    }

    public static int lerpInt(float var0, int var1, int var2) {
        return var1 + floor(var0 * (float)(var2 - var1));
    }

    public static int lerpDiscrete(float var0, int var1, int var2) {
        int var3 = var2 - var1;
        return var1 + floor(var0 * (float)(var3 - 1)) + (var0 > 0.0F ? 1 : 0);
    }

    public static float lerp(float var0, float var1, float var2) {
        return var1 + var0 * (var2 - var1);
    }

    public static double lerp(double var0, double var2, double var4) {
        return var2 + var0 * (var4 - var2);
    }

    public static double lerp2(double var0, double var2, double var4, double var6, double var8, double var10) {
        return lerp(var2, lerp(var0, var4, var6), lerp(var0, var8, var10));
    }

    public static double lerp3(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18, double var20) {
        return lerp(var4, lerp2(var0, var2, var6, var8, var10, var12), lerp2(var0, var2, var14, var16, var18, var20));
    }

    public static float catmullrom(float var0, float var1, float var2, float var3, float var4) {
        return 0.5F * (2.0F * var2 + (var3 - var1) * var0 + (2.0F * var1 - 5.0F * var2 + 4.0F * var3 - var4) * var0 * var0 + (3.0F * var2 - var1 - 3.0F * var3 + var4) * var0 * var0 * var0);
    }

    public static double smoothstep(double var0) {
        return var0 * var0 * var0 * (var0 * (var0 * 6.0D - 15.0D) + 10.0D);
    }

    public static double smoothstepDerivative(double var0) {
        return 30.0D * var0 * var0 * (var0 - 1.0D) * (var0 - 1.0D);
    }

    public static int sign(double var0) {
        if (var0 == 0.0D) {
            return 0;
        } else {
            return var0 > 0.0D ? 1 : -1;
        }
    }

    public static float rotLerp(float var0, float var1, float var2) {
        return var1 + var0 * wrapDegrees(var2 - var1);
    }

    public static double rotLerp(double var0, double var2, double var4) {
        return var2 + var0 * wrapDegrees(var4 - var2);
    }

    public static float triangleWave(float var0, float var1) {
        return (Math.abs(var0 % var1 - var1 * 0.5F) - var1 * 0.25F) / (var1 * 0.25F);
    }

    public static float square(float var0) {
        return var0 * var0;
    }

    public static double square(double var0) {
        return var0 * var0;
    }

    public static int square(int var0) {
        return var0 * var0;
    }

    public static long square(long var0) {
        return var0 * var0;
    }

    public static double clampedMap(double var0, double var2, double var4, double var6, double var8) {
        return clampedLerp(var6, var8, inverseLerp(var0, var2, var4));
    }

    public static float clampedMap(float var0, float var1, float var2, float var3, float var4) {
        return clampedLerp(var3, var4, inverseLerp(var0, var1, var2));
    }

    public static double map(double var0, double var2, double var4, double var6, double var8) {
        return lerp(inverseLerp(var0, var2, var4), var6, var8);
    }

    public static float map(float var0, float var1, float var2, float var3, float var4) {
        return lerp(inverseLerp(var0, var1, var2), var3, var4);
    }

    public static int roundToward(int var0, int var1) {
        return positiveCeilDiv(var0, var1) * var1;
    }

    public static int positiveCeilDiv(int var0, int var1) {
        return -Math.floorDiv(-var0, var1);
    }

    public static double lengthSquared(double var0, double var2) {
        return var0 * var0 + var2 * var2;
    }

    public static double length(double var0, double var2) {
        return Math.sqrt(lengthSquared(var0, var2));
    }

    public static double lengthSquared(double var0, double var2, double var4) {
        return var0 * var0 + var2 * var2 + var4 * var4;
    }

    public static double length(double var0, double var2, double var4) {
        return Math.sqrt(lengthSquared(var0, var2, var4));
    }

    public static int quantize(double var0, int var2) {
        return floor(var0 / (double)var2) * var2;
    }

    public static IntStream outFromOrigin(int var0, int var1, int var2) {
        return outFromOrigin(var0, var1, var2, 1);
    }

    public static IntStream outFromOrigin(int var0, int var1, int var2, int var3) {
        if (var1 > var2) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "upperbound %d expected to be > lowerBound %d", var2, var1));
        } else if (var3 < 1) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "steps expected to be >= 1, was %d", var3));
        } else {
            return var0 >= var1 && var0 <= var2 ? IntStream.iterate(var0, (var3x) -> {
                int var4 = Math.abs(var0 - var3x);
                return var0 - var4 >= var1 || var0 + var4 <= var2;
            }, (var4) -> {
                boolean var5 = var4 <= var0;
                int var6 = Math.abs(var0 - var4);
                boolean var7 = var0 + var6 + var3 <= var2;
                if (!var5 || !var7) {
                    int var8 = var0 - var6 - (var5 ? var3 : 0);
                    if (var8 >= var1) {
                        return var8;
                    }
                }

                return var0 + var6 + var3;
            }) : IntStream.empty();
        }
    }

    static {
        for(int var0 = 0; var0 < 257; ++var0) {
            double var1 = (double)var0 / 256.0D;
            double var3 = Math.asin(var1);
            COS_TAB[var0] = Math.cos(var3);
            ASIN_TAB[var0] = var3;
        }

    }

}
