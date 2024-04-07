package de.legoshi.parkourcalculator.simulation.environment.merger;

import java.util.List;

public class IdenticalMerger implements IndexMerger {
   private final List<Double> coords;

   public IdenticalMerger(List<Double> var1) {
      this.coords = var1;
   }

   public boolean forMergedIndexes(IndexMerger.IndexConsumer var1) {
      int var2 = this.coords.size() - 1;

      for(int var3 = 0; var3 < var2; ++var3) {
         if (!var1.merge(var3, var3, var3)) {
            return false;
         }
      }

      return true;
   }

   public int size() {
      return this.coords.size();
   }

   public List<Double> getList() {
      return this.coords;
   }
}
