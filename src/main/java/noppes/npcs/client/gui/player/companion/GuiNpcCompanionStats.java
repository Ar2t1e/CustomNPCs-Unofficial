package noppes.npcs.client.gui.player.companion;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.constants.EnumCompanionJobs;
import noppes.npcs.constants.EnumCompanionTalent;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketCompanionOpenInv;
import noppes.npcs.packets.server.SPacketNpcRoleGet;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiMenuTopButton;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;

public class GuiNpcCompanionStats extends GuiNPCInterface implements IGuiData {

   public static void addTopMenu(RoleCompanion role, Screen screen, int active) {
      GuiMenuTopButton button;
      if (screen instanceof IGuiInterface gui) {
         button = gui.addTopButton(1, gui.getX() + 4, gui.getY() - 27,
                         Component.translatable("menu.stats"), new ItemStack(Items.BOOK))
                 .setIsEnabled(active == 1);
         button = gui.addTopButton(2, button.getX() + button.getWidth(), button.getY(),
                         Component.translatable("companion.talent"), new ItemStack(Items.NETHER_STAR))
                 .setIsEnabled(active == 2);
         if (role.hasInv()) {
            button = gui.addTopButton(3, button.getX() + button.getWidth(), button.getY(),
                            Component.translatable("inv.inventory"), new ItemStack(Blocks.CHEST))
                    .setIsEnabled(active == 3);
         }
         if (role.job.getType() != EnumCompanionJobs.NONE) {
            gui.addTopButton(4, button.getX() + button.getWidth(), button.getY(),
                            Component.translatable("job.name"), new ItemStack(Items.CARROT))
                    .setIsEnabled(active == 4);
         }
      }
   }

   public static final ResourceLocation GUI_ICONS_LOCATION = new ResourceLocation("minecraft", "textures/gui/icons.png");
   protected final RoleCompanion role;
   protected boolean isEating = false;

   public GuiNpcCompanionStats(EntityNPCInterface npc) {
      super(npc);
      setBackground("companion.png");
      imageWidth = 171;
      imageHeight = 166;

      role = (RoleCompanion)npc.role;
      Packets.sendServer(new SPacketNpcRoleGet());
   }

   @Override
   public void init() {
      super.init();
      int x = guiLeft + 4;
      int y = guiTop + 10;
      addLabel(0, x, y, Component.translatable("gui.name").append(": ")
              .append(npc.display.getName()));
      addLabel(1, x, y += 12, Component.translatable("companion.owner").append(": ")
              .append(role.ownerName));
      addLabel(2, x, y += 12, Component.translatable("companion.age").append(": ").append("" + role.ticksActive / 18000L)
              .append(" (").append(role.stage.name).append(")"));
      addLabel(3, x, y += 12, Component.translatable("companion.strength").append(": ")
              .append("" + npc.stats.melee.getStrength()));
      addLabel(4, x, y += 12, Component.translatable("companion.level").append(": ")
              .append("" + role.getTotalLevel()));
      addLabel(5, x, y + 12, Component.translatable("job.name").append(": ")
              .append(Component.translatable("gui.none")));
      addTopMenu(role, this, 1);
   }

   @Override
   public void buttonEvent(GuiButtonNop button) {
      switch (button.id) {
         case 2: {
            NoppesUtilServer.setEditingNpc(player, npc);
            CustomNpcs.proxy.openGui(npc, EnumGuiType.CompanionTalent, null);
            break;
         }
         case 3: Packets.sendServer(new SPacketCompanionOpenInv()); break;
      }
   }

   @Override
   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      super.render(graphics, mouseX, mouseY, partialTicks);
      if (isEating && !role.isEating()) { Packets.sendServer(new SPacketNpcRoleGet()); }
      isEating = role.isEating();
      super.drawNpc(graphics, 34, 150);
      drawHealth(graphics, guiTop + 88);
   }

   private void drawHealth(GuiGraphics graphics, int y) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, GUI_ICONS_LOCATION);
      int max = role.getTotalArmorValue();
      int k;
      if (role.talents.containsKey(EnumCompanionTalent.ARMOR) || max > 0) {
         for(k = 0; k < 10; ++k) {
            int x = guiLeft + 66 + k * 10;
            if (k * 2 + 1 < max) { graphics.blit(GUI_ICONS_LOCATION, x, y, 34, 9, 9, 9); }
            if (k * 2 + 1 == max) { graphics.blit(GUI_ICONS_LOCATION, x, y, 25, 9, 9, 9); }
            if (k * 2 + 1 > max) { graphics.blit(GUI_ICONS_LOCATION, x, y, 16, 9, 9, 9); }
         }
         y += 10;
      }
      max = Mth.ceil(npc.getMaxHealth());
      k = (int) npc.getHealth();
      float scale;
      if (max > 40) {
         scale = (float)max / 40.0F;
         k = (int)((float)k / scale);
         max = 40;
      }
      int i;
      int x;
      for(i = 0; i < max; ++i) {
         x = guiLeft + 66 + i % 20 * 5;
         int offset = i / 20 * 10;
         graphics.blit(GUI_ICONS_LOCATION, x, y + offset, 52 + i % 2 * 5, 9, i % 2 == 1 ? 4 : 5, 9);
         if (k > i) {
            graphics.blit(GUI_ICONS_LOCATION, x, y + offset, 52 + i % 2 * 5, 0, i % 2 == 1 ? 4 : 5, 9);
         }
      }
      k = role.foodstats.getFoodLevel();
      y += 10;
      if (max > 20) {
         y += 10;
      }
      for(i = 0; i < 20; ++i) {
         x = guiLeft + 66 + i % 20 * 5;
         graphics.blit(GUI_ICONS_LOCATION, x, y, 16 + i % 2 * 5, 27, i % 2 == 1 ? 4 : 5, 9);
         if (k > i) {
            graphics.blit(GUI_ICONS_LOCATION, x, y, 52 + i % 2 * 5, 27, i % 2 == 1 ? 4 : 5, 9);
         }
      }
   }

   @Override
   public void setGuiData(CompoundTag compound) { role.load(compound); }

}
