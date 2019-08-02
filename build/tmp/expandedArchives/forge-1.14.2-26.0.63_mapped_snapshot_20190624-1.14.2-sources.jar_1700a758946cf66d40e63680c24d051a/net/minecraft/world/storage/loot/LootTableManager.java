package net.minecraft.world.storage.loot;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.ILootFunction;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootTableManager implements IResourceManagerReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON_INSTANCE = (new GsonBuilder()).registerTypeAdapter(RandomValueRange.class, new RandomValueRange.Serializer()).registerTypeAdapter(BinomialRange.class, new BinomialRange.Serializer()).registerTypeAdapter(ConstantRange.class, new ConstantRange.Serializer()).registerTypeAdapter(IntClamper.class, new IntClamper.Serializer()).registerTypeAdapter(LootPool.class, new LootPool.Serializer()).registerTypeAdapter(LootTable.class, new LootTable.Serializer()).registerTypeHierarchyAdapter(LootEntry.class, new LootEntryManager.Serializer()).registerTypeHierarchyAdapter(ILootFunction.class, new LootFunctionManager.Serializer()).registerTypeHierarchyAdapter(ILootCondition.class, new LootConditionManager.Serializer()).registerTypeHierarchyAdapter(LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer()).create();
   private final Map<ResourceLocation, LootTable> registeredLootTables = Maps.newHashMap();
   private final Set<ResourceLocation> field_215306_f = Collections.unmodifiableSet(this.registeredLootTables.keySet());
   public static final int field_195435_a = "loot_tables/".length();
   public static final int field_195436_b = ".json".length();

   public LootTable getLootTableFromLocation(ResourceLocation ressources) {
      return this.registeredLootTables.getOrDefault(ressources, LootTable.EMPTY_LOOT_TABLE);
   }

   public void onResourceManagerReload(IResourceManager resourceManager) {
      this.registeredLootTables.clear();

      for(ResourceLocation resourcelocation : resourceManager.getAllResourceLocations("loot_tables", (p_195434_0_) -> {
         return p_195434_0_.endsWith(".json");
      })) {
         String s = resourcelocation.getPath();
         ResourceLocation resourcelocation1 = new ResourceLocation(resourcelocation.getNamespace(), s.substring(field_195435_a, s.length() - field_195436_b));

         try (IResource iresource = resourceManager.getResource(resourcelocation)) {
            LootTable loottable = net.minecraftforge.common.ForgeHooks.loadLootTable(GSON_INSTANCE, resourcelocation, IOUtils.toString(iresource.getInputStream(), StandardCharsets.UTF_8), iresource.getPackName().equals("Default"), this);
            if (loottable != null) {
               this.registeredLootTables.put(resourcelocation1, loottable);
            }
         } catch (Throwable throwable) {
            LOGGER.error("Couldn't read loot table {} from {}", resourcelocation1, resourcelocation, throwable);
         }
      }

      this.registeredLootTables.put(LootTables.EMPTY, LootTable.EMPTY_LOOT_TABLE);
      ValidationResults validationresults = new ValidationResults();
      this.registeredLootTables.forEach((p_215305_2_, p_215305_3_) -> {
         func_215302_a(validationresults, p_215305_2_, p_215305_3_, this.registeredLootTables::get);
      });
      validationresults.getProblems().forEach((p_215303_0_, p_215303_1_) -> {
         LOGGER.warn("Found validation problem in " + p_215303_0_ + ": " + p_215303_1_);
      });
   }

   public static void func_215302_a(ValidationResults p_215302_0_, ResourceLocation p_215302_1_, LootTable p_215302_2_, Function<ResourceLocation, LootTable> p_215302_3_) {
      Set<ResourceLocation> set = ImmutableSet.of(p_215302_1_);
      p_215302_2_.func_216117_a(p_215302_0_.descend("{" + p_215302_1_.toString() + "}"), p_215302_3_, set, p_215302_2_.func_216122_a());
   }

   public static JsonElement func_215301_a(LootTable p_215301_0_) {
      return GSON_INSTANCE.toJsonTree(p_215301_0_);
   }

   public Set<ResourceLocation> func_215304_a() {
      return this.field_215306_f;
   }
}