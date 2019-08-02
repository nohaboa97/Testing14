package net.minecraft.item.crafting;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecipeManager implements IResourceManagerReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final int PATH_PREFIX_LENGTH = "recipes/".length();
   public static final int PATH_SUFFIX_LENGTH = ".json".length();
   private final Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> recipes = Util.make(Maps.newHashMap(), RecipeManager::func_215374_a);
   private boolean someRecipesErrored;

   public void onResourceManagerReload(IResourceManager resourceManager) {
      Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
      this.someRecipesErrored = false;
      func_215374_a(this.recipes);

      for(ResourceLocation resourcelocation : resourceManager.getAllResourceLocations("recipes", (p_199516_0_) -> {
         return p_199516_0_.endsWith(".json") && !p_199516_0_.startsWith("_"); //Forge filter anything beginning with "_" as it's used for metadata.
      })) {
         String s = resourcelocation.getPath();
         ResourceLocation resourcelocation1 = new ResourceLocation(resourcelocation.getNamespace(), s.substring(PATH_PREFIX_LENGTH, s.length() - PATH_SUFFIX_LENGTH));

         try (IResource iresource = resourceManager.getResource(resourcelocation)) {
            JsonObject jsonobject = JSONUtils.fromJson(gson, IOUtils.toString(iresource.getInputStream(), StandardCharsets.UTF_8), JsonObject.class);
            if (jsonobject == null) {
               LOGGER.error("Couldn't load recipe {} as it's null or empty", (Object)resourcelocation1);
            } else if (jsonobject.has("conditions") && !net.minecraftforge.common.crafting.CraftingHelper.processConditions(JSONUtils.getJsonArray(jsonobject, "conditions"))) {
               LOGGER.info("Skipping loading recipe {} as it's conditions were not met", resourcelocation1);
            } else {
               this.addRecipe(func_215377_a(resourcelocation1, jsonobject));
            }
         } catch (IllegalArgumentException | JsonParseException jsonparseexception) {
            LOGGER.error("Parsing error loading recipe {}", resourcelocation1, jsonparseexception);
            this.someRecipesErrored = true;
         } catch (IOException ioexception) {
            LOGGER.error("Couldn't read custom advancement {} from {}", resourcelocation1, resourcelocation, ioexception);
            this.someRecipesErrored = true;
         }
      }

      LOGGER.info("Loaded {} recipes", (int)this.recipes.size());
   }

   public void addRecipe(IRecipe<?> recipe) {
      Map<ResourceLocation, IRecipe<?>> map = this.recipes.get(recipe.getType());
      if (map.containsKey(recipe.getId())) {
         throw new IllegalStateException("Duplicate recipe ignored with ID " + recipe.getId());
      } else {
         map.put(recipe.getId(), recipe);
      }
   }

   public <C extends IInventory, T extends IRecipe<C>> Optional<T> getRecipe(IRecipeType<T> p_215371_1_, C p_215371_2_, World p_215371_3_) {
      return this.getRecipes(p_215371_1_).values().stream().flatMap((p_215372_3_) -> {
         return Util.streamOptional(p_215371_1_.matches(p_215372_3_, p_215371_3_, p_215371_2_));
      }).findFirst();
   }

   public <C extends IInventory, T extends IRecipe<C>> List<T> getRecipes(IRecipeType<T> p_215370_1_, C p_215370_2_, World p_215370_3_) {
      return this.getRecipes(p_215370_1_).values().stream().flatMap((p_215380_3_) -> {
         return Util.streamOptional(p_215370_1_.matches(p_215380_3_, p_215370_3_, p_215370_2_));
      }).sorted(Comparator.comparing((p_215379_0_) -> {
         return p_215379_0_.getRecipeOutput().getTranslationKey();
      })).collect(Collectors.toList());
   }

   private <C extends IInventory, T extends IRecipe<C>> Map<ResourceLocation, IRecipe<C>> getRecipes(IRecipeType<T> p_215366_1_) {
      return (Map)this.recipes.getOrDefault(p_215366_1_, Maps.newHashMap());
   }

   public <C extends IInventory, T extends IRecipe<C>> NonNullList<ItemStack> func_215369_c(IRecipeType<T> p_215369_1_, C p_215369_2_, World p_215369_3_) {
      Optional<T> optional = this.getRecipe(p_215369_1_, p_215369_2_, p_215369_3_);
      if (optional.isPresent()) {
         return ((IRecipe)optional.get()).getRemainingItems(p_215369_2_);
      } else {
         NonNullList<ItemStack> nonnulllist = NonNullList.withSize(p_215369_2_.getSizeInventory(), ItemStack.EMPTY);

         for(int i = 0; i < nonnulllist.size(); ++i) {
            nonnulllist.set(i, p_215369_2_.getStackInSlot(i));
         }

         return nonnulllist;
      }
   }

   public Optional<? extends IRecipe<?>> getRecipe(ResourceLocation p_215367_1_) {
      return this.recipes.values().stream().map((p_215368_1_) -> {
         return p_215368_1_.get(p_215367_1_);
      }).filter(Objects::nonNull).findFirst();
   }

   public Collection<IRecipe<?>> getRecipes() {
      return this.recipes.values().stream().flatMap((p_215376_0_) -> {
         return p_215376_0_.values().stream();
      }).collect(Collectors.toSet());
   }

   public Stream<ResourceLocation> func_215378_c() {
      return this.recipes.values().stream().flatMap((p_215375_0_) -> {
         return p_215375_0_.keySet().stream();
      });
   }

   @OnlyIn(Dist.CLIENT)
   public void clear() {
      func_215374_a(this.recipes);
   }

   public static IRecipe<?> func_215377_a(ResourceLocation p_215377_0_, JsonObject p_215377_1_) {
      String s = JSONUtils.getString(p_215377_1_, "type");
      return Registry.RECIPE_SERIALIZER.getValue(new ResourceLocation(s)).orElseThrow(() -> {
         return new JsonSyntaxException("Invalid or unsupported recipe type '" + s + "'");
      }).read(p_215377_0_, p_215377_1_);
   }

   private static void func_215374_a(Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> p_215374_0_) {
      p_215374_0_.clear();

      for(IRecipeType<?> irecipetype : Registry.RECIPE_TYPE) {
         p_215374_0_.put(irecipetype, Maps.newHashMap());
      }

   }
}