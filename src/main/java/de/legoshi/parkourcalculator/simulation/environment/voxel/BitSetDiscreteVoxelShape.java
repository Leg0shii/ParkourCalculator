package de.legoshi.parkourcalculator.simulation.environment.voxel;

import de.legoshi.parkourcalculator.simulation.Direction;
import de.legoshi.parkourcalculator.simulation.environment.merger.IndexMerger;
import de.legoshi.parkourcalculator.util.BooleanOp;

import java.util.BitSet;

public final class BitSetDiscreteVoxelShape extends DiscreteVoxelShape {
   private final BitSet storage;
   private int xMin;
   private int yMin;
   private int zMin;
   private int xMax;
   private int yMax;
   private int zMax;

   public BitSetDiscreteVoxelShape(int var1, int var2, int var3) {
      super(var1, var2, var3);
      this.storage = new BitSet(var1 * var2 * var3);
      this.xMin = var1;
      this.yMin = var2;
      this.zMin = var3;
   }

   public static BitSetDiscreteVoxelShape withFilledBounds(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      BitSetDiscreteVoxelShape var9 = new BitSetDiscreteVoxelShape(var0, var1, var2);
      var9.xMin = var3;
      var9.yMin = var4;
      var9.zMin = var5;
      var9.xMax = var6;
      var9.yMax = var7;
      var9.zMax = var8;

      for(int var10 = var3; var10 < var6; ++var10) {
         for(int var11 = var4; var11 < var7; ++var11) {
            for(int var12 = var5; var12 < var8; ++var12) {
               var9.fillUpdateBounds(var10, var11, var12, false);
            }
         }
      }

      return var9;
   }

   public BitSetDiscreteVoxelShape(DiscreteVoxelShape var1) {
      super(var1.xSize, var1.ySize, var1.zSize);
      if (var1 instanceof BitSetDiscreteVoxelShape) {
         this.storage = (BitSet)((BitSetDiscreteVoxelShape)var1).storage.clone();
      } else {
         this.storage = new BitSet(this.xSize * this.ySize * this.zSize);

         for(int var2 = 0; var2 < this.xSize; ++var2) {
            for(int var3 = 0; var3 < this.ySize; ++var3) {
               for(int var4 = 0; var4 < this.zSize; ++var4) {
                  if (var1.isFull(var2, var3, var4)) {
                     this.storage.set(this.getIndex(var2, var3, var4));
                  }
               }
            }
         }
      }

      this.xMin = var1.firstFull(Direction.Axis.X);
      this.yMin = var1.firstFull(Direction.Axis.Y);
      this.zMin = var1.firstFull(Direction.Axis.Z);
      this.xMax = var1.lastFull(Direction.Axis.X);
      this.yMax = var1.lastFull(Direction.Axis.Y);
      this.zMax = var1.lastFull(Direction.Axis.Z);
   }

   protected int getIndex(int var1, int var2, int var3) {
      return (var1 * this.ySize + var2) * this.zSize + var3;
   }

   public boolean isFull(int var1, int var2, int var3) {
      return this.storage.get(this.getIndex(var1, var2, var3));
   }

   private void fillUpdateBounds(int var1, int var2, int var3, boolean var4) {
      this.storage.set(this.getIndex(var1, var2, var3));
      if (var4) {
         this.xMin = Math.min(this.xMin, var1);
         this.yMin = Math.min(this.yMin, var2);
         this.zMin = Math.min(this.zMin, var3);
         this.xMax = Math.max(this.xMax, var1 + 1);
         this.yMax = Math.max(this.yMax, var2 + 1);
         this.zMax = Math.max(this.zMax, var3 + 1);
      }

   }

   public void fill(int var1, int var2, int var3) {
      this.fillUpdateBounds(var1, var2, var3, true);
   }

   public boolean isEmpty() {
      return this.storage.isEmpty();
   }

   public int firstFull(Direction.Axis var1) {
      return var1.choose(this.xMin, this.yMin, this.zMin);
   }

   public int lastFull(Direction.Axis var1) {
      return var1.choose(this.xMax, this.yMax, this.zMax);
   }

