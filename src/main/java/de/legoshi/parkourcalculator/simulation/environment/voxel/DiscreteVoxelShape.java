package de.legoshi.parkourcalculator.simulation.environment.voxel;

import de.legoshi.parkourcalculator.simulation.AxisCycle;
import de.legoshi.parkourcalculator.simulation.Direction;

public abstract class DiscreteVoxelShape {
   private static final Direction.Axis[] AXIS_VALUES = Direction.Axis.values();
   protected final int xSize;
   protected final int ySize;
   protected final int zSize;

   protected DiscreteVoxelShape(int var1, int var2, int var3) {
      if (var1 >= 0 && var2 >= 0 && var3 >= 0) {
         this.xSize = var1;
         this.ySize = var2;
         this.zSize = var3;
      } else {
         throw new IllegalArgumentException("Need all positive sizes: x: " + var1 + ", y: " + var2 + ", z: " + var3);
      }
   }

   public boolean isFullWide(AxisCycle var1, int var2, int var3, int var4) {
      return this.isFullWide(var1.cycle(var2, var3, var4, Direction.Axis.X), var1.cycle(var2, var3, var4, Direction.Axis.Y), var1.cycle(var2, var3, var4, Direction.Axis.Z));
   }

   public boolean isFullWide(int var1, int var2, int var3) {
      if (var1 >= 0 && var2 >= 0 && var3 >= 0) {
         return var1 < this.xSize && var2 < this.ySize && var3 < this.zSize ? this.isFull(var1, var2, var3) : false;
      } else {
         return false;
      }
   }

   public boolean isFull(AxisCycle var1, int var2, int var3, int var4) {
      return this.isFull(var1.cycle(var2, var3, var4, Direction.Axis.X), var1.cycle(var2, var3, var4, Direction.Axis.Y), var1.cycle(var2, var3, var4, Direction.Axis.Z));
   }

   public abstract boolean isFull(int var1, int var2, int var3);

   public abstract void fill(int var1, int var2, int var3);

   public boolean isEmpty() {
      Direction.Axis[] var1 = AXIS_VALUES;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Direction.Axis var4 = var1[var3];
         if (this.firstFull(var4) >= this.lastFull(var4)) {
            return true;
         }
      }

