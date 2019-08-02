package net.minecraft.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.world.gen.Heightmap;

public class EntitySpawnPlacementRegistry {
   private static final Map<EntityType<?>, EntitySpawnPlacementRegistry.Entry> REGISTRY = Maps.newHashMap();

   public static void register(EntityType<?> entityTypeIn, EntitySpawnPlacementRegistry.PlacementType placementType, Heightmap.Type heightMapType) {
      if (REGISTRY.containsKey(entityTypeIn)) throw new IllegalArgumentException("Invalid register call, " + entityTypeIn + " already registered.");
      REGISTRY.put(entityTypeIn, new EntitySpawnPlacementRegistry.Entry(heightMapType, placementType));
   }

   @Nullable
   public static EntitySpawnPlacementRegistry.PlacementType getPlacementType(EntityType<?> entityTypeIn) {
      EntitySpawnPlacementRegistry.Entry entityspawnplacementregistry$entry = REGISTRY.get(entityTypeIn);
      return entityspawnplacementregistry$entry == null ? null : entityspawnplacementregistry$entry.placementType;
   }

   public static Heightmap.Type func_209342_b(@Nullable EntityType<?> entityTypeIn) {
      EntitySpawnPlacementRegistry.Entry entityspawnplacementregistry$entry = REGISTRY.get(entityTypeIn);
      return entityspawnplacementregistry$entry == null ? Heightmap.Type.MOTION_BLOCKING_NO_LEAVES : entityspawnplacementregistry$entry.type;
   }

   static {
      register(EntityType.COD, EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.DOLPHIN, EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.DROWNED, EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.GUARDIAN, EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.PUFFERFISH, EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.SALMON, EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.SQUID, EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.TROPICAL_FISH, EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.BAT, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.BLAZE, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.CAVE_SPIDER, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.CHICKEN, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.COW, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.CREEPER, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.DONKEY, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.ENDERMAN, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.ENDERMITE, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.ENDER_DRAGON, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.GHAST, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.GIANT, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.HORSE, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.HUSK, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.IRON_GOLEM, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.LLAMA, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.MAGMA_CUBE, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.MOOSHROOM, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.MULE, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.OCELOT, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING);
      register(EntityType.PARROT, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING);
      register(EntityType.PIG, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.PILLAGER, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.POLAR_BEAR, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.RABBIT, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.SHEEP, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.SILVERFISH, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.SKELETON, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.SKELETON_HORSE, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.SLIME, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.SNOW_GOLEM, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.SPIDER, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.STRAY, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.TURTLE, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.VILLAGER, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.WITCH, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.WITHER, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.WITHER_SKELETON, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.WOLF, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.ZOMBIE, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.ZOMBIE_HORSE, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.ZOMBIE_PIGMAN, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      register(EntityType.ZOMBIE_VILLAGER, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
   }

   static class Entry {
      private final Heightmap.Type type;
      private final EntitySpawnPlacementRegistry.PlacementType placementType;

      public Entry(Heightmap.Type p_i50583_1_, EntitySpawnPlacementRegistry.PlacementType placementTypeIn) {
         this.type = p_i50583_1_;
         this.placementType = placementTypeIn;
      }
   }

   public static enum PlacementType implements net.minecraftforge.common.IExtensibleEnum {
      ON_GROUND,
      IN_WATER;

      public static PlacementType create(String name, net.minecraftforge.common.util.TriPredicate<net.minecraft.world.IWorldReader, net.minecraft.util.math.BlockPos, EntityType<? extends MobEntity>> predicate) {
         throw new IllegalStateException("Enum not extended");
      }

      private net.minecraftforge.common.util.TriPredicate<net.minecraft.world.IWorldReader, net.minecraft.util.math.BlockPos, EntityType<?>> predicate;
      private PlacementType() { this(null); }
      private PlacementType(net.minecraftforge.common.util.TriPredicate<net.minecraft.world.IWorldReader, net.minecraft.util.math.BlockPos, EntityType<?>> predicate) {
         this.predicate = predicate;
      }

      public boolean canSpawnAt(net.minecraft.world.IWorldReader world, net.minecraft.util.math.BlockPos pos, EntityType<?> type) {
         if (predicate == null) return net.minecraft.world.spawner.WorldEntitySpawner.canSpawnAtBody(this, world, pos, type);
         return predicate.test(world, pos, type);
      }
   }
}