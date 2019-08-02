package net.minecraft.profiler;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class DataPoint implements Comparable<DataPoint> {
   public final double relTime;
   public final double rootRelTime;
   public final String name;

   public DataPoint(String p_i50404_1_, double p_i50404_2_, double p_i50404_4_) {
      this.name = p_i50404_1_;
      this.relTime = p_i50404_2_;
      this.rootRelTime = p_i50404_4_;
   }

   public int compareTo(DataPoint p_compareTo_1_) {
      if (p_compareTo_1_.relTime < this.relTime) {
         return -1;
      } else {
         return p_compareTo_1_.relTime > this.relTime ? 1 : p_compareTo_1_.name.compareTo(this.name);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public int getTextColor() {
      return (this.name.hashCode() & 11184810) + 4473924;
   }
}