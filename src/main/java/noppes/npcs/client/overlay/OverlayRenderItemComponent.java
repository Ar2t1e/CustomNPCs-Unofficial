package noppes.npcs.client.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.api.overlay.IRenderItemOverlay;

public class OverlayRenderItemComponent implements IOverlayRenderComponent {

   private final int x;
   private final int y;
   private final int id;
   private final ItemStack item;

   public OverlayRenderItemComponent(IRenderItemOverlay itemIn) {
      x = itemIn.getPosX();
      y = itemIn.getPosY();
      id = itemIn.getId();
      item = itemIn.getItem().getMCItemStack();
   }

   public void render(GuiGraphics graphics, int linkSide) {
      graphics.pose().pushPose();
      graphics.pose().translate((double)x / 1.2000000476837158D, (double)y / 1.2000000476837158D, (double)id / 1.2000000476837158D);
      graphics.pose().scale(1.2F, 1.2F, 1.0F);
      int width = (int)((float)Minecraft.getInstance().getWindow().getGuiScaledWidth() / 1.2F);
      int height = (int)((float)Minecraft.getInstance().getWindow().getGuiScaledHeight() / 1.2F);
      renderItemOverlay(graphics, linkSide, item, x, y, width, height);
      graphics.pose().popPose();
   }

   public void renderItemOverlay(GuiGraphics graphics, int linkSide, ItemStack item, int x, int y, int width, int height) {
      int offsetX = width / 2 * ((linkSide - 1) % 3);
      int offsetY = height / 2 * ((linkSide - 1) / 3);
      graphics.renderItem(item, x + offsetX, y + offsetY);
      graphics.renderItemDecorations(Minecraft.getInstance().font, item, x + offsetX, y + offsetY);
   }

}
