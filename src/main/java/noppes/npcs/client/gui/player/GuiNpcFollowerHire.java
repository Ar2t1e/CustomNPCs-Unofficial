package noppes.npcs.client.gui.player;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.gui.util.GuiContainerNPCInterface;
import noppes.npcs.containers.ContainerNPCFollowerHire;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketFollowerHire;
import noppes.npcs.roles.RoleFollower;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.util.Util;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class GuiNpcFollowerHire extends GuiContainerNPCInterface<ContainerNPCFollowerHire> {

   protected final RoleFollower role;

   public GuiNpcFollowerHire(ContainerNPCFollowerHire container, Inventory inv, Component titleIn) {
      super(NoppesUtilServer.getEditingNpc(Minecraft.getInstance().player), container, inv, titleIn);
      setBackground("followerhire.png");

      role = (RoleFollower) npc.role;
   }

   @Override
   public void init() {
      super.init();
      int x = guiLeft + 26;
      int y = guiTop - 7;
      for (int i = 0; i < 3; ++i) {
         if (!role.rentalItems.getItem(i).isEmpty()) {
            addButton(i, x, y += 18, "follower.hire")
                 .setSize(50, 14);
         }
      }
      if (role.rates.containsKey(3) && role.rentalMoney > 0) {
         addButton(3, x, guiTop + 65, "follower.hire")
                 .setSize(50, 14);
      }
   }

   @Override
   public void buttonEvent(GuiButtonNop button) {
      Packets.sendServer(new SPacketFollowerHire(button.id));
      onClose();
   }

   @Override
   protected void renderLabels(@NotNull GuiGraphics graphics, int x, int y) {
      long time = (System.currentTimeMillis() - role.hiredTime) / 50L;
      graphics.drawString(font, Component.translatable("follower.health")
              .append(": " + npc.getHealth() + "/" + npc.getMaxHealth()), 62, 70, CustomNpcResourceListener.DefaultTextColor);
      if (!role.infiniteDays) {
         graphics.drawString(font, Component.translatable("follower.daysleft")
                 .append(" " + Util.instance.ticksToElapsedTime((role.getDays() * 28800L) - time, false, true, false)), 62, 82, CustomNpcResourceListener.DefaultTextColor);
      }
      graphics.drawString(font, Component.translatable("follower.lastday")
              .append(": " + Util.instance.ticksToElapsedTime(time, false, true, false)), 62, 94, CustomNpcResourceListener.DefaultTextColor);
   }

   @Override
   protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
      int l = (width - imageWidth) / 2;
      int i1 = (height - imageHeight) / 2;
      graphics.blit(background, l, i1, 0, 0, imageWidth, imageHeight);
      int index = 0;
      for(int slot = 0; slot < role.inventory.getContainerSize(); ++slot) {
         ItemStack itemstack = role.inventory.getItem(slot);
         if (!NoppesUtilServer.isItemStackNull(itemstack)) {
            int days = 1;
            if (role.rates.containsKey(slot)) {
               days = role.rates.get(slot);
            }
            int yOffset = index * 26;
            int x = guiLeft + 78;
            int y = guiTop + yOffset + 10;
            graphics.renderItem(itemstack, x + 11, y);
            graphics.renderItemDecorations(font, itemstack, x + 11, y);
            Component daysS = Component.empty()
                    .append(" = " + days + " ")
                    .append(Component.translatable(days == 1 ? "follower.day": "follower.days"));
            graphics.drawString(font, daysS, x + 27, y + 4,
                    CustomNpcResourceListener.DefaultTextColor);
            if (isMouseHover(mouseX, mouseY, x - guiLeft + 11, y - guiTop, 16, 16)) {
               graphics.renderTooltip(font, itemstack, mouseX, mouseY);
            }
            ++index;
         }
      }
      if (role.rates.containsKey(3) && role.rentalMoney > 0) {
         int days = role.rates.get(3);
         Component daysS = Component.empty()
                 .append(Util.instance.getTextReducedNumber(role.rentalMoney, true, true, false))
                 .append(" " + CustomNpcs.displayCurrencies + " = " + days + " ")
                 .append(Component.translatable(days == 1 ? "follower.day": "follower.days"));
         graphics.drawString(font, daysS, guiLeft + 90, guiTop + 68, CustomNpcResourceListener.DefaultTextColor);
      }
   }

   @Override
   public void render(@Nonnull GuiGraphics graphics,  int mouseX, int mouseY, float partialTicks) {
      for (int i = 0; i < 3; ++i) {
         if (getButton(i) != null) {
            getButton(i).setIsEnabled(player.isCreative() || Util.instance.canRemoveItems(player.getInventory().items, role.rentalItems.getItem(i), false, false));
         }
      }
      if (getButton(3) != null) {
         getButton(3).setIsEnabled(player.isCreative() || CustomNpcs.proxy.getPlayerData(player).game.getMoney() >= role.rentalMoney);
      }
      for (int i = 0; i < 4; ++i) {
         if (getButton(i) != null && getButton(i).isHoveredOrFocused()) {
            List<Component> hover = new ArrayList<>();
            hover.add(Component.translatable("follower.hover.hire.info"));
            if (role.disableGui) { hover.add(Component.translatable("follower.hover.disable.gui").withStyle(ChatFormatting.GRAY)); }
            if (role.infiniteDays) { hover.add(Component.translatable("follower.hover.infinite")); }
            setHoverText(hover);
         }
      }
      super.render(graphics, mouseX, mouseY, partialTicks);
   }

}
