package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.play.server.SRecipeBookPacket;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerRecipeBook extends RecipeBook {
   private static final Logger LOGGER = LogManager.getLogger();
   private final RecipeManager recipeManager;

   public ServerRecipeBook(RecipeManager p_i48175_1_) {
      this.recipeManager = p_i48175_1_;
   }

   public int add(Collection<IRecipe<?>> p_197926_1_, ServerPlayerEntity p_197926_2_) {
      List<ResourceLocation> list = Lists.newArrayList();
      int i = 0;

      for(IRecipe<?> irecipe : p_197926_1_) {
         ResourceLocation resourcelocation = irecipe.getId();
         if (!this.recipes.contains(resourcelocation) && !irecipe.isDynamic()) {
            this.unlock(resourcelocation);
            this.markNew(resourcelocation);
            list.add(resourcelocation);
            CriteriaTriggers.RECIPE_UNLOCKED.trigger(p_197926_2_, irecipe);
            ++i;
         }
      }

      this.sendPacket(SRecipeBookPacket.State.ADD, p_197926_2_, list);
      return i;
   }

   public int remove(Collection<IRecipe<?>> p_197925_1_, ServerPlayerEntity p_197925_2_) {
      List<ResourceLocation> list = Lists.newArrayList();
      int i = 0;

      for(IRecipe<?> irecipe : p_197925_1_) {
         ResourceLocation resourcelocation = irecipe.getId();
         if (this.recipes.contains(resourcelocation)) {
            this.lock(resourcelocation);
            list.add(resourcelocation);
            ++i;
         }
      }

      this.sendPacket(SRecipeBookPacket.State.REMOVE, p_197925_2_, list);
      return i;
   }

   private void sendPacket(SRecipeBookPacket.State state, ServerPlayerEntity player, List<ResourceLocation> recipesIn) {
      player.connection.sendPacket(new SRecipeBookPacket(state, recipesIn, Collections.emptyList(), this.isGuiOpen, this.isFilteringCraftable, this.isFurnaceGuiOpen, this.isFurnaceFilteringCraftable));
   }

   public CompoundNBT write() {
      CompoundNBT compoundnbt = new CompoundNBT();
      compoundnbt.putBoolean("isGuiOpen", this.isGuiOpen);
      compoundnbt.putBoolean("isFilteringCraftable", this.isFilteringCraftable);
      compoundnbt.putBoolean("isFurnaceGuiOpen", this.isFurnaceGuiOpen);
      compoundnbt.putBoolean("isFurnaceFilteringCraftable", this.isFurnaceFilteringCraftable);
      ListNBT listnbt = new ListNBT();

      for(ResourceLocation resourcelocation : this.recipes) {
         listnbt.add(new StringNBT(resourcelocation.toString()));
      }

      compoundnbt.put("recipes", listnbt);
      ListNBT listnbt1 = new ListNBT();

      for(ResourceLocation resourcelocation1 : this.newRecipes) {
         listnbt1.add(new StringNBT(resourcelocation1.toString()));
      }

      compoundnbt.put("toBeDisplayed", listnbt1);
      return compoundnbt;
   }

   public void read(CompoundNBT tag) {
      this.isGuiOpen = tag.getBoolean("isGuiOpen");
      this.isFilteringCraftable = tag.getBoolean("isFilteringCraftable");
      this.isFurnaceGuiOpen = tag.getBoolean("isFurnaceGuiOpen");
      this.isFurnaceFilteringCraftable = tag.getBoolean("isFurnaceFilteringCraftable");
      ListNBT listnbt = tag.getList("recipes", 8);

      for(int i = 0; i < listnbt.size(); ++i) {
         ResourceLocation resourcelocation = new ResourceLocation(listnbt.getString(i));
         Optional<? extends IRecipe<?>> optional = this.recipeManager.getRecipe(resourcelocation);
         if (!optional.isPresent()) {
            LOGGER.error("Tried to load unrecognized recipe: {} removed now.", (Object)resourcelocation);
         } else {
            this.unlock(optional.get());
         }
      }

      ListNBT listnbt1 = tag.getList("toBeDisplayed", 8);

      for(int j = 0; j < listnbt1.size(); ++j) {
         ResourceLocation resourcelocation1 = new ResourceLocation(listnbt1.getString(j));
         Optional<? extends IRecipe<?>> optional1 = this.recipeManager.getRecipe(resourcelocation1);
         if (!optional1.isPresent()) {
            LOGGER.error("Tried to load unrecognized recipe: {} removed now.", (Object)resourcelocation1);
         } else {
            this.markNew(optional1.get());
         }
      }

   }

   public void init(ServerPlayerEntity player) {
      player.connection.sendPacket(new SRecipeBookPacket(SRecipeBookPacket.State.INIT, this.recipes, this.newRecipes, this.isGuiOpen, this.isFilteringCraftable, this.isFurnaceGuiOpen, this.isFurnaceFilteringCraftable));
   }
}