   static BitSetDiscreteVoxelShape join(DiscreteVoxelShape var0, DiscreteVoxelShape var1, IndexMerger var2, IndexMerger var3, IndexMerger var4, BooleanOp var5) {
      BitSetDiscreteVoxelShape var6 = new BitSetDiscreteVoxelShape(var2.size() - 1, var3.size() - 1, var4.size() - 1);
      int[] var7 = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};
      var2.forMergedIndexes((var7x, var8, var9) -> {
         boolean[] var10 = new boolean[]{false};
         var3.forMergedIndexes((var10x, var11, var12) -> {
            boolean[] var13 = new boolean[]{false};
            var4.forMergedIndexes((var12x, var13x, var14) -> {
               if (var5.apply(var0.isFullWide(var7x, var10x, var12x), var1.isFullWide(var8, var11, var13x))) {
                  var6.storage.set(var6.getIndex(var9, var12, var14));
                  var7[2] = Math.min(var7[2], var14);
                  var7[5] = Math.max(var7[5], var14);
                  var13[0] = true;
               }

               return true;
            });
            if (var13[0]) {
               var7[1] = Math.min(var7[1], var12);
               var7[4] = Math.max(var7[4], var12);
               var10[0] = true;
            }

            return true;
         });
         if (var10[0]) {
            var7[0] = Math.min(var7[0], var9);
            var7[3] = Math.max(var7[3], var9);
         }

         return true;
      });
      var6.xMin = var7[0];
      var6.yMin = var7[1];
      var6.zMin = var7[2];
      var6.xMax = var7[3] + 1;
      var6.yMax = var7[4] + 1;
      var6.zMax = var7[5] + 1;
      return var6;
   }

   protected static void forAllBoxes(DiscreteVoxelShape var0, DiscreteVoxelShape.IntLineConsumer var1, boolean var2) {
      BitSetDiscreteVoxelShape var3 = new BitSetDiscreteVoxelShape(var0);

      for(int var4 = 0; var4 < var3.ySize; ++var4) {
         for(int var5 = 0; var5 < var3.xSize; ++var5) {
            int var6 = -1;

            for(int var7 = 0; var7 <= var3.zSize; ++var7) {
               if (var3.isFullWide(var5, var4, var7)) {
                  if (var2) {
                     if (var6 == -1) {
                        var6 = var7;
                     }
                  } else {
                     var1.consume(var5, var4, var7, var5 + 1, var4 + 1, var7 + 1);
                  }
               } else if (var6 != -1) {
                  int var8 = var5;
                  int var9 = var4;
                  var3.clearZStrip(var6, var7, var5, var4);

                  while(var3.isZStripFull(var6, var7, var8 + 1, var4)) {
                     var3.clearZStrip(var6, var7, var8 + 1, var4);
                     ++var8;
                  }

                  while(var3.isXZRectangleFull(var5, var8 + 1, var6, var7, var9 + 1)) {
                     for(int var10 = var5; var10 <= var8; ++var10) {
                        var3.clearZStrip(var6, var7, var10, var9 + 1);
                     }

                     ++var9;
                  }

                  var1.consume(var5, var4, var6, var8 + 1, var9 + 1, var7);
                  var6 = -1;
               }
            }
         }
      }

   }

   private boolean isZStripFull(int var1, int var2, int var3, int var4) {
      if (var3 < this.xSize && var4 < this.ySize) {
         return this.storage.nextClearBit(this.getIndex(var3, var4, var1)) >= this.getIndex(var3, var4, var2);
      } else {
         return false;
      }
   }

   private boolean isXZRectangleFull(int var1, int var2, int var3, int var4, int var5) {
      for(int var6 = var1; var6 < var2; ++var6) {
         if (!this.isZStripFull(var3, var4, var6, var5)) {
            return false;
         }
      }

      return true;
   }

   private void clearZStrip(int var1, int var2, int var3, int var4) {
      this.storage.clear(this.getIndex(var3, var4, var1), this.getIndex(var3, var4, var2));
   }
}
