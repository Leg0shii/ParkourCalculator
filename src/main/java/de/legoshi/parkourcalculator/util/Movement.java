package de.legoshi.parkourcalculator.util;

public class Movement {

	private static final double SQRT_2 = Math.sqrt(2);

	public enum Slipperiness {
		AIR(1F),
		BLOCK(0.6F),
		SLIME(0.8F),
		ICE(0.98F);

		public final float value;

		Slipperiness(float value) {
			this.value = value;
		}
	}

	public static class SlipperinessClass {
		public static final double AIR = 1;
		public static final double BLOCK = 0.6;
	}

	public static class MovementMultipliers {
		public static final double SPRINTING = 1.3;
		public static final double WALKING = 1.0;
		public static final double SNEAKING = 0.3;
		public static final double STOPPING = 0.0;
	}

	public static class StrafingMultipliers {
		public static final double DEFAULT = 0.98;
		public static final double STRAFE = 1;
		public static final double SNEAK_STRAFE = 0.98 * SQRT_2;
	}
}
