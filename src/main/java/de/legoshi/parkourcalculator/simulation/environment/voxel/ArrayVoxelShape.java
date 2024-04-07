package de.legoshi.parkourcalculator.simulation.environment.voxel;


import de.legoshi.parkourcalculator.simulation.Direction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ArrayVoxelShape extends VoxelShape {

   private final List<Double> xs;
   private final List<Double> ys;
   private final List<Double> zs;

   protected ArrayVoxelShape(DiscreteVoxelShape var1, double[] var2, double[] var3, double[] var4) {
      this(var1,
              Arrays.stream(Arrays.copyOf(var2, var1.getXSize() + 1)).boxed().collect(Collectors.toList()),
              Arrays.stream(Arrays.copyOf(var3, var1.getYSize() + 1)).boxed().collect(Collectors.toList()),
              Arrays.stream(Arrays.copyOf(var4, var1.getZSize() + 1)).boxed().collect(Collectors.toList())
      );
   }

   ArrayVoxelShape(DiscreteVoxelShape var1, List<Double> var2, List<Double> var3, List<Double> var4) {
      super(var1);
      int var5 = var1.getXSize() + 1;
      int var6 = var1.getYSize() + 1;
      int var7 = var1.getZSize() + 1;
      if (var5 == var2.size() && var6 == var3.size() && var7 == var4.size()) {
         this.xs = var2;
         this.ys = var3;
         this.zs = var4;
      } else {
         throw new IllegalArgumentException();
      }
   }

   protected List<Double> getCoords(Direction.Axis var1) {
      return switch (var1) {
         case X -> this.xs;
         case Y -> this.ys;
         case Z -> this.zs;
      };
   }
}
