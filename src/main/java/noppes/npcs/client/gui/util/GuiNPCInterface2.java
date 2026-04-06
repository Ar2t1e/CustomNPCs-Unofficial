package noppes.npcs.client.gui.util;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.gui.INpcMenuGui;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.mainmenu.GuiNpcGlobalMainMenu;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.util.LogWriter;

import javax.annotation.Nonnull;

// Menu GUI
public abstract class GuiNPCInterface2 extends GuiNPCInterface implements INpcMenuGui {

   protected final ResourceLocation background = getResource("menubg.png");
   protected final @Nonnull GuiNpcMenu menuTabs;
   public EnumGuiType backGui = EnumGuiType.MainMenuDisplay;

   public GuiNPCInterface2(EntityNPCInterface npc) { this(npc, -1); }

   public GuiNPCInterface2(EntityNPCInterface npc, int activeMenu) {
      super(npc);
      imageWidth = 420;
      imageHeight = 200;
      menuTabs = new GuiNpcMenu(this, activeMenu, npc);
   }

   @Override
   public void init() {
      super.init();
      if (!hasSubGui()) { menuTabs.init(guiLeft, guiTop, imageWidth); }
   }

   @Override
   public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
      return !hasSubGui() && menuTabs.mouseClicked(mouseX, mouseY, mouseButton) || super.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      if (drawDefaultBackground) { renderBackground(graphics); }
      if (this instanceof GuiNpcGlobalMainMenu && npc == null) {
         graphics.blit(background, guiLeft + 70, guiTop, 0, 0, 142, 220);
         graphics.blit(background, guiLeft + imageWidth - 208, guiTop, 113, 0, 143, 220);
      }
      else {
         graphics.blit(background, guiLeft, guiTop, 0, 0, 200, 220);
         graphics.blit(background, guiLeft + imageWidth - 230, guiTop, 26, 0, 230, 220);
      }
      int x = mouseX;
      int y = mouseY;
      if (hasSubGui()) { y = (x = 0); }
      if (npc != null) { menuTabs.drawElements(graphics, x, y, partialTicks); }
      boolean bo = drawDefaultBackground;
      drawDefaultBackground = false;
      super.render(graphics, mouseX, mouseY, partialTicks);
      drawDefaultBackground = bo;
   }

   @Override
   public abstract void save();

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
