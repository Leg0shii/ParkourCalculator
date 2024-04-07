package de.legoshi.parkourcalculator.simulation.environment.merger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IndirectMerger implements IndexMerger {
   private static final List<Double> EMPTY = new ArrayList<>();
   private final double[] result;
   private final int[] firstIndices;
   private final int[] secondIndices;
   private final int resultLength;

   static {
      EMPTY.add(0.0D);
   }

   public IndirectMerger(List<Double> var1, List<Double> var2, boolean var3, boolean var4) {
      double var5 = Double.NaN;
      int var7 = var1.size();
      int var8 = var2.size();
      int var9 = var7 + var8;
      this.result = new double[var9];
      this.firstIndices = new int[var9];
      this.secondIndices = new int[var9];
      boolean var10 = !var3;
      boolean var11 = !var4;
      int var12 = 0;
      int var13 = 0;
      int var14 = 0;

      while(true) {
         boolean var17;
         while(true) {
            boolean var15 = var13 >= var7;
            boolean var16 = var14 >= var8;
            if (var15 && var16) {
               this.resultLength = Math.max(1, var12);
               return;
            }

            var17 = !var15 && (var16 || var1.get(var13) < var2.get(var14) + 1.0E-7D);
            if (var17) {
               ++var13;
               if (!var10 || var14 != 0 && !var16) {
                  break;
               }
            } else {
               ++var14;
               if (!var11 || var13 != 0 && !var15) {
                  break;
               }
            }
         }

         int var18 = var13 - 1;
         int var19 = var14 - 1;
         double var20 = var17 ? var1.get(var18) : var2.get(var19);
         if (!(var5 >= var20 - 1.0E-7D)) {
            this.firstIndices[var12] = var18;
            this.secondIndices[var12] = var19;
            this.result[var12] = var20;
            ++var12;
            var5 = var20;
         } else {
            this.firstIndices[var12 - 1] = var18;
            this.secondIndices[var12 - 1] = var19;
         }
      }
   }

   public boolean forMergedIndexes(IndexMerger.IndexConsumer var1) {
      int var2 = this.resultLength - 1;

      for(int var3 = 0; var3 < var2; ++var3) {
         if (!var1.merge(this.firstIndices[var3], this.secondIndices[var3], var3)) {
            return false;
         }
      }

      return true;
   }

   public int size() {
      return this.resultLength;
   }

   public List<Double> getList() {
      if (this.resultLength <= 1) {
         return Collections.emptyList();
      } else {
         List<Double> list = new ArrayList<>(this.resultLength);
         for (int i = 0; i < this.resultLength; i++) {
            list.add(this.result[i]);
         }
         return list;
      }
   }
}
