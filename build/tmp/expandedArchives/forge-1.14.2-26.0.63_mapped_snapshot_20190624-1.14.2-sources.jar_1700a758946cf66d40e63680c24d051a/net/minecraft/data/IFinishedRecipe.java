package net.minecraft.data;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public interface IFinishedRecipe {
   void func_218610_a(JsonObject p_218610_1_);

   /**
    * Gets the JSON for the recipe.
    */
   default JsonObject getRecipeJson() {
      JsonObject jsonobject = new JsonObject();
      jsonobject.addProperty("type", Registry.RECIPE_SERIALIZER.getKey(this.func_218609_c()).toString());
      this.func_218610_a(jsonobject);
      return jsonobject;
   }

   /**
    * Gets the ID for the recipe.
    */
   ResourceLocation getID();

   IRecipeSerializer<?> func_218609_c();

   /**
    * Gets the JSON for the advancement that unlocks this recipe. Null if there is no advancement.
    */
   @Nullable
   JsonObject getAdvancementJson();

   /**
    * Gets the ID for the advancement associated with this recipe. Should not be null if {@link #getAdvancementJson} is
    * non-null.
    */
   @Nullable
   ResourceLocation getAdvancementID();
}