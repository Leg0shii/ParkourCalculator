package de.legoshi.parkourcalculator.simulation.environment.voxel;

import de.legoshi.parkourcalculator.simulation.AxisCycle;
import de.legoshi.parkourcalculator.simulation.Direction;
import de.legoshi.parkourcalculator.util.AxisAlignedBB;
import de.legoshi.parkourcalculator.util.BooleanOp;
import de.legoshi.parkourcalculator.util.MinecraftMathHelper_1_20_4;
import de.legoshi.parkourcalculator.util.Vec3;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class VoxelShape {
   public final DiscreteVoxelShape shape;
   private VoxelShape[] faces;

   VoxelShape(DiscreteVoxelShape var1) {
      this.shape = var1;
   }

   public double min(Direction.Axis var1) {
      int var2 = this.shape.firstFull(var1);
      return var2 >= this.shape.getSize(var1) ? Double.POSITIVE_INFINITY : this.get(var1, var2);
   }

   public double max(Direction.Axis var1) {
      int var2 = this.shape.lastFull(var1);
      return var2 <= 0 ? Double.NEGATIVE_INFINITY : this.get(var1, var2);
   }

   public AxisAlignedBB bounds() {
      if (this.isEmpty()) {
         return null;
      } else {
         return new AxisAlignedBB(this.min(Direction.Axis.X), this.min(Direction.Axis.Y), this.min(Direction.Axis.Z), this.max(Direction.Axis.X), this.max(Direction.Axis.Y), this.max(Direction.Axis.Z));
      }
   }

   public VoxelShape singleEncompassing() {
      return this.isEmpty() ? Shapes.empty() : Shapes.box(this.min(Direction.Axis.X), this.min(Direction.Axis.Y), this.min(Direction.Axis.Z), this.max(Direction.Axis.X), this.max(Direction.Axis.Y), this.max(Direction.Axis.Z));
   }

   public double get(Direction.Axis var1, int var2) {
      return this.getCoords(var1).get(var2);
   }

   protected abstract List<Double> getCoords(Direction.Axis var1);

   public boolean isEmpty() {
      return this.shape.isEmpty();
   }

   public VoxelShape optimize() {
      VoxelShape[] var1 = new VoxelShape[]{Shapes.empty()};
      this.forAllBoxes((var1x, var3, var5, var7, var9, var11) -> {
         var1[0] = Shapes.joinUnoptimized(var1[0], Shapes.box(var1x, var3, var5, var7, var9, var11), BooleanOp.OR);
      });
      return var1[0];
   }

   public void forAllEdges(Shapes.DoubleLineConsumer var1) {
      this.shape.forAllEdges((var2, var3, var4, var5, var6, var7) -> {
         var1.consume(this.get(Direction.Axis.X, var2), this.get(Direction.Axis.Y, var3), this.get(Direction.Axis.Z, var4), this.get(Direction.Axis.X, var5), this.get(Direction.Axis.Y, var6), this.get(Direction.Axis.Z, var7));
      }, true);
   }

   public void forAllBoxes(Shapes.DoubleLineConsumer var1) {
      List<Double> var2 = this.getCoords(Direction.Axis.X);
      List<Double> var3 = this.getCoords(Direction.Axis.Y);
      List<Double> var4 = this.getCoords(Direction.Axis.Z);
      this.shape.forAllBoxes((var4x, var5, var6, var7, var8, var9) -> {
         var1.consume(var2.get(var4x), var3.get(var5), var4.get(var6), var2.get(var7), var3.get(var8), var4.get(var9));
      }, true);
   }

   public List<AxisAlignedBB> toAabbs() {
      List<AxisAlignedBB> var1 = new ArrayList<>();
      this.forAllBoxes((var1x, var3, var5, var7, var9, var11) -> {
         var1.add(new AxisAlignedBB(var1x, var3, var5, var7, var9, var11));
      });
      return var1;
   }

   public double min(Direction.Axis var1, double var2, double var4) {
      Direction.Axis var6 = AxisCycle.FORWARD.cycle(var1);
      Direction.Axis var7 = AxisCycle.BACKWARD.cycle(var1);
      int var8 = this.findIndex(var6, var2);
      int var9 = this.findIndex(var7, var4);
      int var10 = this.shape.firstFull(var1, var8, var9);
      return var10 >= this.shape.getSize(var1) ? Double.POSITIVE_INFINITY : this.get(var1, var10);
   }

   public double max(Direction.Axis var1, double var2, double var4) {
      Direction.Axis var6 = AxisCycle.FORWARD.cycle(var1);
      Direction.Axis var7 = AxisCycle.BACKWARD.cycle(var1);
      int var8 = this.findIndex(var6, var2);
      int var9 = this.findIndex(var7, var4);
      int var10 = this.shape.lastFull(var1, var8, var9);
      return var10 <= 0 ? Double.NEGATIVE_INFINITY : this.get(var1, var10);
   }

   public int findIndex(Direction.Axis var1, double var2) {
      return MinecraftMathHelper_1_20_4.binarySearch(0, this.shape.getSize(var1) + 1, (var4) -> {
         return var2 < this.get(var1, var4);
      }) - 1;
   }

   public Optional<Vec3> closestPointTo(Vec3 var1) {
      if (this.isEmpty()) {
         return Optional.empty();
      } else {
         Vec3[] var2 = new Vec3[1];
         this.forAllBoxes((var2x, var4, var6, var8, var10, var12) -> {
            double var14 = MinecraftMathHelper_1_20_4.clamp(var1.x, var2x, var8);
            double var16 = MinecraftMathHelper_1_20_4.clamp(var1.y, var4, var10);
            double var18 = MinecraftMathHelper_1_20_4.clamp(var1.z, var6, var12);
            if (var2[0] == null || var1.distanceToSqr(var14, var16, var18) < var1.distanceToSqr(var2[0])) {
               var2[0] = new Vec3(var14, var16, var18);
            }

         });
         return Optional.of(var2[0]);
      }
   }

   public VoxelShape getFaceShape(Direction var1) {
      if (!this.isEmpty() && this != Shapes.block()) {
         VoxelShape var2;
         if (this.faces != null) {
            var2 = this.faces[var1.ordinal()];
            if (var2 != null) {
               return var2;
            }
         } else {
            this.faces = new VoxelShape[6];
         }

         var2 = this.calculateFace(var1);
         this.faces[var1.ordinal()] = var2;
         return var2;
      } else {
         return this;
      }
   }

   private VoxelShape calculateFace(Direction var1) {
      Direction.Axis var2 = var1.getAxis();
      List<Double> var3 = this.getCoords(var2);
      if (var3.size() == 2 && MinecraftMathHelper_1_20_4.fuzzyEquals(var3.get(0), 0.0D, 1.0E-7D)
              && MinecraftMathHelper_1_20_4.fuzzyEquals(var3.get(1), 1.0D, 1.0E-7D)) {
         return this;
      } else {
         Direction.AxisDirection var4 = var1.getAxisDirection();
         int var5 = this.findIndex(var2, var4 == Direction.AxisDirection.POSITIVE ? 0.9999999D : 1.0E-7D);
         return new SliceShape(this, var2, var5);
      }
   }

   public double collide(Direction.Axis var1, AxisAlignedBB var2, double var3) {
      return this.collideX(AxisCycle.between(var1, Direction.Axis.X), var2, var3);
   }

   protected double collideX(AxisCycle var1, AxisAlignedBB var2, double var3) {
      if (this.isEmpty()) {
         return var3;
      } else if (Math.abs(var3) < 1.0E-7D) {
         return 0.0D;
      } else {
         AxisCycle var5 = var1.inverse();
         Direction.Axis var6 = var5.cycle(Direction.Axis.X);
         Direction.Axis var7 = var5.cycle(Direction.Axis.Y);
         Direction.Axis var8 = var5.cycle(Direction.Axis.Z);
         double var9 = var2.max(var6);
         double var11 = var2.min(var6);
         int var13 = this.findIndex(var6, var11 + 1.0E-7D);
         int var14 = this.findIndex(var6, var9 - 1.0E-7D);
         int var15 = Math.max(0, this.findIndex(var7, var2.min(var7) + 1.0E-7D));
         int var16 = Math.min(this.shape.getSize(var7), this.findIndex(var7, var2.max(var7) - 1.0E-7D) + 1);
         int var17 = Math.max(0, this.findIndex(var8, var2.min(var8) + 1.0E-7D));
         int var18 = Math.min(this.shape.getSize(var8), this.findIndex(var8, var2.max(var8) - 1.0E-7D) + 1);
         int var19 = this.shape.getSize(var6);
         int var20;
         int var21;
         int var22;
         double var23;
         if (var3 > 0.0D) {
            for(var20 = var14 + 1; var20 < var19; ++var20) {
               for(var21 = var15; var21 < var16; ++var21) {
                  for(var22 = var17; var22 < var18; ++var22) {
                     if (this.shape.isFullWide(var5, var20, var21, var22)) {
                        var23 = this.get(var6, var20) - var9;
                        if (var23 >= -1.0E-7D) {
                           var3 = Math.min(var3, var23);
                        }

                        return var3;
                     }
                  }
               }
            }
         } else if (var3 < 0.0D) {
            for(var20 = var13 - 1; var20 >= 0; --var20) {
               for(var21 = var15; var21 < var16; ++var21) {
                  for(var22 = var17; var22 < var18; ++var22) {
                     if (this.shape.isFullWide(var5, var20, var21, var22)) {
                        var23 = this.get(var6, var20 + 1) - var11;
                        if (var23 <= 1.0E-7D) {
                           var3 = Math.max(var3, var23);
                        }

                        return var3;
                     }
                  }
               }
            }
         }

         return var3;
      }
   }

   public String toString() {
      return this.isEmpty() ? "EMPTY" : "VoxelShape[" + this.bounds() + "]";
   }
}
