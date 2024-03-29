package net.minecraft.entity.monster;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class HuskEntity extends ZombieEntity {
   public HuskEntity(EntityType<? extends HuskEntity> p_i50204_1_, World p_i50204_2_) {
      super(p_i50204_1_, p_i50204_2_);
   }

   public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
      return super.canSpawn(worldIn, spawnReasonIn) && (spawnReasonIn == SpawnReason.SPAWNER || worldIn.func_217337_f(new BlockPos(this)));
   }

   protected boolean shouldBurnInDay() {
      return false;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_HUSK_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_HUSK_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_HUSK_DEATH;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.ENTITY_HUSK_STEP;
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      boolean flag = super.attackEntityAsMob(entityIn);
      if (flag && this.getHeldItemMainhand().isEmpty() && entityIn instanceof LivingEntity) {
         float f = this.world.getDifficultyForLocation(new BlockPos(this)).getAdditionalDifficulty();
         ((LivingEntity)entityIn).addPotionEffect(new EffectInstance(Effects.HUNGER, 140 * (int)f));
      }

      return flag;
   }

   protected boolean shouldDrown() {
      return true;
   }

   protected void onDrowned() {
      this.func_213698_b(EntityType.ZOMBIE);
      this.world.playEvent((PlayerEntity)null, 1041, new BlockPos(this), 0);
   }

   protected ItemStack getSkullDrop() {
      return ItemStack.EMPTY;
   }
}