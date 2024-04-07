package de.legoshi.parkourcalculator.simulation.environment.merger;

import java.util.ArrayList;
import java.util.List;

public class NonOverlappingMerger extends ArrayList<Double> implements IndexMerger {
   private final List<Double> lower;
   private final List<Double> upper;
   private final boolean swap;

   public NonOverlappingMerger(List<Double> var1, List<Double> var2, boolean var3) {
      this.lower = var1;
      this.upper = var2;
      this.swap = var3;
   }

   public int size() {
      return this.lower.size() + this.upper.size();
   }

   public boolean forMergedIndexes(IndexMerger.IndexConsumer var1) {
      return this.swap ? this.forNonSwappedIndexes((var1x, var2, var3) -> {
         return var1.merge(var2, var1x, var3);
      }) : this.forNonSwappedIndexes(var1);
   }

   private boolean forNonSwappedIndexes(IndexMerger.IndexConsumer var1) {
      int var2 = this.lower.size();

      int var3;
      for(var3 = 0; var3 < var2; ++var3) {
         if (!var1.merge(var3, -1, var3)) {
            return false;
         }
      }

      var3 = this.upper.size() - 1;

      for(int var4 = 0; var4 < var3; ++var4) {
         if (!var1.merge(var2 - 1, var4, var2 + var4)) {
            return false;
         }
      }

      return true;
   }

   public double getDouble(int var1) {
      return var1 < this.lower.size() ? this.lower.get(var1) : this.upper.get(var1 - this.lower.size());
   }

   public List<Double> getList() {
      return this;
   }
}
