package noppes.npcs.client.gui.player.tabs;

import com.mojang.blaze3d.systems.RenderSystem;

import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.player.GuiLog;
import org.jetbrains.annotations.NotNull;

public class InventoryTabFactions extends AbstractTab {

   protected final Component displayString = Component.translatable("menu.factions");

   public InventoryTabFactions() {
      super(1, 0, 0, new ItemStack(Items.RED_BANNER, 1));
      setFocused(false);
   }

   @Override
   public void onTabClicked() { NoppesUtil.openGUI(Minecraft.getInstance().player, new GuiLog(1)); }

   @Override
   public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      super.render(graphics, mouseX, mouseY, partialTicks);
      if (this.visible) {
         Minecraft mc = Minecraft.getInstance();
         boolean hovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
         if (hovered) {
            graphics.pose().translate((float)mouseX, (float)(this.getY() + 2), 0.0F);
            int y = 0;
            drawHoveringText(graphics, List.of(this.displayString), -mc.font.width(this.displayString), y, mc.font);
            graphics.pose().translate((float)(-mouseX), (float)(-(this.getY() + 2)), 0.0F);
         }
      }
   }

   @Override
   protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) { }

   protected void drawHoveringText(GuiGraphics graphics, List<Component> list, int x, int y, Font font) {
      if (!list.isEmpty()) {
         RenderSystem.disableDepthTest();
         int k = 0;
         Iterator<Component> var7 = list.iterator();

         int i1;
         while(var7.hasNext()) {
            Component o = var7.next();
            i1 = font.width(o);
            if (i1 > k) {
               k = i1;
            }
         }

          int k2 = y;
         i1 = 8;
         if (list.size() > 1) {
            i1 += 2 + (list.size() - 1) * 10;
         }

         graphics.pose().pushPose();
         graphics.pose().translate(0.0F, 0.0F, 300.0F);
         int j1 = -267386864;
         graphics.fillGradient(x - 3, y - 4, x + k + 3, y - 3, j1, j1);
         graphics.fillGradient(x - 3, y + i1 + 3, x + k + 3, y + i1 + 4, j1, j1);
         graphics.fillGradient(x - 3, y - 3, x + k + 3, y + i1 + 3, j1, j1);
         graphics.fillGradient(x - 4, y - 3, x - 3, y + i1 + 3, j1, j1);
         graphics.fillGradient(x + k + 3, y - 3, x + k + 4, y + i1 + 3, j1, j1);
         int k1 = 1347420415;
         int l1 = (k1 & 16711422) >> 1 | k1 & -16777216;
         graphics.fillGradient(x - 3, y - 3 + 1, x - 3 + 1, y + i1 + 3 - 1, k1, l1);
         graphics.fillGradient(x + k + 2, y - 3 + 1, x + k + 3, y + i1 + 3 - 1, k1, l1);
         graphics.fillGradient(x - 3, y - 3, x + k + 3, y - 3 + 1, k1, k1);
         graphics.fillGradient(x - 3, y + i1 + 2, x + k + 3, y + i1 + 3, l1, l1);

         for(int i2 = 0; i2 < list.size(); ++i2) {
            Component s1 = list.get(i2);
            graphics.drawString(font, s1, x, k2, -1);
            if (i2 == 0) {
               k2 += 2;
            }

            k2 += 10;
         }
         graphics.pose().popPose();
         RenderSystem.enableDepthTest();
      }
   }

}
