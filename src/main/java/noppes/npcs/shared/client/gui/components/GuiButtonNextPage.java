package noppes.npcs.shared.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;
import org.jetbrains.annotations.NotNull;

public class GuiButtonNextPage extends GuiButtonNop {

   protected static final ResourceLocation resource = new ResourceLocation("textures/gui/book.png");
   protected final boolean isLeftButton;

   public GuiButtonNextPage(IGuiInterface gui, int id, int x, int y, boolean par4, OnPress press) {
      super(gui, id, x, y, 23, 13, Component.empty(), press);
      isLeftButton = par4;
   }

   @Override
   public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      if (offsetHoverX != 0 || offsetHoverY != 0) {
         mouseX -= offsetHoverX;
         mouseY -= offsetHoverY;
      }
      isHovered = visible && mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
      if (visible && resource != null) {
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.setShaderTexture(0, resource);
         int u = 0;
         int v = 192;
         if (isHovered) { u += 23; }
         if (!isLeftButton) { v += 13; }
         graphics.blit(resource, getX(), getY(), u, v, 23, 13);
      }
   }

   @Override
   protected boolean isValidClickButton(int mouseButton) { return mouseButton == 0; }

}
