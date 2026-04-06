package noppes.npcs.client.gui.player.companion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.client.gui.util.GuiContainerNPCInterface;
import noppes.npcs.constants.EnumCompanionTalent;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.containers.ContainerNPCCompanion;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import org.jetbrains.annotations.NotNull;

public class GuiNpcCompanionInv extends GuiContainerNPCInterface<ContainerNPCCompanion> {

   protected final ResourceLocation resource = getResource("companioninv.png");
   protected final RoleCompanion role;

   public GuiNpcCompanionInv(ContainerNPCCompanion container, Inventory inv, Component titleIn) {
      super(NoppesUtilServer.getEditingNpc(Minecraft.getInstance().player), container, inv, titleIn);
      imageWidth = 171;
      imageHeight = 166;

      role = (RoleCompanion) npc.role;
   }

   @Override
   public void init() {
      super.init();
      GuiNpcCompanionStats.addTopMenu(role, this, 3);
   }

   @Override
   public void buttonEvent(GuiButtonNop button) {
      NoppesUtilServer.setEditingNpc(player, npc);
      switch (button.id) {
         case 1: CustomNpcs.proxy.openGui(npc, EnumGuiType.Companion, null); break;
         case 2: CustomNpcs.proxy.openGui(npc, EnumGuiType.CompanionTalent, null); break;
      }
   }

   @Override
   protected void renderBg(@NotNull GuiGraphics graphics, float f, int xMouse, int yMouse) {
      super.renderBackground(graphics);
      graphics.blit(resource, guiLeft, guiTop, 0, 0, imageWidth, imageHeight);
      int size;
      if (role.getTalentLevel(EnumCompanionTalent.ARMOR) > 0) {
         for(size = 0; size < 4; ++size) {
            graphics.blit(resource, guiLeft + 5, guiTop + 7 + size * 18, 0, 0, 18, 18);
         }
      }

      if (role.getTalentLevel(EnumCompanionTalent.SWORD) > 0) {
         graphics.blit(resource, guiLeft + 78, guiTop + 16, 0, npc.inventory.weapons.get(0) == null ? 18 : 0, 18, 18);
      }

      if (role.talents.containsKey(EnumCompanionTalent.INVENTORY)) {
         size = (role.getTalentLevel(EnumCompanionTalent.INVENTORY) + 1) * 2;
         for(int i = 0; i < size; ++i) {
            graphics.blit(resource, guiLeft + 113 + i % 3 * 18, guiTop + 7 + i / 3 * 18, 0, 0, 18, 18);
         }
      }

   }

   @Override
   public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      super.render(graphics, mouseX, mouseY, partialTicks);
      super.drawNpc(graphics, 52, 70);
   }

}
