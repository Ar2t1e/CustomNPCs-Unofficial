package noppes.npcs.client.overlay;

import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import noppes.npcs.api.overlay.IOverlayTexturedRect;

public class OverlayTexturedRectComponent implements IOverlayRenderComponent {

   private final int x;
   private final int y;
   private final int width;
   private final int height;
   private final String texture;
   private final int textureX;
   private final int textureY;
   private final int textureMaxX;
   private final int textureMaxY;
   private final int id;

   public OverlayTexturedRectComponent(IOverlayTexturedRect component) {
      x = component.getPosX();
      y = component.getPosY();
      id = component.getId();
      width = component.getWidth();
      height = component.getHeight();
      texture = component.getTexture();
      textureX = component.getTextureX();
      textureY = component.getTextureY();
      textureMaxX = component.getTextureMaxX();
      textureMaxY = component.getTextureMaxY();
   }

   public void render(GuiGraphics graphics, int linkSide) {
      graphics.pose().pushPose();
      graphics.pose().translate(2 * x, 2 * y, (double)id);
      int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
      int height = Minecraft.getInstance().getWindow().getGuiScaledHeight();
      int i = width / 2;
      if (Objects.equals(texture, "")) {
         renderGradientRect(graphics, x, y, linkSide, width, height, width, height, i, -1072689136, -804253680);
      } else {
         ResourceLocation resLoc = ResourceLocation.tryParse(texture);
         if (textureX >= 0 && textureY >= 0) {
            if (textureMaxX >= 0 && textureMaxY >= 0) {
               renderRectTextureCustomSize(graphics, resLoc, x, y, linkSide, width, height, width, height, textureX, textureY, textureMaxX, textureMaxY);
            } else {
               renderRectTextureSize(graphics, resLoc, x, y, linkSide, width, height, width, height, textureX, textureY);
            }
         } else {
            renderRectTexture(graphics, resLoc, x, y, linkSide, width, height, width, height);
         }
      }

      graphics.pose().popPose();
   }

   public void renderGradientRect(GuiGraphics graphics, int x, int y, int linkSide, int widthScaled, int heightScaled, int width, int height, int ignoredI, int startColor, int endColor) {
      int offsetX = widthScaled / 2 * ((linkSide - 1) % 3);
      int offsetY = heightScaled / 2 * ((linkSide - 1) / 3);
      graphics.fillGradient(offsetX, offsetY, offsetX + width, offsetY + height, startColor, endColor);
   }

   public void renderRectTexture(GuiGraphics graphics, ResourceLocation resLoc, int x, int y, int linkSide, int widthScaled, int heightScaled, int width, int height) {
      renderRectTextureCustomSize(graphics, resLoc, 0, 0, linkSide, widthScaled, heightScaled, width, height, 0, 0, 256, 256);
   }

   public void renderRectTextureSize(GuiGraphics graphics, ResourceLocation resLoc, int x, int y, int linkSide, int widthScaled, int heightScaled, int width, int height, int textureX, int textureY) {
      renderRectTextureCustomSize(graphics, resLoc, 0, 0, linkSide, widthScaled, heightScaled, width, height, textureX, textureY, 256, 256);
   }

   public void renderRectTextureCustomSize(GuiGraphics graphics, ResourceLocation resLoc, int x, int y, int linkSide, int widthScaled, int heightScaled, int width, int height, int textureX, int textureY, int textureMaxX, int textureMaxY) {
      int offsetX = widthScaled / 2 * ((linkSide - 1) % 3);
      int offsetY = heightScaled / 2 * ((linkSide - 1) / 3);
      graphics.blit(resLoc, offsetX, offsetY, (float)textureX, (float)textureY, width, height, textureMaxX, textureMaxY);
   }

}
