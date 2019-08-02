package net.minecraft.tags;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TagCollection<T> {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = new Gson();
   private static final int JSON_EXTENSION_LENGTH = ".json".length();
   private final Map<ResourceLocation, Tag<T>> tagMap = Maps.newHashMap();
   private final Function<ResourceLocation, Optional<T>> resourceLocationToItem;
   private final String resourceLocationPrefix;
   private final boolean preserveOrder;
   private final String itemTypeName;

   public TagCollection(Function<ResourceLocation, Optional<T>> p_i50686_1_, String p_i50686_2_, boolean p_i50686_3_, String p_i50686_4_) {
      this.resourceLocationToItem = p_i50686_1_;
      this.resourceLocationPrefix = p_i50686_2_;
      this.preserveOrder = p_i50686_3_;
      this.itemTypeName = p_i50686_4_;
   }

   public void register(Tag<T> tagIn) {
      if (this.tagMap.containsKey(tagIn.getId())) {
         throw new IllegalArgumentException("Duplicate " + this.itemTypeName + " tag '" + tagIn.getId() + "'");
      } else {
         this.tagMap.put(tagIn.getId(), tagIn);
      }
   }

   @Nullable
   public Tag<T> get(ResourceLocation resourceLocationIn) {
      return this.tagMap.get(resourceLocationIn);
   }

   public Tag<T> getOrCreate(ResourceLocation resourceLocationIn) {
      Tag<T> tag = this.tagMap.get(resourceLocationIn);
      return tag == null ? new Tag<>(resourceLocationIn) : tag;
   }

   public Collection<ResourceLocation> getRegisteredTags() {
      return this.tagMap.keySet();
   }

   public Collection<ResourceLocation> getOwningTags(T itemIn) {
      List<ResourceLocation> list = Lists.newArrayList();

      for(Entry<ResourceLocation, Tag<T>> entry : this.tagMap.entrySet()) {
         if (entry.getValue().contains(itemIn)) {
            list.add(entry.getKey());
         }
      }

      return list;
   }

   public void clear() {
      this.tagMap.clear();
   }

   public CompletableFuture<Map<ResourceLocation, Tag.Builder<T>>> reload(IResourceManager p_219781_1_, Executor p_219781_2_) {
      return CompletableFuture.supplyAsync(() -> {
         Map<ResourceLocation, Tag.Builder<T>> map = Maps.newHashMap();

         for(ResourceLocation resourcelocation : p_219781_1_.getAllResourceLocations(this.resourceLocationPrefix, (p_199916_0_) -> {
            return p_199916_0_.endsWith(".json");
         })) {
            String s = resourcelocation.getPath();
            ResourceLocation resourcelocation1 = new ResourceLocation(resourcelocation.getNamespace(), s.substring(this.resourceLocationPrefix.length() + 1, s.length() - JSON_EXTENSION_LENGTH));

            try {
               for(IResource iresource : p_219781_1_.getAllResources(resourcelocation)) {
                  try {
                     JsonObject jsonobject = JSONUtils.fromJson(GSON, IOUtils.toString(iresource.getInputStream(), StandardCharsets.UTF_8), JsonObject.class);
                     if (jsonobject == null) {
                        LOGGER.error("Couldn't load {} tag list {} from {} in data pack {} as it's empty or null", this.itemTypeName, resourcelocation1, resourcelocation, iresource.getPackName());
                     } else {
                        map.computeIfAbsent(resourcelocation1, (p_222990_1_) -> {
                           return Util.make(Tag.Builder.create(), (p_222989_1_) -> {
                              p_222989_1_.ordered(this.preserveOrder);
                           });
                        }).fromJson(this.resourceLocationToItem, jsonobject);
                     }
                  } catch (RuntimeException | IOException ioexception) {
                     LOGGER.error("Couldn't read {} tag list {} from {} in data pack {}", this.itemTypeName, resourcelocation1, resourcelocation, iresource.getPackName(), ioexception);
                  } finally {
                     IOUtils.closeQuietly((Closeable)iresource);
                  }
               }
            } catch (IOException ioexception1) {
               LOGGER.error("Couldn't read {} tag list {} from {}", this.itemTypeName, resourcelocation1, resourcelocation, ioexception1);
            }
         }

         return map;
      }, p_219781_2_);
   }

   public void registerAll(Map<ResourceLocation, Tag.Builder<T>> p_219779_1_) {
      while(true) {
         if (!p_219779_1_.isEmpty()) {
            boolean flag = false;
            Iterator<Entry<ResourceLocation, Tag.Builder<T>>> iterator = p_219779_1_.entrySet().iterator();

            while(iterator.hasNext()) {
               Entry<ResourceLocation, Tag.Builder<T>> entry = iterator.next();
               if (entry.getValue().resolve(this::get)) {
                  flag = true;
                  this.register(entry.getValue().build(entry.getKey()));
                  iterator.remove();
               }
            }

            if (flag) {
               continue;
            }

            for(Entry<ResourceLocation, Tag.Builder<T>> entry2 : p_219779_1_.entrySet()) {
               LOGGER.error("Couldn't load {} tag {} as it either references another tag that doesn't exist, or ultimately references itself", this.itemTypeName, entry2.getKey());
            }
         }

         for(Entry<ResourceLocation, Tag.Builder<T>> entry1 : p_219779_1_.entrySet()) {
            this.register(entry1.getValue().build(entry1.getKey()));
         }

         return;
      }
   }

   public Map<ResourceLocation, Tag<T>> getTagMap() {
      return this.tagMap;
   }
}