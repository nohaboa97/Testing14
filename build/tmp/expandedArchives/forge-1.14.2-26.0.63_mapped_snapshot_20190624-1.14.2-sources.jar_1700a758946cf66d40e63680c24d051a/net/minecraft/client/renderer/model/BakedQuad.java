package net.minecraft.client.renderer.model;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BakedQuad implements net.minecraftforge.client.model.pipeline.IVertexProducer {
   protected final int[] vertexData;
   protected final int tintIndex;
   protected final Direction face;
   protected final TextureAtlasSprite sprite;

   /**
    * @deprecated Use constructor with the format argument.
    */
   @Deprecated
   public BakedQuad(int[] vertexDataIn, int tintIndexIn, Direction faceIn, TextureAtlasSprite spriteIn) {
      this(vertexDataIn, tintIndexIn, faceIn, spriteIn, true, net.minecraft.client.renderer.vertex.DefaultVertexFormats.BLOCK);
   }
   
   public BakedQuad(int[] vertexDataIn, int tintIndexIn, Direction faceIn, TextureAtlasSprite spriteIn, boolean applyDiffuseLighting, net.minecraft.client.renderer.vertex.VertexFormat format) {
      this.format = format;
      this.applyDiffuseLighting = applyDiffuseLighting;
      this.vertexData = vertexDataIn;
      this.tintIndex = tintIndexIn;
      this.face = faceIn;
      this.sprite = spriteIn;
   }

   public TextureAtlasSprite getSprite() {
      return this.sprite;
   }

   public int[] getVertexData() {
      return this.vertexData;
   }

   public boolean hasTintIndex() {
      return this.tintIndex != -1;
   }

   public int getTintIndex() {
      return this.tintIndex;
   }

   public Direction getFace() {
      return this.face;
   }
   
   protected final net.minecraft.client.renderer.vertex.VertexFormat format;
   protected final boolean applyDiffuseLighting;
   
   @Override
   public void pipe(net.minecraftforge.client.model.pipeline.IVertexConsumer consumer) {
       net.minecraftforge.client.model.pipeline.LightUtil.putBakedQuad(consumer, this);
   }
   
   public net.minecraft.client.renderer.vertex.VertexFormat getFormat() {
       return format;
   }
   
   public boolean shouldApplyDiffuseLighting() {
       return applyDiffuseLighting;
   }
}