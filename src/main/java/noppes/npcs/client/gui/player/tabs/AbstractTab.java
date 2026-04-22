package noppes.npcs.client.gui.player.tabs;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomNpcs;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractTab extends AbstractButton {

   protected ResourceLocation texture = new ResourceLocation(CustomNpcs.MODID, "textures/gui/tabs.png");
   protected ItemStack renderStack;
   protected int id;

   protected Screen screen;
   protected int guiLeft = 0;
   protected int guiTop = 0;

   public AbstractTab(int idIn, int posX, int posY, ItemStack renderStackIn) {
      super(posX, posY, 28, 32, Component.empty());
      renderStack = renderStackIn;
      id = idIn;
   }

   public AbstractTab init(Screen screenIn) {
      screen = screenIn;
      guiLeft = (screenIn.width - 176) / 2 + id * 28;
      guiTop = (screenIn.height - 166) / 2 - 28;
      return this;
   }

   public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      if (visible) {
         int x = guiLeft;
         int y = guiTop;
         if (screen instanceof InventoryScreen inv && inv.getRecipeBookComponent().isVisible()) {
            x += 77;
         }
         if (getX() != x) { setX(x); }
         if (getY() != y) { setY(y); }
         isHovered = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         Minecraft mc = Minecraft.getInstance();
         int yTexPos = isFocused() || isHovered ? 32 : 3;
         int ySize = isFocused() || isHovered ? 32 : 29;
         int xOffset = id == 0 ? 0 : 1;
         RenderSystem.setShaderTexture(0, texture);
         graphics.blit(texture, getX(), getY(), xOffset * 28, yTexPos, 28, ySize);
         if (!isFocused() && isHovered) {
            graphics.fill(getX() + 3, getY() + 3, getX() + width - 3, getY() + height - 1, 0xF07E88BF);
         }
         graphics.pose().pushPose();
         graphics.pose().translate(0.0F, 0.0F, 30.0F);
         graphics.renderItem(renderStack, getX() + 6, getY() + 8);
         graphics.renderItemDecorations(mc.font, renderStack, getX() + 6, getY() + 8, null);
         graphics.pose().popPose();
      }
   }

   @Override
   public void onClick(double mouseX, double mouseY) { onTabClicked(); }

   @Override
   public void onPress() { }

   public abstract void onTabClicked();

}
