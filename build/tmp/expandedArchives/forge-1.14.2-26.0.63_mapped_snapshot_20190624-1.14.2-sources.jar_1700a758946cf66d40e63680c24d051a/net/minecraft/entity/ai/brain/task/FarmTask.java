package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropsBlock;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPosWrapper;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.ServerWorld;

public class FarmTask extends Task<VillagerEntity> {
   @Nullable
   private BlockPos field_220422_a;
   private boolean field_220423_b;
   private boolean field_220424_c;
   private long field_220425_d;
   private int field_220426_e;

   public FarmTask() {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.SECONDARY_JOB_SITE, MemoryModuleStatus.VALUE_PRESENT));
   }

   protected boolean shouldExecute(ServerWorld worldIn, VillagerEntity owner) {
      if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(worldIn, owner)) {
         return false;
      } else if (owner.getVillagerData().getProfession() != VillagerProfession.FARMER) {
         return false;
      } else {
         Set<BlockPos> set = owner.getBrain().getMemory(MemoryModuleType.SECONDARY_JOB_SITE).get().stream().map(GlobalPos::getPos).collect(Collectors.toSet());
         BlockPos blockpos = new BlockPos(owner);
         List<BlockPos> list = ImmutableList.of(blockpos.down(), blockpos.south(), blockpos.north(), blockpos.east(), blockpos.west()).stream().filter(set::contains).collect(Collectors.toList());
         this.field_220423_b = owner.isFarmItemInInventory();
         this.field_220424_c = owner.wantsMoreFood();
         List<BlockPos> list1 = list.stream().map(BlockPos::up).filter((p_220420_2_) -> {
            return this.func_220421_a(worldIn.getBlockState(p_220420_2_));
         }).collect(Collectors.toList());
         if (!list1.isEmpty()) {
            this.field_220422_a = list1.get(worldIn.getRandom().nextInt(list1.size()));
            return true;
         } else {
            return false;
         }
      }
   }

   private boolean func_220421_a(BlockState p_220421_1_) {
      Block block = p_220421_1_.getBlock();
      return block instanceof CropsBlock && ((CropsBlock)block).isMaxAge(p_220421_1_) && this.field_220424_c || p_220421_1_.isAir() && this.field_220423_b;
   }

   protected void startExecuting(ServerWorld p_212831_1_, VillagerEntity p_212831_2_, long p_212831_3_) {
      if (p_212831_3_ > this.field_220425_d && this.field_220422_a != null) {
         p_212831_2_.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosWrapper(this.field_220422_a));
         p_212831_2_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosWrapper(this.field_220422_a), 0.5F, 1));
      }

   }

   protected void resetTask(ServerWorld p_212835_1_, VillagerEntity p_212835_2_, long p_212835_3_) {
      p_212835_2_.getBrain().removeMemory(MemoryModuleType.LOOK_TARGET);
      p_212835_2_.getBrain().removeMemory(MemoryModuleType.WALK_TARGET);
      this.field_220426_e = 0;
      this.field_220425_d = p_212835_3_ + 40L;
   }

   protected void updateTask(ServerWorld worldIn, VillagerEntity owner, long gameTime) {
      if (this.field_220426_e > 15 && this.field_220422_a != null && gameTime > this.field_220425_d) {
         BlockState blockstate = worldIn.getBlockState(this.field_220422_a);
         Block block = blockstate.getBlock();
         if (block instanceof CropsBlock && ((CropsBlock)block).isMaxAge(blockstate) && this.field_220424_c) {
            worldIn.destroyBlock(this.field_220422_a, true);
         } else if (blockstate.isAir() && this.field_220423_b) {
            Inventory inventory = owner.func_213715_ed();

            for(int i = 0; i < inventory.getSizeInventory(); ++i) {
               ItemStack itemstack = inventory.getStackInSlot(i);
               boolean flag = false;
               if (!itemstack.isEmpty()) {
                  if (itemstack.getItem() == Items.WHEAT_SEEDS) {
                     worldIn.setBlockState(this.field_220422_a, Blocks.WHEAT.getDefaultState(), 3);
                     flag = true;
                  } else if (itemstack.getItem() == Items.POTATO) {
                     worldIn.setBlockState(this.field_220422_a, Blocks.POTATOES.getDefaultState(), 3);
                     flag = true;
                  } else if (itemstack.getItem() == Items.CARROT) {
                     worldIn.setBlockState(this.field_220422_a, Blocks.CARROTS.getDefaultState(), 3);
                     flag = true;
                  } else if (itemstack.getItem() == Items.BEETROOT_SEEDS) {
                     worldIn.setBlockState(this.field_220422_a, Blocks.BEETROOTS.getDefaultState(), 3);
                     flag = true;
                  } else if (itemstack.getItem() instanceof net.minecraftforge.common.IPlantable) {
                     if (((net.minecraftforge.common.IPlantable)itemstack.getItem()).getPlantType(worldIn, field_220422_a) == net.minecraftforge.common.PlantType.Crop) {
                        worldIn.setBlockState(field_220422_a, ((net.minecraftforge.common.IPlantable)itemstack.getItem()).getPlant(worldIn, field_220422_a),3);
                        flag = true;
                     }
                  }
               }

               if (flag) {
                  worldIn.playSound((PlayerEntity)null, (double)this.field_220422_a.getX(), (double)this.field_220422_a.getY(), (double)this.field_220422_a.getZ(), SoundEvents.ITEM_CROP_PLANT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                  itemstack.shrink(1);
                  if (itemstack.isEmpty()) {
                     inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                  }
                  break;
               }
            }
         }
      }

      ++this.field_220426_e;
   }

   protected boolean shouldContinueExecuting(ServerWorld p_212834_1_, VillagerEntity p_212834_2_, long p_212834_3_) {
      return this.field_220426_e < 30;
   }
}