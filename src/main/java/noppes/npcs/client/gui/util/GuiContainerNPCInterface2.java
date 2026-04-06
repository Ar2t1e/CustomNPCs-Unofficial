package noppes.npcs.client.gui.util;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.gui.INpcMenuGui;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.util.LogWriter;

import javax.annotation.Nonnull;

public abstract class GuiContainerNPCInterface2<T extends AbstractContainerMenu> extends GuiContainerNPCInterface<T>
        implements INpcMenuGui {

   protected final @Nonnull GuiNpcMenu menuTabs;
   protected final ResourceLocation rightBackground = getResource("menubg.png");
   protected EnumGuiType backGui = EnumGuiType.MainMenuDisplay;
   public int menuYOffset;

   public GuiContainerNPCInterface2(EntityNPCInterface npc, T cont, Inventory inv, Component titleIn) {
      this(npc, cont, inv, titleIn, -1);
   }

   public GuiContainerNPCInterface2(EntityNPCInterface npc, T cont, Inventory inv, Component titleIn, int activeMenu) {
      super(npc, cont, inv, titleIn);
      drawDefaultBackground = false;
      menuYOffset = 0;
      imageWidth = 420;
      menuTabs = new GuiNpcMenu(this, activeMenu, npc);
   }

   @Override
    public void init() {
      super.init();
      menuTabs.init(guiLeft, guiTop + menuYOffset, imageWidth);
   }

   @Override
   public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
      if (!hasSubGui() && menuTabs.mouseClicked(mouseX, mouseY, mouseButton)) { return true; }
      return super.mouseClicked(mouseX, mouseY, mouseButton);
   }

   public void delete() {
      if (npc != null) { npc.delete(); }
      setScreen(null);
      if (minecraft != null) { minecraft.mouseHandler.grabMouse(); }
   }

   @Override
   protected void renderBg(@Nonnull GuiGraphics graphics, float partialTicks, int x, int y) {
      renderBackground(graphics);
      graphics.blit(background != null ? background : rightBackground, guiLeft, guiTop, 0, 0, 256, 256);
      graphics.blit(rightBackground, guiLeft + imageWidth - 200, guiTop, 56, 0, 200, 220);
      if (npc != null) { menuTabs.drawElements(graphics, x, y, partialTicks); }
   }

   // New from Unofficial (BetaZavr)
   @Override
   public void onClose() {
      if (menuTabs.activeMenu != 1) {
         menuTabs.save();
         if (backGui != null && (npc != null || backGui != EnumGuiType.MainMenuDisplay)) {
            NoppesUtil.requestOpenGUI(backGui);
            return;
         }
      }
      super.onClose();
      if (menuTabs.activeMenu != 1 && npc == null && backGui == EnumGuiType.MainMenuDisplay) {
         CustomNpcs.proxy.openGui(player, EnumGuiType.NpcRemote);
      }
   }

   @Override
   public void setMenuData(boolean display, boolean stats, boolean ai, boolean inventory, boolean advanced) {
      menuTabs.permissions[0] = display;
      menuTabs.permissions[1] = stats;
      menuTabs.permissions[2] = ai;
      menuTabs.permissions[3] = inventory;
      menuTabs.permissions[4] = advanced;
   }

}
