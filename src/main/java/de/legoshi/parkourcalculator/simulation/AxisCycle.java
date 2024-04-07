package de.legoshi.parkourcalculator.simulation;

public enum AxisCycle {
   NONE {
      public int cycle(int var1, int var2, int var3, Direction.Axis var4) {
         return var4.choose(var1, var2, var3);
      }

      public double cycle(double var1, double var3, double var5, Direction.Axis var7) {
         return var7.choose(var1, var3, var5);
      }

      public Direction.Axis cycle(Direction.Axis var1) {
         return var1;
      }

      public AxisCycle inverse() {
         return this;
      }
   },
   FORWARD {
      public int cycle(int var1, int var2, int var3, Direction.Axis var4) {
         return var4.choose(var3, var1, var2);
      }

      public double cycle(double var1, double var3, double var5, Direction.Axis var7) {
         return var7.choose(var5, var1, var3);
      }

      public Direction.Axis cycle(Direction.Axis var1) {
         return AXIS_VALUES[Math.floorMod(var1.ordinal() + 1, 3)];
      }

      public AxisCycle inverse() {
         return BACKWARD;
      }
   },
   BACKWARD {
      public int cycle(int var1, int var2, int var3, Direction.Axis var4) {
         return var4.choose(var2, var3, var1);
      }

      public double cycle(double var1, double var3, double var5, Direction.Axis var7) {
         return var7.choose(var3, var5, var1);
      }

      public Direction.Axis cycle(Direction.Axis var1) {
         return AXIS_VALUES[Math.floorMod(var1.ordinal() - 1, 3)];
      }

      public AxisCycle inverse() {
         return FORWARD;
      }
   };

   public static final Direction.Axis[] AXIS_VALUES = Direction.Axis.values();
   public static final AxisCycle[] VALUES = values();

   AxisCycle() {
   }

   public abstract int cycle(int var1, int var2, int var3, Direction.Axis var4);

   public abstract double cycle(double var1, double var3, double var5, Direction.Axis var7);

   public abstract Direction.Axis cycle(Direction.Axis var1);

   public abstract AxisCycle inverse();

   public static AxisCycle between(Direction.Axis var0, Direction.Axis var1) {
      return VALUES[Math.floorMod(var1.ordinal() - var0.ordinal(), 3)];
   }
}
