package net.minecraft.client.renderer;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DestroyBlockProgress {
   private final int miningPlayerEntId;
   private final BlockPos position;
   private int partialBlockProgress;
   private int createdAtCloudUpdateTick;

   public DestroyBlockProgress(int miningPlayerEntIdIn, BlockPos positionIn) {
      this.miningPlayerEntId = miningPlayerEntIdIn;
      this.position = positionIn;
   }

   public BlockPos getPosition() {
      return this.position;
   }

   /**
    * inserts damage value into this partially destroyed Block. -1 causes client renderer to delete it, otherwise ranges
    * from 1 to 10
    */
   public void setPartialBlockDamage(int damage) {
      if (damage > 10) {
         damage = 10;
      }

      this.partialBlockProgress = damage;
   }

   public int getPartialBlockDamage() {
      return this.partialBlockProgress;
   }

   /**
    * saves the current Cloud update tick into the PartiallyDestroyedBlock
    */
   public void setCloudUpdateTick(int createdAtCloudUpdateTickIn) {
      this.createdAtCloudUpdateTick = createdAtCloudUpdateTickIn;
   }

   /**
    * retrieves the 'date' at which the PartiallyDestroyedBlock was created
    */
   public int getCreationCloudUpdateTick() {
      return this.createdAtCloudUpdateTick;
   }
}