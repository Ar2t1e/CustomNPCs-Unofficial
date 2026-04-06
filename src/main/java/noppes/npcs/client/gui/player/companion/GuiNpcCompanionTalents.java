package noppes.npcs.client.gui.player.companion;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.constants.EnumCompanionTalent;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketCompanionOpenInv;
import noppes.npcs.packets.server.SPacketCompanionTalentExp;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;

public class GuiNpcCompanionTalents extends GuiNPCInterface {

   protected final Map<Integer, GuiNpcCompanionTalents.GuiTalent> talents = new HashMap<>();
   protected final RoleCompanion role;
   protected GuiButtonNop selected;
   protected long lastPressedTime = 0L;
   protected long startPressedTime = 0L;

   public GuiNpcCompanionTalents(EntityNPCInterface npc) {
      super(npc);
      setBackground("companion_empty.png");
      imageWidth = 171;
      imageHeight = 166;

      role = (RoleCompanion) npc.role;
   }

   @Override
   public void init() {
      super.init();
      talents.clear();
      addLabel(0, guiLeft + 4, guiTop + 10, Component.translatable("quest.exp").append(": "));
      GuiNpcCompanionStats.addTopMenu(role, this, 2);
      int i = 0;
      for (EnumCompanionTalent e : role.talents.keySet()) { addTalent(i++, e); }
   }

   @Override
   public void buttonEvent(GuiButtonNop button) {
      switch (button.id) {
         case 1: {
            NoppesUtilServer.setEditingNpc(player, npc);
            CustomNpcs.proxy.openGui(npc, EnumGuiType.Companion, null);
            break;
         }
         case 3: Packets.sendServer(new SPacketCompanionOpenInv()); break;
         default: {
            if (button.id >= 10) {
               if (minecraft == null) { minecraft = Minecraft.getInstance(); }
               selected = button;
               lastPressedTime = startPressedTime = (minecraft.level == null ? 0L : minecraft.level.getDayTime());
               addExperience(1);
            }
            break;
         }
      }
   }

   @Override
   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      if (minecraft == null) { minecraft = Minecraft.getInstance(); }
      super.render(graphics, mouseX, mouseY, partialTicks);
      long time = (minecraft.level == null ? 0L : minecraft.level.getDayTime());
      if (selected != null && time - startPressedTime > 4L && lastPressedTime < time && time % 4L == 0L) {
         if (selected.mouseClicked(mouseX, mouseY, 0)) {
            lastPressedTime = minecraft.level.getDayTime();
            if (lastPressedTime - startPressedTime < 20L) { addExperience(1); }
            else if (lastPressedTime - startPressedTime < 40L) { addExperience(2); }
            else if (lastPressedTime - startPressedTime < 60L) { addExperience(4); }
            else if (lastPressedTime - startPressedTime < 90L) { addExperience(8); }
            else if (lastPressedTime - startPressedTime < 140L) { addExperience(14); }
            else { addExperience(28); }
         } else {
            lastPressedTime = 0L;
            selected = null;
         }
      }
      graphics.blit(GuiNpcCompanionStats.GUI_ICONS_LOCATION, guiLeft + 4, guiTop + 20, 10, 64, 162, 5);
      if (role.currentExp > 0) {
         float v = (float) role.currentExp / (float)role.getMaxExp();
         if (v > 1.0F) { v = 1.0F; }
         graphics.blit(GuiNpcCompanionStats.GUI_ICONS_LOCATION, guiLeft + 4, guiTop + 20, 10, 69, (int)(v * 162.0F), 5);
      }
      String s = role.currentExp + "\\" + role.getMaxExp();
      graphics.drawString(minecraft.font, s, guiLeft + imageWidth / 2 - minecraft.font.width(s) / 2, guiTop + 10, CustomNpcResourceListener.DefaultTextColor);
      for (GuiTalent talent : talents.values()) { talent.render(graphics, mouseX, mouseY, partialTicks); }
   }

   private void addTalent(int i, EnumCompanionTalent talent) {
      if (minecraft == null) { minecraft = Minecraft.getInstance(); }
      int y = guiTop + 28 + i / 2 * 26;
      int x = guiLeft + 4 + i % 2 * 84;
      GuiNpcCompanionTalents.GuiTalent gui = new GuiNpcCompanionTalents.GuiTalent(role, talent, x, y);
      gui.init(minecraft, width, height);
      talents.put(i, gui);
      if (role.getTalentLevel(talent) < 5) {
         addButton(i + 10, x + 26, y, "+")
                 .setSize(14, 14);
         y += 8;
      }
      addLabel(i, x + 26, y + 8, role.talents.get(talent) + "/" + role.getNextLevel(talent));
   }

   private void addExperience(int exp) {
      EnumCompanionTalent talent = talents.get(selected.id - 10).talent;
      if (role.canAddExp(-exp) || role.currentExp > 0) {
         if (exp > role.currentExp) { exp = role.currentExp; }
         Packets.sendServer(new SPacketCompanionTalentExp(talent, exp));
         role.talents.put(talent, role.talents.get(talent) + exp);
         role.addExp(-exp);
         getLabel(selected.id - 10).setMessage(Component.literal(role.talents.get(talent) + "/" + role.getNextLevel(talent)));
      }
   }

   public static class GuiTalent extends Screen {

      private final EnumCompanionTalent talent;
      private final int x;
      private final int y;
      private final RoleCompanion role;
      private static final ResourceLocation resource = new ResourceLocation(CustomNpcs.MODID, "textures/gui/talent.png");

      public GuiTalent(RoleCompanion roleIn, EnumCompanionTalent talentIn, int xIn, int yIn) {
         super(Component.empty());
         talent = talentIn;
         x = xIn;
         y = yIn;
         role = roleIn;
      }

      @SuppressWarnings("all")
      public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
         Minecraft mc = Minecraft.getInstance();
         PoseStack matrixStack = graphics.pose();
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.setShaderTexture(0, resource);
         ItemStack item = talent.item;
         if (item.getItem() == null) { item = new ItemStack(Blocks.DIRT); }

         matrixStack.pushPose();
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.enableBlend();
         boolean hover = x < mouseX && x + 24 > mouseX && y < mouseY && y + 24 > mouseY;
         graphics.blit(resource, x, y, 0, hover ? 24 : 0, 24, 24);
         graphics.pose().pushPose();
         graphics.pose().translate(0.0F, 0.0F, 100.0F);
         graphics.renderItem(item, x + 4, y + 4);
         graphics.renderItemDecorations(mc.font, item, x + 4, y + 4);
         matrixStack.translate(0.0F, 0.0F, 200.0F);
         graphics.drawCenteredString(mc.font, "" + role.getTalentLevel(talent), x + 20, y + 16, new Color(0xFFFFFF).getRGB());
         graphics.pose().popPose();
         matrixStack.popPose();
      }
   }

}
