package de.legoshi.parkourcalculator.simulation.environment.voxel;

import de.legoshi.parkourcalculator.simulation.Direction;
import de.legoshi.parkourcalculator.util.MinecraftMathHelper_1_20_4;

import java.util.ArrayList;
import java.util.List;

public final class CubeVoxelShape extends VoxelShape {
   protected CubeVoxelShape(DiscreteVoxelShape var1) {
      super(var1);
   }

   protected List<Double> getCoords(Direction.Axis var1) {
      return new CubePointRange(this.shape.getSize(var1));
   }

   public int findIndex(Direction.Axis var1, double var2) {
      int var4 = this.shape.getSize(var1);
      return MinecraftMathHelper_1_20_4.floor(MinecraftMathHelper_1_20_4.clamp(var2 * (double)var4, -1.0D, (double)var4));
   }
}
