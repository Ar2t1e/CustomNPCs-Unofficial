package noppes.npcs.shared.client.gui.components.custom;

import noppes.npcs.api.wrapper.gui.CustomGuiButtonWrapper;
import noppes.npcs.client.gui.custom.GuiCustom;

public class GuiColorButton extends CustomGuiButton {

   public int color;

   public GuiColorButton(GuiCustom parent, CustomGuiButtonWrapper component, int colorIn) {
      super(parent, component);
      width = 50;
      height = 20;
      color = colorIn;
   }

   @Override
   public void renderWidget(int mouseX, int mouseY, float partialTicks) {
      isHovered = false;
      if (!visible) { return; }
      isHovered = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
      drawRect(getX(), getY(), getX() + width, getY() + height, color | (int) Math.ceil(alpha * 255.0F) << 24);
   }

}
