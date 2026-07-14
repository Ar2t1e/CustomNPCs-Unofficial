package noppes.npcs.shared.client.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;
import org.jetbrains.annotations.NotNull;

public class GuiButtonNextPage extends GuiButtonNop {

   protected static final ResourceLocation resource = new ResourceLocation("textures/gui/book.png");
   protected final boolean isLeftButton;

   public GuiButtonNextPage(IGuiInterface gui, int id, int x, int y, boolean isLeft, OnPress press) {
      super(gui, id, Component.empty(), x, y, press);
      width = 23;
      height = 13;
      isLeftButton = isLeft;
   }

   @Override
   public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      if (offsetHoverX != 0 || offsetHoverY != 0) {
         mouseX -= offsetHoverX;
         mouseY -= offsetHoverY;
      }
      int x = getX();
      int y = getY();
      isHovered = visible && mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
      if (visible && resource != null) {
         int u = 0;
         int v = 192;
         if (isHovered) { u += width; }
         if (!isLeftButton) { v += height; }
         graphics.blit(resource, x, y, u, v, width, height);
      }
   }

}
