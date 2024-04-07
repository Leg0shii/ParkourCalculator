package de.legoshi.parkourcalculator.simulation.environment.merger;


import de.legoshi.parkourcalculator.simulation.environment.voxel.CubePointRange;
import de.legoshi.parkourcalculator.simulation.environment.voxel.Shapes;
import de.legoshi.parkourcalculator.util.MinecraftMathHelper_1_20_4;

import java.util.List;

public final class DiscreteCubeMerger implements IndexMerger {
   private final List<Double> result;
   private final int firstDiv;
   private final int secondDiv;

   public DiscreteCubeMerger(int var1, int var2) {
      this.result = new CubePointRange((int) Shapes.lcm(var1, var2));
      int var3 = MinecraftMathHelper_1_20_4.gcd(var1, var2);
      this.firstDiv = var1 / var3;
      this.secondDiv = var2 / var3;
   }

   public boolean forMergedIndexes(IndexMerger.IndexConsumer var1) {
      int var2 = this.result.size() - 1;

      for(int var3 = 0; var3 < var2; ++var3) {
         if (!var1.merge(var3 / this.secondDiv, var3 / this.firstDiv, var3)) {
            return false;
         }
      }

      return true;
   }

   public int size() {
      return this.result.size();
   }

   public List<Double> getList() {
      return this.result;
   }
}
