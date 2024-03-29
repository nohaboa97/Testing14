package net.minecraft.client.shader;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.TextureUtil;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;

@OnlyIn(Dist.CLIENT)
public class ShaderLoader {
   private final ShaderLoader.ShaderType shaderType;
   private final String shaderFilename;
   private final int shader;
   private int shaderAttachCount;

   private ShaderLoader(ShaderLoader.ShaderType type, int shaderId, String filename) {
      this.shaderType = type;
      this.shader = shaderId;
      this.shaderFilename = filename;
   }

   public void attachShader(IShaderManager manager) {
      ++this.shaderAttachCount;
      GLX.glAttachShader(manager.getProgram(), this.shader);
   }

   public void detachShader() {
      --this.shaderAttachCount;
      if (this.shaderAttachCount <= 0) {
         GLX.glDeleteShader(this.shader);
         this.shaderType.getLoadedShaders().remove(this.shaderFilename);
      }

   }

   public String getShaderFilename() {
      return this.shaderFilename;
   }

   public static ShaderLoader func_216534_a(ShaderLoader.ShaderType p_216534_0_, String p_216534_1_, InputStream p_216534_2_) throws IOException {
      String s = TextureUtil.readResourceAsString(p_216534_2_);
      if (s == null) {
         throw new IOException("Could not load program " + p_216534_0_.getShaderName());
      } else {
         int i = GLX.glCreateShader(p_216534_0_.getShaderMode());
         GLX.glShaderSource(i, s);
         GLX.glCompileShader(i);
         if (GLX.glGetShaderi(i, GLX.GL_COMPILE_STATUS) == 0) {
            String s1 = StringUtils.trim(GLX.glGetShaderInfoLog(i, 32768));
            throw new IOException("Couldn't compile " + p_216534_0_.getShaderName() + " program: " + s1);
         } else {
            ShaderLoader shaderloader = new ShaderLoader(p_216534_0_, i, p_216534_1_);
            p_216534_0_.getLoadedShaders().put(p_216534_1_, shaderloader);
            return shaderloader;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum ShaderType {
      VERTEX("vertex", ".vsh", GLX.GL_VERTEX_SHADER),
      FRAGMENT("fragment", ".fsh", GLX.GL_FRAGMENT_SHADER);

      private final String shaderName;
      private final String shaderExtension;
      private final int shaderMode;
      private final Map<String, ShaderLoader> loadedShaders = Maps.newHashMap();

      private ShaderType(String shaderNameIn, String shaderExtensionIn, int shaderModeIn) {
         this.shaderName = shaderNameIn;
         this.shaderExtension = shaderExtensionIn;
         this.shaderMode = shaderModeIn;
      }

      public String getShaderName() {
         return this.shaderName;
      }

      public String getShaderExtension() {
         return this.shaderExtension;
      }

      private int getShaderMode() {
         return this.shaderMode;
      }

      /**
       * gets a map of loaded shaders for the ShaderType.
       */
      public Map<String, ShaderLoader> getLoadedShaders() {
         return this.loadedShaders;
      }
   }
}