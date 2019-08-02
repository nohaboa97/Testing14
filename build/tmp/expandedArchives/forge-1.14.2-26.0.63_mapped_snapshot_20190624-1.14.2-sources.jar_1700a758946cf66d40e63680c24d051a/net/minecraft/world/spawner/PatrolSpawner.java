package net.minecraft.world.spawner;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.PatrollerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;

public class PatrolSpawner {
   private static final List<PatrolSpawner.WeightedEntry> field_222697_a = Arrays.asList(new PatrolSpawner.WeightedEntry(EntityType.PILLAGER, 80), new PatrolSpawner.WeightedEntry(EntityType.VINDICATOR, 20));
   private int field_222698_b;

   public int tick(ServerWorld worldIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs) {
      if (!spawnHostileMobs) {
         return 0;
      } else {
         Random random = worldIn.rand;
         --this.field_222698_b;
         if (this.field_222698_b > 0) {
            return 0;
         } else {
            this.field_222698_b += 6000 + random.nextInt(1200);
            long i = worldIn.getDayTime() / 24000L;
            if (i >= 5L && worldIn.isDaytime()) {
               if (random.nextInt(5) != 0) {
                  return 0;
               } else {
                  int j = worldIn.getPlayers().size();
                  if (j < 1) {
                     return 0;
                  } else {
                     PlayerEntity playerentity = worldIn.getPlayers().get(random.nextInt(j));
                     if (playerentity.isSpectator()) {
                        return 0;
                     } else if (worldIn.func_217483_b_(playerentity.getPosition())) {
                        return 0;
                     } else {
                        int k = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
                        int l = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
                        BlockPos blockpos = (new BlockPos(playerentity)).add(k, 0, l);
                        if (!worldIn.isAreaLoaded(blockpos.getX() - 10, blockpos.getY() - 10, blockpos.getZ() - 10, blockpos.getX() + 10, blockpos.getY() + 10, blockpos.getZ() + 10)) {
                           return 0;
                        } else {
                           Biome biome = worldIn.getBiome(blockpos);
                           Biome.Category biome$category = biome.getCategory();
                           if (biome$category != Biome.Category.PLAINS && biome$category != Biome.Category.TAIGA && biome$category != Biome.Category.DESERT && biome$category != Biome.Category.SAVANNA) {
                              return 0;
                           } else {
                              int i1 = 1;
                              this.func_222695_a(worldIn, blockpos, random, true);
                              int j1 = (int)Math.ceil((double)worldIn.getDifficultyForLocation(blockpos).getAdditionalDifficulty());

                              for(int k1 = 0; k1 < j1; ++k1) {
                                 ++i1;
                                 this.func_222695_a(worldIn, blockpos, random, false);
                              }

                              return i1;
                           }
                        }
                     }
                  }
               }
            } else {
               return 0;
            }
         }
      }
   }

   private void func_222695_a(World worldIn, BlockPos p_222695_2_, Random random, boolean p_222695_4_) {
      PatrolSpawner.WeightedEntry patrolspawner$weightedentry = WeightedRandom.getRandomItem(random, field_222697_a);
      PatrollerEntity patrollerentity = patrolspawner$weightedentry.entityType.create(worldIn);
      if (patrollerentity != null) {
         double d0 = (double)(p_222695_2_.getX() + random.nextInt(5) - random.nextInt(5));
         double d1 = (double)(p_222695_2_.getZ() + random.nextInt(5) - random.nextInt(5));
         BlockPos blockpos = patrollerentity.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(d0, (double)p_222695_2_.getY(), d1));
         if (p_222695_4_) {
            patrollerentity.setLeader(true);
            patrollerentity.resetPatrolTarget();
         }

         patrollerentity.setPosition((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ());
         patrollerentity.onInitialSpawn(worldIn, worldIn.getDifficultyForLocation(blockpos), SpawnReason.PATROL, (ILivingEntityData)null, (CompoundNBT)null);
         worldIn.addEntity(patrollerentity);
      }

   }

   public static class WeightedEntry extends WeightedRandom.Item {
      public final EntityType<? extends PatrollerEntity> entityType;

      public WeightedEntry(EntityType<? extends PatrollerEntity> entityTypeIn, int itemWeightIn) {
         super(itemWeightIn);
         this.entityType = entityTypeIn;
      }
   }
}