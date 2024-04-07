package de.legoshi.parkourcalculator.util;

public interface BooleanOp {
   BooleanOp FALSE = (var0, var1) -> {
      return false;
   };
   BooleanOp NOT_OR = (var0, var1) -> {
      return !var0 && !var1;
   };
   BooleanOp ONLY_SECOND = (var0, var1) -> {
      return var1 && !var0;
   };
   BooleanOp NOT_FIRST = (var0, var1) -> {
      return !var0;
   };
   BooleanOp ONLY_FIRST = (var0, var1) -> {
      return var0 && !var1;
   };
   BooleanOp NOT_SECOND = (var0, var1) -> {
      return !var1;
   };
   BooleanOp NOT_SAME = (var0, var1) -> {
      return var0 != var1;
   };
   BooleanOp NOT_AND = (var0, var1) -> {
      return !var0 || !var1;
   };
   BooleanOp AND = (var0, var1) -> {
      return var0 && var1;
   };
   BooleanOp SAME = (var0, var1) -> {
      return var0 == var1;
   };
   BooleanOp SECOND = (var0, var1) -> {
      return var1;
   };
   BooleanOp CAUSES = (var0, var1) -> {
      return !var0 || var1;
   };
   BooleanOp FIRST = (var0, var1) -> {
      return var0;
   };
   BooleanOp CAUSED_BY = (var0, var1) -> {
      return var0 || !var1;
   };
   BooleanOp OR = (var0, var1) -> {
      return var0 || var1;
   };
   BooleanOp TRUE = (var0, var1) -> {
      return true;
   };

   boolean apply(boolean var1, boolean var2);
}
