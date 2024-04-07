package de.legoshi.parkourcalculator.simulation.environment.voxel;

import java.util.ArrayList;

public class CubePointRange extends ArrayList<Double> {
   private final int parts;

   public CubePointRange(int var1) {
      if (var1 <= 0) {
         throw new IllegalArgumentException("Need at least 1 part");
      } else {
         this.parts = var1;
      }
   }

   public double getDouble(int var1) {
      return (double)var1 / (double)this.parts;
   }

   public int size() {
      return this.parts + 1;
   }
}