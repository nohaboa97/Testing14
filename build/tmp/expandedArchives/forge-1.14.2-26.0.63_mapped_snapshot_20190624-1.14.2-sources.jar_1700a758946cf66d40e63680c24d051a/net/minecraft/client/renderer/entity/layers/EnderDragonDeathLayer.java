package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Random;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.EnderDragonModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnderDragonDeathLayer extends LayerRenderer<EnderDragonEntity, EnderDragonModel> {
   public EnderDragonDeathLayer(IEntityRenderer<EnderDragonEntity, EnderDragonModel> p_i50941_1_) {
      super(p_i50941_1_);
   }

   public void render(EnderDragonEntity entityIn, float p_212842_2_, float p_212842_3_, float p_212842_4_, float p_212842_5_, float p_212842_6_, float p_212842_7_, float p_212842_8_) {
      if (entityIn.deathTicks > 0) {
         Tessellator tessellator = Tessellator.getInstance();
         BufferBuilder bufferbuilder = tessellator.getBuffer();
         RenderHelper.disableStandardItemLighting();
         float f = ((float)entityIn.deathTicks + p_212842_4_) / 200.0F;
         float f1 = 0.0F;
         if (f > 0.8F) {
            f1 = (f - 0.8F) / 0.2F;
         }

         Random random = new Random(432L);
         GlStateManager.disableTexture();
         GlStateManager.shadeModel(7425);
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
         GlStateManager.disableAlphaTest();
         GlStateManager.enableCull();
         GlStateManager.depthMask(false);
         GlStateManager.pushMatrix();
         GlStateManager.translatef(0.0F, -1.0F, -2.0F);

         for(int i = 0; (float)i < (f + f * f) / 2.0F * 60.0F; ++i) {
            GlStateManager.rotatef(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotatef(random.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotatef(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotatef(random.nextFloat() * 360.0F + f * 90.0F, 0.0F, 0.0F, 1.0F);
            float f2 = random.nextFloat() * 20.0F + 5.0F + f1 * 10.0F;
            float f3 = random.nextFloat() * 2.0F + 1.0F + f1 * 2.0F;
            bufferbuilder.begin(6, DefaultVertexFormats.POSITION_COLOR);
            bufferbuilder.pos(0.0D, 0.0D, 0.0D).color(255, 255, 255, (int)(255.0F * (1.0F - f1))).endVertex();
            bufferbuilder.pos(-0.866D * (double)f3, (double)f2, (double)(-0.5F * f3)).color(255, 0, 255, 0).endVertex();
            bufferbuilder.pos(0.866D * (double)f3, (double)f2, (double)(-0.5F * f3)).color(255, 0, 255, 0).endVertex();
            bufferbuilder.pos(0.0D, (double)f2, (double)(1.0F * f3)).color(255, 0, 255, 0).endVertex();
            bufferbuilder.pos(-0.866D * (double)f3, (double)f2, (double)(-0.5F * f3)).color(255, 0, 255, 0).endVertex();
            tessellator.draw();
         }

         GlStateManager.popMatrix();
         GlStateManager.depthMask(true);
         GlStateManager.disableCull();
         GlStateManager.disableBlend();
         GlStateManager.shadeModel(7424);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.enableTexture();
         GlStateManager.enableAlphaTest();
         RenderHelper.enableStandardItemLighting();
      }
   }

   public boolean shouldCombineTextures() {
      return false;
   }
}