      return false;
   }

   public abstract int firstFull(Direction.Axis var1);

   public abstract int lastFull(Direction.Axis var1);

   public int firstFull(Direction.Axis var1, int var2, int var3) {
      int var4 = this.getSize(var1);
      if (var2 >= 0 && var3 >= 0) {
         Direction.Axis var5 = AxisCycle.FORWARD.cycle(var1);
         Direction.Axis var6 = AxisCycle.BACKWARD.cycle(var1);
         if (var2 < this.getSize(var5) && var3 < this.getSize(var6)) {
            AxisCycle var7 = AxisCycle.between(Direction.Axis.X, var1);

            for(int var8 = 0; var8 < var4; ++var8) {
               if (this.isFull(var7, var8, var2, var3)) {
                  return var8;
               }
            }

            return var4;
         } else {
            return var4;
         }
      } else {
         return var4;
      }
   }

   public int lastFull(Direction.Axis var1, int var2, int var3) {
      if (var2 >= 0 && var3 >= 0) {
         Direction.Axis var4 = AxisCycle.FORWARD.cycle(var1);
         Direction.Axis var5 = AxisCycle.BACKWARD.cycle(var1);
         if (var2 < this.getSize(var4) && var3 < this.getSize(var5)) {
            int var6 = this.getSize(var1);
            AxisCycle var7 = AxisCycle.between(Direction.Axis.X, var1);

            for(int var8 = var6 - 1; var8 >= 0; --var8) {
               if (this.isFull(var7, var8, var2, var3)) {
                  return var8 + 1;
               }
            }

            return 0;
         } else {
            return 0;
         }
      } else {
         return 0;
      }
   }

   public int getSize(Direction.Axis var1) {
      return var1.choose(this.xSize, this.ySize, this.zSize);
   }

   public int getXSize() {
      return this.getSize(Direction.Axis.X);
   }

   public int getYSize() {
      return this.getSize(Direction.Axis.Y);
   }

   public int getZSize() {
      return this.getSize(Direction.Axis.Z);
   }

   public void forAllEdges(DiscreteVoxelShape.IntLineConsumer var1, boolean var2) {
      this.forAllAxisEdges(var1, AxisCycle.NONE, var2);
      this.forAllAxisEdges(var1, AxisCycle.FORWARD, var2);
      this.forAllAxisEdges(var1, AxisCycle.BACKWARD, var2);
   }

   private void forAllAxisEdges(DiscreteVoxelShape.IntLineConsumer var1, AxisCycle var2, boolean var3) {
      AxisCycle var5 = var2.inverse();
      int var6 = this.getSize(var5.cycle(Direction.Axis.X));
      int var7 = this.getSize(var5.cycle(Direction.Axis.Y));
      int var8 = this.getSize(var5.cycle(Direction.Axis.Z));

      for(int var9 = 0; var9 <= var6; ++var9) {
         for(int var10 = 0; var10 <= var7; ++var10) {
            int var4 = -1;

            for(int var11 = 0; var11 <= var8; ++var11) {
               int var12 = 0;
               int var13 = 0;

               for(int var14 = 0; var14 <= 1; ++var14) {
                  for(int var15 = 0; var15 <= 1; ++var15) {
                     if (this.isFullWide(var5, var9 + var14 - 1, var10 + var15 - 1, var11)) {
                        ++var12;
                        var13 ^= var14 ^ var15;
                     }
                  }
               }

               if (var12 == 1 || var12 == 3 || var12 == 2 && (var13 & 1) == 0) {
                  if (var3) {
                     if (var4 == -1) {
                        var4 = var11;
                     }
                  } else {
                     var1.consume(var5.cycle(var9, var10, var11, Direction.Axis.X), var5.cycle(var9, var10, var11, Direction.Axis.Y), var5.cycle(var9, var10, var11, Direction.Axis.Z), var5.cycle(var9, var10, var11 + 1, Direction.Axis.X), var5.cycle(var9, var10, var11 + 1, Direction.Axis.Y), var5.cycle(var9, var10, var11 + 1, Direction.Axis.Z));
                  }
               } else if (var4 != -1) {
                  var1.consume(var5.cycle(var9, var10, var4, Direction.Axis.X), var5.cycle(var9, var10, var4, Direction.Axis.Y), var5.cycle(var9, var10, var4, Direction.Axis.Z), var5.cycle(var9, var10, var11, Direction.Axis.X), var5.cycle(var9, var10, var11, Direction.Axis.Y), var5.cycle(var9, var10, var11, Direction.Axis.Z));
                  var4 = -1;
               }
            }
         }
      }

   }

   public void forAllBoxes(DiscreteVoxelShape.IntLineConsumer var1, boolean var2) {
      BitSetDiscreteVoxelShape.forAllBoxes(this, var1, var2);
   }

   public void forAllFaces(DiscreteVoxelShape.IntFaceConsumer var1) {
      this.forAllAxisFaces(var1, AxisCycle.NONE);
      this.forAllAxisFaces(var1, AxisCycle.FORWARD);
      this.forAllAxisFaces(var1, AxisCycle.BACKWARD);
   }

   private void forAllAxisFaces(DiscreteVoxelShape.IntFaceConsumer var1, AxisCycle var2) {
      AxisCycle var3 = var2.inverse();
      Direction.Axis var4 = var3.cycle(Direction.Axis.Z);
      int var5 = this.getSize(var3.cycle(Direction.Axis.X));
      int var6 = this.getSize(var3.cycle(Direction.Axis.Y));
      int var7 = this.getSize(var4);
      Direction var8 = Direction.fromAxisAndDirection(var4, Direction.AxisDirection.NEGATIVE);
      Direction var9 = Direction.fromAxisAndDirection(var4, Direction.AxisDirection.POSITIVE);

      for(int var10 = 0; var10 < var5; ++var10) {
         for(int var11 = 0; var11 < var6; ++var11) {
            boolean var12 = false;

            for(int var13 = 0; var13 <= var7; ++var13) {
               boolean var14 = var13 != var7 && this.isFull(var3, var10, var11, var13);
               if (!var12 && var14) {
                  var1.consume(var8, var3.cycle(var10, var11, var13, Direction.Axis.X), var3.cycle(var10, var11, var13, Direction.Axis.Y), var3.cycle(var10, var11, var13, Direction.Axis.Z));
               }

               if (var12 && !var14) {
                  var1.consume(var9, var3.cycle(var10, var11, var13 - 1, Direction.Axis.X), var3.cycle(var10, var11, var13 - 1, Direction.Axis.Y), var3.cycle(var10, var11, var13 - 1, Direction.Axis.Z));
               }

               var12 = var14;
            }
         }
      }

   }

   public interface IntLineConsumer {
      void consume(int var1, int var2, int var3, int var4, int var5, int var6);
   }

   public interface IntFaceConsumer {
      void consume(Direction var1, int var2, int var3, int var4);
   }
}
