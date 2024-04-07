package de.legoshi.parkourcalculator.simulation.environment.merger;

import java.util.List;

public interface IndexMerger {

   List<Double> getList();

   boolean forMergedIndexes(IndexMerger.IndexConsumer var1);

   int size();

   public interface IndexConsumer {
      boolean merge(int var1, int var2, int var3);
   }
}