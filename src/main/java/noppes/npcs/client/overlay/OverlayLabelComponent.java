package noppes.npcs.client.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import noppes.npcs.api.overlay.ILabel;

import java.awt.*;

public class OverlayLabelComponent implements IOverlayRenderComponent {

   private final String text;
   private final int x;
   private final int y;
   private final int id;
   private final float scale;

   public OverlayLabelComponent(ILabel label) {
      String textIn = label.getText();
      x = label.getPosX();
      y = label.getPosY();
      id = label.getId();
      scale = label.getScale();
      StringBuilder stringBuilder = new StringBuilder();
      for (String s : textIn.split("&t")) {
         stringBuilder.append(Component.translatable(s));
      }
      text = stringBuilder.toString();
   }

   public void render(GuiGraphics graphics, int linkSide) {
      graphics.pose().pushPose();
      graphics.pose().translate(x, y, (double)id);
      graphics.pose().scale(scale, scale, scale);
      int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
      int height = Minecraft.getInstance().getWindow().getGuiScaledHeight();
      renderString(graphics, text, x, y, linkSide, width, height);
      graphics.pose().popPose();
   }

   public void renderString(GuiGraphics graphics, String text, int x, int y, int linkSide, int width, int height) {
      int offsetX = width / 2 * ((linkSide - 1) % 3);
      int offsetY = height / 2 * ((linkSide - 1) / 3);
      graphics.drawString(Minecraft.getInstance().font, text, x + offsetX, y + offsetY, new Color(0xFFFFFF).getRGB());
   }

}
