package net.minecraft.client.gui.screen;

import com.google.common.util.concurrent.Runnables;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.client.gui.AccessibilityScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.renderer.RenderSkybox;
import net.minecraft.client.renderer.RenderSkyboxCube;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.storage.SaveFormat;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MainMenuScreen extends Screen {
   public static final RenderSkyboxCube field_213098_a = new RenderSkyboxCube(new ResourceLocation("textures/gui/title/background/panorama"));
   private static final ResourceLocation PANORAMA_OVERLAY_TEXTURES = new ResourceLocation("textures/gui/title/background/panorama_overlay.png");
   private static final ResourceLocation ACCESSIBILITY_TEXTURES = new ResourceLocation("textures/gui/accessibility.png");
   private final boolean field_213101_e;
   @Nullable
   private String splashText;
   private Button buttonResetDemo;
   private final Object threadLock = new Object();
   public static final String MORE_INFO_TEXT = "Please click " + TextFormatting.UNDERLINE + "here" + TextFormatting.RESET + " for more information.";
   private int openGLWarning2Width;
   private int openGLWarning1Width;
   private int openGLWarningX1;
   private int openGLWarningY1;
   private int openGLWarningX2;
   private int openGLWarningY2;
   private String openGLWarning1;
   private String openGLWarning2 = MORE_INFO_TEXT;
   private String openGLWarningLink;
   private static final ResourceLocation MINECRAFT_TITLE_TEXTURES = new ResourceLocation("textures/gui/title/minecraft.png");
   private static final ResourceLocation MINECRAFT_TITLE_EDITION = new ResourceLocation("textures/gui/title/edition.png");
   private boolean hasCheckedForRealmsNotification;
   private Screen realmsNotification;
   private int widthCopyright;
   private int widthCopyrightRest;
   private final RenderSkybox panorama = new RenderSkybox(field_213098_a);
   private final boolean field_213102_y;
   private long field_213103_z;
   private net.minecraftforge.client.gui.NotificationModUpdateScreen modUpdateNotification;

   public MainMenuScreen() {
      this(false);
   }

   public MainMenuScreen(boolean p_i51107_1_) {
      super(new TranslationTextComponent("narrator.screen.title"));
      this.field_213102_y = p_i51107_1_;
      this.field_213101_e = (double)(new Random()).nextFloat() < 1.0E-4D;
      this.openGLWarning1 = "";
      if (!GLX.supportsOpenGL2() && !GLX.isNextGen()) {
         this.openGLWarning1 = I18n.format("title.oldgl1");
         this.openGLWarning2 = I18n.format("title.oldgl2");
         this.openGLWarningLink = "https://help.mojang.com/customer/portal/articles/325948?ref=game";
      }

   }

   /**
    * Is there currently a realms notification screen, and are realms notifications enabled?
    */
   private boolean areRealmsNotificationsEnabled() {
      return this.minecraft.gameSettings.realmsNotifications && this.realmsNotification != null;
   }

   public void tick() {
      if (this.areRealmsNotificationsEnabled()) {
         this.realmsNotification.tick();
      }

   }

   public static CompletableFuture<Void> func_213097_a(TextureManager p_213097_0_, Executor p_213097_1_) {
      return CompletableFuture.allOf(p_213097_0_.loadAsync(MINECRAFT_TITLE_TEXTURES, p_213097_1_), p_213097_0_.loadAsync(MINECRAFT_TITLE_EDITION, p_213097_1_), p_213097_0_.loadAsync(PANORAMA_OVERLAY_TEXTURES, p_213097_1_), field_213098_a.func_217617_a(p_213097_0_, p_213097_1_));
   }

   public boolean isPauseScreen() {
      return false;
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   protected void init() {
      if (this.splashText == null) {
         this.splashText = this.minecraft.getSplashes().func_215276_a();
      }

      this.widthCopyright = this.font.getStringWidth("Copyright Mojang AB. Do not distribute!");
      this.widthCopyrightRest = this.width - this.widthCopyright - 2;
      int i = 24;
      int j = this.height / 4 + 48;
      Button modbutton = null;
      if (this.minecraft.isDemo()) {
         this.addDemoButtons(j, 24);
      } else {
         this.addSingleplayerMultiplayerButtons(j, 24);
         modbutton = this.addButton(new Button(this.width / 2 - 100, j + 24 * 2, 98, 20, I18n.format("fml.menu.mods"), button -> {
            this.minecraft.displayGuiScreen(new net.minecraftforge.fml.client.gui.GuiModList(this));
         }));
      }

      this.addButton(new ImageButton(this.width / 2 - 124, j + 72 + 12, 20, 20, 0, 106, 20, Button.WIDGETS_LOCATION, 256, 256, (p_213090_1_) -> {
         this.minecraft.displayGuiScreen(new LanguageScreen(this, this.minecraft.gameSettings, this.minecraft.getLanguageManager()));
      }, I18n.format("narrator.button.language")));
      this.addButton(new Button(this.width / 2 - 100, j + 72 + 12, 98, 20, I18n.format("menu.options"), (p_213096_1_) -> {
         this.minecraft.displayGuiScreen(new OptionsScreen(this, this.minecraft.gameSettings));
      }));
      this.addButton(new Button(this.width / 2 + 2, j + 72 + 12, 98, 20, I18n.format("menu.quit"), (p_213094_1_) -> {
         this.minecraft.shutdown();
      }));
      this.addButton(new ImageButton(this.width / 2 + 104, j + 72 + 12, 20, 20, 0, 0, 20, ACCESSIBILITY_TEXTURES, 32, 64, (p_213088_1_) -> {
         this.minecraft.displayGuiScreen(new AccessibilityScreen(this, this.minecraft.gameSettings));
      }, I18n.format("narrator.button.accessibility")));
      synchronized(this.threadLock) {
         this.openGLWarning1Width = this.font.getStringWidth(this.openGLWarning1);
         this.openGLWarning2Width = this.font.getStringWidth(this.openGLWarning2);
         int k = Math.max(this.openGLWarning1Width, this.openGLWarning2Width);
         this.openGLWarningX1 = (this.width - k) / 2;
         this.openGLWarningY1 = j - 24;
         this.openGLWarningX2 = this.openGLWarningX1 + k;
         this.openGLWarningY2 = this.openGLWarningY1 + 24;
      }

      this.minecraft.setConnectedToRealms(false);
      if (this.minecraft.gameSettings.realmsNotifications && !this.hasCheckedForRealmsNotification) {
         RealmsBridge realmsbridge = new RealmsBridge();
         this.realmsNotification = realmsbridge.getNotificationScreen(this);
         this.hasCheckedForRealmsNotification = true;
      }

      if (this.areRealmsNotificationsEnabled()) {
         this.realmsNotification.init(this.minecraft, this.width, this.height);
      }
      modUpdateNotification = net.minecraftforge.client.gui.NotificationModUpdateScreen.init(this, modbutton);

   }

   /**
    * Adds Singleplayer and Multiplayer buttons on Main Menu for players who have bought the game.
    */
   private void addSingleplayerMultiplayerButtons(int yIn, int rowHeightIn) {
      this.addButton(new Button(this.width / 2 - 100, yIn, 200, 20, I18n.format("menu.singleplayer"), (p_213089_1_) -> {
         this.minecraft.displayGuiScreen(new WorldSelectionScreen(this));
      }));
      this.addButton(new Button(this.width / 2 - 100, yIn + rowHeightIn * 1, 200, 20, I18n.format("menu.multiplayer"), (p_213086_1_) -> {
         this.minecraft.displayGuiScreen(new MultiplayerScreen(this));
      }));
      this.addButton(new Button(this.width / 2 + 2, yIn + rowHeightIn * 2, 98, 20, I18n.format("menu.online"), (p_213095_1_) -> {
         this.switchToRealms();
      }));
   }

   /**
    * Adds Demo buttons on Main Menu for players who are playing Demo.
    */
   private void addDemoButtons(int yIn, int rowHeightIn) {
      this.addButton(new Button(this.width / 2 - 100, yIn, 200, 20, I18n.format("menu.playdemo"), (p_213092_1_) -> {
         this.minecraft.launchIntegratedServer("Demo_World", "Demo_World", MinecraftServer.DEMO_WORLD_SETTINGS);
      }));
      this.buttonResetDemo = this.addButton(new Button(this.width / 2 - 100, yIn + rowHeightIn * 1, 200, 20, I18n.format("menu.resetdemo"), (p_213091_1_) -> {
         SaveFormat saveformat1 = this.minecraft.getSaveLoader();
         WorldInfo worldinfo1 = saveformat1.getWorldInfo("Demo_World");
         if (worldinfo1 != null) {
            this.minecraft.displayGuiScreen(new ConfirmScreen(this::func_213087_a, new TranslationTextComponent("selectWorld.deleteQuestion"), new TranslationTextComponent("selectWorld.deleteWarning", worldinfo1.getWorldName()), I18n.format("selectWorld.deleteButton"), I18n.format("gui.cancel")));
         }

      }));
      SaveFormat saveformat = this.minecraft.getSaveLoader();
      WorldInfo worldinfo = saveformat.getWorldInfo("Demo_World");
      if (worldinfo == null) {
         this.buttonResetDemo.active = false;
      }

   }

   private void switchToRealms() {
      RealmsBridge realmsbridge = new RealmsBridge();
      realmsbridge.switchToRealms(this);
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      if (this.field_213103_z == 0L && this.field_213102_y) {
         this.field_213103_z = Util.milliTime();
      }

      float f = this.field_213102_y ? (float)(Util.milliTime() - this.field_213103_z) / 1000.0F : 1.0F;
      fill(0, 0, this.width, this.height, -1);
      this.panorama.func_217623_a(p_render_3_, MathHelper.clamp(f, 0.0F, 1.0F));
      int i = 274;
      int j = this.width / 2 - 137;
      int k = 30;
      this.minecraft.getTextureManager().bindTexture(PANORAMA_OVERLAY_TEXTURES);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, this.field_213102_y ? (float)MathHelper.ceil(MathHelper.clamp(f, 0.0F, 1.0F)) : 1.0F);
      blit(0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);
      float f1 = this.field_213102_y ? MathHelper.clamp(f - 1.0F, 0.0F, 1.0F) : 1.0F;
      int l = MathHelper.ceil(f1 * 255.0F) << 24;
      if ((l & -67108864) != 0) {
         this.minecraft.getTextureManager().bindTexture(MINECRAFT_TITLE_TEXTURES);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, f1);
         if (this.field_213101_e) {
            this.blit(j + 0, 30, 0, 0, 99, 44);
            this.blit(j + 99, 30, 129, 0, 27, 44);
            this.blit(j + 99 + 26, 30, 126, 0, 3, 44);
            this.blit(j + 99 + 26 + 3, 30, 99, 0, 26, 44);
            this.blit(j + 155, 30, 0, 45, 155, 44);
         } else {
            this.blit(j + 0, 30, 0, 0, 155, 44);
            this.blit(j + 155, 30, 0, 45, 155, 44);
         }

         this.minecraft.getTextureManager().bindTexture(MINECRAFT_TITLE_EDITION);
         blit(j + 88, 67, 0.0F, 0.0F, 98, 14, 128, 16);
         net.minecraftforge.client.ForgeHooksClient.renderMainMenu(this, this.font, this.width, this.height);
         if (this.splashText != null) {
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)(this.width / 2 + 90), 70.0F, 0.0F);
            GlStateManager.rotatef(-20.0F, 0.0F, 0.0F, 1.0F);
            float f2 = 1.8F - MathHelper.abs(MathHelper.sin((float)(Util.milliTime() % 1000L) / 1000.0F * ((float)Math.PI * 2F)) * 0.1F);
            f2 = f2 * 100.0F / (float)(this.font.getStringWidth(this.splashText) + 32);
            GlStateManager.scalef(f2, f2, f2);
            this.drawCenteredString(this.font, this.splashText, 0, -8, 16776960 | l);
            GlStateManager.popMatrix();
         }

         String s = "Minecraft " + SharedConstants.getVersion().getName();
         if (this.minecraft.isDemo()) {
            s = s + " Demo";
         } else {
            s = s + ("release".equalsIgnoreCase(this.minecraft.getVersionType()) ? "" : "/" + this.minecraft.getVersionType());
         }

         net.minecraftforge.fml.BrandingControl.forEachLine(true, true, (brdline, brd) ->
            this.drawString(this.font, brd, 2, this.height - ( 10 + brdline * (this.font.FONT_HEIGHT + 1)), 16777215 | l)
         );

         this.drawString(this.font, "Copyright Mojang AB. Do not distribute!", this.widthCopyrightRest, this.height - 10, 16777215 | l);
         if (p_render_1_ > this.widthCopyrightRest && p_render_1_ < this.widthCopyrightRest + this.widthCopyright && p_render_2_ > this.height - 10 && p_render_2_ < this.height) {
            fill(this.widthCopyrightRest, this.height - 1, this.widthCopyrightRest + this.widthCopyright, this.height, 16777215 | l);
         }

         if (this.openGLWarning1 != null && !this.openGLWarning1.isEmpty()) {
            fill(this.openGLWarningX1 - 2, this.openGLWarningY1 - 2, this.openGLWarningX2 + 2, this.openGLWarningY2 - 1, 1428160512);
            this.drawString(this.font, this.openGLWarning1, this.openGLWarningX1, this.openGLWarningY1, 16777215 | l);
            this.drawString(this.font, this.openGLWarning2, (this.width - this.openGLWarning2Width) / 2, this.openGLWarningY1 + 12, 16777215 | l);
         }

         for(Widget widget : this.buttons) {
            widget.setAlpha(f1);
         }

         super.render(p_render_1_, p_render_2_, p_render_3_);
         if (this.areRealmsNotificationsEnabled() && f1 >= 1.0F) {
            this.realmsNotification.render(p_render_1_, p_render_2_, p_render_3_);
         }
         modUpdateNotification.render(p_render_1_, p_render_2_, p_render_3_);

      }
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
         return true;
      } else {
         synchronized(this.threadLock) {
            if (!this.openGLWarning1.isEmpty() && !StringUtils.isNullOrEmpty(this.openGLWarningLink) && p_mouseClicked_1_ >= (double)this.openGLWarningX1 && p_mouseClicked_1_ <= (double)this.openGLWarningX2 && p_mouseClicked_3_ >= (double)this.openGLWarningY1 && p_mouseClicked_3_ <= (double)this.openGLWarningY2) {
               ConfirmOpenLinkScreen confirmopenlinkscreen = new ConfirmOpenLinkScreen((p_213093_1_) -> {
                  if (p_213093_1_) {
                     Util.getOSType().openURI(this.openGLWarningLink);
                  }

                  this.minecraft.displayGuiScreen(this);
               }, this.openGLWarningLink, true);
               this.minecraft.displayGuiScreen(confirmopenlinkscreen);
               return true;
            }
         }

         if (this.areRealmsNotificationsEnabled() && this.realmsNotification.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
            return true;
         } else {
            if (p_mouseClicked_1_ > (double)this.widthCopyrightRest && p_mouseClicked_1_ < (double)(this.widthCopyrightRest + this.widthCopyright) && p_mouseClicked_3_ > (double)(this.height - 10) && p_mouseClicked_3_ < (double)this.height) {
               this.minecraft.displayGuiScreen(new WinGameScreen(false, Runnables.doNothing()));
            }

            return false;
         }
      }
   }

   public void removed() {
      if (this.realmsNotification != null) {
         this.realmsNotification.removed();
      }

   }

   private void func_213087_a(boolean p_213087_1_) {
      if (p_213087_1_) {
         SaveFormat saveformat = this.minecraft.getSaveLoader();
         saveformat.deleteWorldDirectory("Demo_World");
      }

      this.minecraft.displayGuiScreen(this);
   }
}