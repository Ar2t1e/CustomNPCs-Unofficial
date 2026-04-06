package noppes.npcs.shared.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;

public class GuiButtonNextPage extends GuiButtonNop {

   protected static final ResourceLocation resource = new ResourceLocation("textures/gui/book.png");
   protected final boolean isLeftButton;

   public GuiButtonNextPage(IGuiInterface gui, int id, int x, int y, boolean par4, OnPress press) {
      super(gui, id, x, y, 23, 13, Component.empty(), press);
      isLeftButton = par4;
   }

   @Override
   public void render(int mouseX, int mouseY, float partialTicks) {
      if (offsetHoverX != 0 || offsetHoverY != 0) {
         mouseX -= offsetHoverX;
         mouseY -= offsetHoverY;
      }
      isHovered = visible && mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
      if (visible && resource != null) {
         Minecraft mc = Minecraft.getMinecraft();
         mc.getTextureManager().bindTexture(resource);
         GlStateManager.enableDepth();
         GlStateManager.enableBlend();
         GlStateManager.pushMatrix();
         GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
         GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
         int u = 0;
         int v = 192;
         if (isHovered) { u += 23; }
         if (!isLeftButton) { v += 13; }
         drawTexturedModalRect(getX(), getY(), u, v, 23, 13);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.popMatrix();
         GlStateManager.disableBlend();
      }
   }

   @Override
   protected boolean isValidClickButton(int mouseButton) { return mouseButton == 0; }

}
