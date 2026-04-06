package noppes.npcs.shared.client.gui.components.custom;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import noppes.npcs.api.wrapper.gui.CustomGuiButtonWrapper;
import noppes.npcs.client.gui.custom.GuiCustom;

import javax.annotation.Nonnull;

public class GuiColorButton extends CustomGuiButton {

   public int color;

   public GuiColorButton(GuiCustom parent, CustomGuiButtonWrapper component, int colorIn) {
      super(parent, component);
      width = 50;
      height = 20;
      color = colorIn;
   }

   @Override
   public void renderWidget(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      isHovered = false;
      if (!visible) { return; }
      isHovered = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
      graphics.fill(getX(), getY(), getX() + width, getY() + height, color | Mth.ceil(alpha * 255.0F) << 24);
   }

}
