package noppes.npcs.client.gui.model;

import com.mojang.blaze3d.systems.RenderSystem;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.wrapper.gui.CustomGuiButtonWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiTextFieldWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiTexturedRectWrapper;
import noppes.npcs.client.gui.custom.GuiCustom;
import noppes.npcs.shared.client.gui.components.custom.CustomGuiButton;
import noppes.npcs.shared.client.gui.components.custom.CustomGuiTextField;
import noppes.npcs.shared.client.gui.components.custom.CustomGuiTexturedRect;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;
import org.jetbrains.annotations.NotNull;

public class GuiModelColor extends GuiCustom implements ITextfieldListener {

   protected static final ResourceLocation colorPicker = new ResourceLocation("moreplayermodels","textures/gui/color.png");
   protected static final ResourceLocation colorGui = new ResourceLocation("moreplayermodels","textures/gui/color_gui.png");
   protected final CustomGuiButton button;
   protected final GuiModelColor.ColorCallback callback;
   protected int colorX;
   protected int colorY;
   protected CustomGuiTextField textfield;
   public int color;

   public GuiModelColor(GuiCustom parent, int c, GuiModelColor.ColorCallback callback) {
      super(parent.getMenu(), parent.inv, Component.empty());
       this.callback = callback;
      this.imageHeight = 170;
      this.imageWidth = 130;
      this.color = c;
      CustomGuiTexturedRectWrapper bg = new CustomGuiTexturedRectWrapper();
      bg.setTexture(CustomNpcs.MODID + ":textures/gui/components.png").setSize(this.imageWidth, this.imageHeight);
      bg.setTextureOffset(0, 0).setRepeatingTexture(64, 64, 4);
      this.background = new CustomGuiTexturedRect(this, bg);
      this.textfield = new CustomGuiTextField(this, (new CustomGuiTextFieldWrapper(24, 35, 25, 60, 20)).setCharacterType(2).setColor(this.color).setText(this.getColor()).setOnChange((gui, text) -> {
         this.color = Integer.parseInt(text.getText(), 16);
         callback.color(this.color);
         this.textfield.setTextColor(this.color);
      }));
      this.button = new CustomGuiButton(this, (CustomGuiButtonWrapper)(new CustomGuiButtonWrapper(66, "x", 107, 8, 20, 20)).setOnPress((gui, button) -> parent.subgui = null).setDisablePackets());
      this.minecraft = Minecraft.getInstance();
   }

   public void init() {
      super.init();
      this.add(this.textfield);
      this.add(this.button);
      this.background.setTexture(colorGui);
      this.colorX = this.leftPos + 4;
      this.colorY = this.topPos + 50;
   }

   public void render(@NotNull GuiGraphics graphics, int par1, int limbSwingAmount, float par3) {
      super.render(graphics, par1, limbSwingAmount, par3);
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, colorPicker);
      graphics.blit(colorPicker, this.colorX, this.colorY, 0, 0, 120, 120);
   }

   public boolean mouseClicked(double i, double j, int k) {
      super.mouseClicked(i, j, k);
      if (minecraft != null && !(i < (double)this.colorX) && !(i > (double)(this.colorX + 120)) && !(j < (double)this.colorY) && !(j > (double)(this.colorY + 120))) {
         Resource resource = this.minecraft.getResourceManager().getResource(colorPicker).orElse(null);
         if (resource != null) {
            try {
               InputStream stream = resource.open();
               try {
                  BufferedImage bufferedimage = ImageIO.read(stream);
                  int color = bufferedimage.getRGB((int)(i - (double)this.leftPos - 4.0D) * 4, (int)(j - (double)this.topPos - 50.0D) * 4) & new Color(0xFFFFFF).getRGB();
                  if (color != 0) {
                     this.color = color;
                     this.callback.color(color);
                     this.textfield.setTextColor(color);
                     this.textfield.setValue(this.getColor());
                  }
               } catch (Throwable var11) {
                  try {
                     stream.close();
                  } catch (Throwable var10) {
                     var11.addSuppressed(var10);
                  }
                  throw var11;
               }
               stream.close();
            } catch (IOException ignored) {}
         }

         return true;
      } else {
         return false;
      }
   }

   public void unFocused(GuiTextFieldNop textfield) {
      try {
         this.color = Integer.parseInt(textfield.getValue(), 16);
      } catch (NumberFormatException var3) {
         this.color = 0;
      }

      this.callback.color(this.color);
      textfield.setTextColor(this.color);
   }

   public String getColor() {
      StringBuilder str = new StringBuilder(Integer.toHexString(this.color));
      while (str.length() < 6) { str.insert(0, "0"); }
      return str.toString();
   }

   public interface ColorCallback {
      void color(int var1);
   }

}
