package de.legoshi.parkourcalculator.simulation.environment.voxel;

import de.legoshi.parkourcalculator.simulation.Direction;
import de.legoshi.parkourcalculator.simulation.environment.voxel.DiscreteVoxelShape;
import de.legoshi.parkourcalculator.util.MinecraftMathHelper_1_20_4;

public final class SubShape extends DiscreteVoxelShape {
   private final DiscreteVoxelShape parent;
   private final int startX;
   private final int startY;
   private final int startZ;
   private final int endX;
   private final int endY;
   private final int endZ;

   protected SubShape(DiscreteVoxelShape var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      super(var5 - var2, var6 - var3, var7 - var4);
      this.parent = var1;
      this.startX = var2;
      this.startY = var3;
      this.startZ = var4;
      this.endX = var5;
      this.endY = var6;
      this.endZ = var7;
   }

   public boolean isFull(int var1, int var2, int var3) {
      return this.parent.isFull(this.startX + var1, this.startY + var2, this.startZ + var3);
   }

   public void fill(int var1, int var2, int var3) {
      this.parent.fill(this.startX + var1, this.startY + var2, this.startZ + var3);
   }

   public int firstFull(Direction.Axis var1) {
      return this.clampToShape(var1, this.parent.firstFull(var1));
   }

   public int lastFull(Direction.Axis var1) {
      return this.clampToShape(var1, this.parent.lastFull(var1));
   }

   private int clampToShape(Direction.Axis var1, int var2) {
      int var3 = var1.choose(this.startX, this.startY, this.startZ);
      int var4 = var1.choose(this.endX, this.endY, this.endZ);
      return MinecraftMathHelper_1_20_4.clamp(var2, var3, var4) - var3;
   }
}
