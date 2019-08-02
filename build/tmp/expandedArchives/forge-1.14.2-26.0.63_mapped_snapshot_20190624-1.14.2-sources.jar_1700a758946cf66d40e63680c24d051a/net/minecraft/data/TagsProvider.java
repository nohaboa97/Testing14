package net.minecraft.data;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class TagsProvider<T> implements IDataProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   protected final DataGenerator generator;
   protected final Registry<T> registry;
   protected final Map<Tag<T>, Tag.Builder<T>> tagToBuilder = Maps.newLinkedHashMap();

   protected TagsProvider(DataGenerator p_i49827_1_, Registry<T> p_i49827_2_) {
      this.generator = p_i49827_1_;
      this.registry = p_i49827_2_;
   }

   protected abstract void registerTags();

   /**
    * Performs this provider's action.
    */
   public void act(DirectoryCache cache) throws IOException {
      this.tagToBuilder.clear();
      this.registerTags();
      TagCollection<T> tagcollection = new TagCollection<>((p_200428_0_) -> {
         return Optional.empty();
      }, "", false, "generated");

      //This does not support resolving nested tags of ResourceLocation type entries.
      //So we must do this in multiple passes, and error if we have a pass that doesn't resolve anything.
      java.util.Set<Tag<T>> pending = new java.util.HashSet<>(this.tagToBuilder.keySet());
      java.util.Set<Tag<?>> processed = new java.util.HashSet<>();

      do {
         pending.removeAll(processed);
         processed.clear();
      for (Tag<T> key : pending) {
         Tag.Builder<T> value = this.tagToBuilder.get(key);
         ResourceLocation resourcelocation = key.getId();
         if (!value.resolve(tagcollection::get)) {
            continue;
         }

         Tag<T> tag = value.build(resourcelocation);
         JsonObject jsonobject = tag.serialize(this.registry::getKey);
         Path path = this.makePath(resourcelocation);
         tagcollection.register(tag);
         this.setCollection(tagcollection);
         processed.add(key);

         try {
            String s = GSON.toJson((JsonElement)jsonobject);
            String s1 = HASH_FUNCTION.hashUnencodedChars(s).toString();
            if (!Objects.equals(cache.getPreviousHash(path), s1) || !Files.exists(path)) {
               Files.createDirectories(path.getParent());

               try (BufferedWriter bufferedwriter = Files.newBufferedWriter(path)) {
                  bufferedwriter.write(s);
               }
            }

            cache.func_208316_a(path, s1);
         } catch (IOException ioexception) {
            LOGGER.error("Couldn't save tags to {}", path, ioexception);
         }
      }
      } while (!processed.isEmpty() && !pending.isEmpty());

      if (!pending.isEmpty()) {
         throw new UnsupportedOperationException("Failed to resolve tags: " + pending.stream().map(Tag::getId).map(Object::toString).sorted().collect(java.util.stream.Collectors.joining(", ")));
      }
   }

   protected abstract void setCollection(TagCollection<T> colectionIn);

   /**
    * Resolves a Path for the location to save the given tag.
    */
   protected abstract Path makePath(ResourceLocation id);

   /**
    * Creates (or finds) the builder for the given tag
    */
   protected Tag.Builder<T> getBuilder(Tag<T> tagIn) {
      return this.tagToBuilder.computeIfAbsent(tagIn, (p_200427_0_) -> {
         return Tag.Builder.create();
      });
   }
}