package net.minecraft.entity.ai.brain.memory;

import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.IPosWrapper;
import net.minecraft.util.registry.Registry;

public class MemoryModuleType<U> extends net.minecraftforge.registries.ForgeRegistryEntry<MemoryModuleType<?>> {
   public static final MemoryModuleType<Void> DUMMY = register("dummy", Optional.empty());
   public static final MemoryModuleType<GlobalPos> HOME = register("home", Optional.of(GlobalPos::deserialize));
   public static final MemoryModuleType<GlobalPos> JOB_SITE = register("job_site", Optional.of(GlobalPos::deserialize));
   public static final MemoryModuleType<GlobalPos> MEETING_POINT = register("meeting_point", Optional.of(GlobalPos::deserialize));
   public static final MemoryModuleType<List<GlobalPos>> SECONDARY_JOB_SITE = register("secondary_job_site", Optional.empty());
   public static final MemoryModuleType<List<LivingEntity>> MOBS = register("mobs", Optional.empty());
   public static final MemoryModuleType<List<LivingEntity>> VISIBLE_MOBS = register("visible_mobs", Optional.empty());
   public static final MemoryModuleType<List<LivingEntity>> VISIBLE_VILLAGER_BABIES = register("visible_villager_babies", Optional.empty());
   public static final MemoryModuleType<List<PlayerEntity>> NEAREST_PLAYERS = register("nearest_players", Optional.empty());
   public static final MemoryModuleType<PlayerEntity> NEAREST_VISIBLE_PLAYER = register("nearest_visible_player", Optional.empty());
   public static final MemoryModuleType<WalkTarget> WALK_TARGET = register("walk_target", Optional.empty());
   public static final MemoryModuleType<IPosWrapper> LOOK_TARGET = register("look_target", Optional.empty());
   public static final MemoryModuleType<LivingEntity> INTERACTION_TARGET = register("interaction_target", Optional.empty());
   public static final MemoryModuleType<VillagerEntity> BREED_TARGET = register("breed_target", Optional.empty());
   public static final MemoryModuleType<Path> PATH = register("path", Optional.empty());
   public static final MemoryModuleType<List<GlobalPos>> INTERACTABLE_DOORS = register("interactable_doors", Optional.empty());
   public static final MemoryModuleType<BlockPos> NEAREST_BED = register("nearest_bed", Optional.empty());
   public static final MemoryModuleType<DamageSource> HURT_BY = register("hurt_by", Optional.empty());
   public static final MemoryModuleType<LivingEntity> HURT_BY_ENTITY = register("hurt_by_entity", Optional.empty());
   public static final MemoryModuleType<LivingEntity> NEAREST_HOSTILE = register("nearest_hostile", Optional.empty());
   public static final MemoryModuleType<VillagerEntity.GolemStatus> GOLEM_SPAWN_CONDITIONS = register("golem_spawn_conditions", Optional.empty());
   public static final MemoryModuleType<GlobalPos> HIDING_PLACE = register("hiding_place", Optional.empty());
   public static final MemoryModuleType<Long> HEARD_BELL_TIME = register("heard_bell_time", Optional.empty());
   public static final MemoryModuleType<Long> CANT_REACH_WALK_TARGET_SINCE = register("cant_reach_walk_target_since", Optional.empty());
   private final Optional<Function<Dynamic<?>, U>> deserializer;

   public MemoryModuleType(Optional<Function<Dynamic<?>, U>> p_i50306_1_) {
      this.deserializer = p_i50306_1_;
   }

   public String toString() {
      return Registry.MEMORY_MODULE_TYPE.getKey(this).toString();
   }

   public Optional<Function<Dynamic<?>, U>> getDeserializer() {
      return this.deserializer;
   }

   private static <U> MemoryModuleType<U> register(String key, Optional<Function<Dynamic<?>, U>> p_220937_1_) {
      return Registry.register(Registry.MEMORY_MODULE_TYPE, new ResourceLocation(key), new MemoryModuleType<>(p_220937_1_));
   }
}