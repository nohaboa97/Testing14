package net.minecraft.client.audio;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.GameSettings;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class SoundHandler extends ReloadListener<SoundHandler.Loader> {
   public static final Sound MISSING_SOUND = new Sound("meta:missing_sound", 1.0F, 1.0F, 1, Sound.Type.FILE, false, false, 16);
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).registerTypeHierarchyAdapter(ITextComponent.class, new ITextComponent.Serializer()).registerTypeAdapter(SoundList.class, new SoundListSerializer()).create();
   private static final ParameterizedType TYPE = new ParameterizedType() {
      public Type[] getActualTypeArguments() {
         return new Type[]{String.class, SoundList.class};
      }

      public Type getRawType() {
         return Map.class;
      }

      public Type getOwnerType() {
         return null;
      }
   };
   private final Map<ResourceLocation, SoundEventAccessor> soundRegistry = Maps.newHashMap();
   private final SoundEngine sndManager;

   public SoundHandler(IResourceManager manager, GameSettings gameSettingsIn) {
      this.sndManager = new SoundEngine(this, gameSettingsIn, manager);
   }

   /**
    * Performs any reloading that can be done off-thread, such as file IO
    */
   protected SoundHandler.Loader prepare(IResourceManager p_212854_1_, IProfiler p_212854_2_) {
      SoundHandler.Loader soundhandler$loader = new SoundHandler.Loader();
      p_212854_2_.startTick();

      java.util.List<net.minecraft.util.Tuple<ResourceLocation, Runnable>> resources = new java.util.LinkedList<>();
      for(String s : p_212854_1_.getResourceNamespaces()) {
         p_212854_2_.startSection(s);

         try {
            for(IResource iresource : p_212854_1_.getAllResources(new ResourceLocation(s, "sounds.json"))) {
               p_212854_2_.startSection(iresource.getPackName());

               try {
                  p_212854_2_.startSection("parse");
                  Map<String, SoundList> map = getSoundMap(iresource.getInputStream());
                  p_212854_2_.endStartSection("register");

                  for(Entry<String, SoundList> entry : map.entrySet()) {
                     resources.add(new net.minecraft.util.Tuple<>(new ResourceLocation(s, entry.getKey()), () -> {
                     soundhandler$loader.func_217944_a(new ResourceLocation(s, entry.getKey()), entry.getValue(), p_212854_1_);
                     }));
                  }

                  p_212854_2_.endSection();
               } catch (RuntimeException runtimeexception) {
                  LOGGER.warn("Invalid sounds.json in resourcepack: '{}'", iresource.getPackName(), runtimeexception);
               }

               p_212854_2_.endSection();
            }
         } catch (IOException var13) {
            ;
         }

         p_212854_2_.endSection();
      }

      net.minecraftforge.fml.common.progress.StartupProgressManager.start("Loading sounds", resources.size(), bar -> {
         resources.forEach(entry -> {
            bar.step(entry.getA().toString());
            try {
                entry.getB().run();
            } catch (RuntimeException e) {
                LOGGER.warn("Invalid sounds.json", e);
            }
         });
      });

      p_212854_2_.endTick();
      return soundhandler$loader;
   }

   /**
    * Performs any reloading that must be done on the main thread, such as uploading textures to the GPU or touching
    * non-threadsafe data
    */
   protected void apply(SoundHandler.Loader p_212853_1_, IResourceManager p_212853_2_, IProfiler p_212853_3_) {
      p_212853_1_.func_217946_a(this.soundRegistry, this.sndManager);

      for(ResourceLocation resourcelocation : this.soundRegistry.keySet()) {
         SoundEventAccessor soundeventaccessor = this.soundRegistry.get(resourcelocation);
         if (soundeventaccessor.getSubtitle() instanceof TranslationTextComponent) {
            String s = ((TranslationTextComponent)soundeventaccessor.getSubtitle()).getKey();
            if (!I18n.hasKey(s)) {
               LOGGER.debug("Missing subtitle {} for event: {}", s, resourcelocation);
            }
         }
      }

      if (LOGGER.isDebugEnabled()) {
         for(ResourceLocation resourcelocation1 : this.soundRegistry.keySet()) {
            if (!Registry.SOUND_EVENT.containsKey(resourcelocation1)) {
               LOGGER.debug("Not having sound event for: {}", (Object)resourcelocation1);
            }
         }
      }

      this.sndManager.reload();
   }

   @Nullable
   protected static Map<String, SoundList> getSoundMap(InputStream p_175085_0_) {
      Map map;
      try {
         map = JSONUtils.fromJson(GSON, new InputStreamReader(p_175085_0_, StandardCharsets.UTF_8), TYPE);
      } finally {
         IOUtils.closeQuietly(p_175085_0_);
      }

      return map;
   }

   private static boolean func_215292_b(Sound p_215292_0_, ResourceLocation p_215292_1_, IResourceManager p_215292_2_) {
      ResourceLocation resourcelocation = p_215292_0_.getSoundAsOggLocation();
      if (!p_215292_2_.hasResource(resourcelocation)) {
         LOGGER.warn("File {} does not exist, cannot add it to event {}", resourcelocation, p_215292_1_);
         return false;
      } else {
         return true;
      }
   }

   @Nullable
   public SoundEventAccessor getAccessor(ResourceLocation location) {
      return this.soundRegistry.get(location);
   }

   public Collection<ResourceLocation> getAvailableSounds() {
      return this.soundRegistry.keySet();
   }

   /**
    * Play a sound
    */
   public void play(ISound sound) {
      this.sndManager.play(sound);
   }

   /**
    * Plays the sound in n ticks
    */
   public void playDelayed(ISound sound, int delay) {
      this.sndManager.playDelayed(sound, delay);
   }

   public void func_215289_a(ActiveRenderInfo p_215289_1_) {
      this.sndManager.func_217920_a(p_215289_1_);
   }

   public void pause() {
      this.sndManager.pause();
   }

   public void stop() {
      this.sndManager.stopAllSounds();
   }

   public void unloadSounds() {
      this.sndManager.unload();
   }

   public void tick(boolean p_215290_1_) {
      this.sndManager.tick(p_215290_1_);
   }

   public void resume() {
      this.sndManager.resume();
   }

   public void setSoundLevel(SoundCategory category, float volume) {
      if (category == SoundCategory.MASTER && volume <= 0.0F) {
         this.stop();
      }

      this.sndManager.setVolume(category, volume);
   }

   public void stop(ISound soundIn) {
      this.sndManager.stop(soundIn);
   }

   public boolean func_215294_c(ISound p_215294_1_) {
      return this.sndManager.func_217933_b(p_215294_1_);
   }

   public void addListener(ISoundEventListener listener) {
      this.sndManager.addListener(listener);
   }

   public void removeListener(ISoundEventListener listener) {
      this.sndManager.removeListener(listener);
   }

   public void stop(@Nullable ResourceLocation id, @Nullable SoundCategory category) {
      this.sndManager.stop(id, category);
   }

   //@Override //TODO: Filtered reload
   public net.minecraftforge.resource.IResourceType getResourceType() {
      return net.minecraftforge.resource.VanillaResourceType.SOUNDS;
   }

   public String getDebugString() {
      return this.sndManager.getDebugString();
   }

   @OnlyIn(Dist.CLIENT)
   public static class Loader {
      private final Map<ResourceLocation, SoundEventAccessor> field_217948_a = Maps.newHashMap();

      private void func_217944_a(ResourceLocation p_217944_1_, SoundList p_217944_2_, IResourceManager p_217944_3_) {
         SoundEventAccessor soundeventaccessor = this.field_217948_a.get(p_217944_1_);
         boolean flag = soundeventaccessor == null;
         if (flag || p_217944_2_.canReplaceExisting()) {
            if (!flag) {
               SoundHandler.LOGGER.debug("Replaced sound event location {}", (Object)p_217944_1_);
            }

            soundeventaccessor = new SoundEventAccessor(p_217944_1_, p_217944_2_.getSubtitle());
            this.field_217948_a.put(p_217944_1_, soundeventaccessor);
         }

         for(final Sound sound : p_217944_2_.getSounds()) {
            final ResourceLocation resourcelocation = sound.getSoundLocation();
            ISoundEventAccessor<Sound> isoundeventaccessor;
            switch(sound.getType()) {
            case FILE:
               if (!SoundHandler.func_215292_b(sound, p_217944_1_, p_217944_3_)) {
                  continue;
               }

               isoundeventaccessor = sound;
               break;
            case SOUND_EVENT:
               isoundeventaccessor = new ISoundEventAccessor<Sound>() {
                  public int getWeight() {
                     SoundEventAccessor soundeventaccessor1 = Loader.this.field_217948_a.get(resourcelocation);
                     return soundeventaccessor1 == null ? 0 : soundeventaccessor1.getWeight();
                  }

                  public Sound cloneEntry() {
                     SoundEventAccessor soundeventaccessor1 = Loader.this.field_217948_a.get(resourcelocation);
                     if (soundeventaccessor1 == null) {
                        return SoundHandler.MISSING_SOUND;
                     } else {
                        Sound sound1 = soundeventaccessor1.cloneEntry();
                        return new Sound(sound1.getSoundLocation().toString(), sound1.getVolume() * sound.getVolume(), sound1.getPitch() * sound.getPitch(), sound.getWeight(), Sound.Type.FILE, sound1.isStreaming() || sound.isStreaming(), sound1.shouldPreload(), sound1.getAttenuationDistance());
                     }
                  }

                  public void func_217867_a(SoundEngine p_217867_1_) {
                     SoundEventAccessor soundeventaccessor1 = Loader.this.field_217948_a.get(resourcelocation);
                     if (soundeventaccessor1 != null) {
                        soundeventaccessor1.func_217867_a(p_217867_1_);
                     }
                  }
               };
               break;
            default:
               throw new IllegalStateException("Unknown SoundEventRegistration type: " + sound.getType());
            }

            soundeventaccessor.addSound(isoundeventaccessor);
         }

      }

      public void func_217946_a(Map<ResourceLocation, SoundEventAccessor> p_217946_1_, SoundEngine p_217946_2_) {
         p_217946_1_.clear();

         for(Entry<ResourceLocation, SoundEventAccessor> entry : this.field_217948_a.entrySet()) {
            p_217946_1_.put(entry.getKey(), entry.getValue());
            entry.getValue().func_217867_a(p_217946_2_);
         }

      }
   }
}