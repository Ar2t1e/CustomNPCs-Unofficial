package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.text.TextFormatting;
import noppes.npcs.*;
import noppes.npcs.containers.ContainerNPCBank;
import noppes.npcs.containers.NpcMiscInventory;
import noppes.npcs.controllers.data.Bank;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.util.Util;

import java.util.HashMap;
import java.util.Map;

public class SPacketBankUpgrade extends PacketServerBasic {

   protected static int channelId;
   private int bankId;
   private int ceil;
   private int size;
   private int scrollY;
   private int ceilPos;

   public SPacketBankUpgrade() { }

   public SPacketBankUpgrade(int bankIdIn, int ceilIn, int sizeIn, int scrollYIn, int ceilPosIn) {
      bankId = bankIdIn;
      ceil = ceilIn;
      size = sizeIn;
      scrollY = scrollYIn;
      ceilPos = ceilPosIn;
   }

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeInt(bankId);
      buf.writeInt(ceil);
      buf.writeInt(size);
      buf.writeInt(scrollY);
      buf.writeInt(ceilPos);
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      bankId = buf.readInt();
      ceil = buf.readInt();
      size = buf.readInt();
      scrollY = buf.readInt();
      ceilPos = buf.readInt();
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (player.openContainer instanceof ContainerNPCBank) {
         ContainerNPCBank cont = (ContainerNPCBank) player.openContainer;
         boolean isOwner = player.isCreative() || !cont.data.bank.isPublic || cont.data.bank.owner.isEmpty() || player.getName().equals(cont.data.bank.owner);
         boolean update = false;
         if (!isOwner) {
            player.sendMessage(Component.translatable("bank.hover.changed.false").withStyle(TextFormatting.RED));
         }
         else {
            NpcMiscInventory inv = cont.data.get(ceil);
            if (cont.data.bank.ceilSettings.containsKey(ceil) && inv != null) {
               PlayerData data = PlayerData.get(player);
               Bank.CeilSettings cs = cont.data.bank.ceilSettings.get(ceil);
               if (inv.getSizeInventory() == 0) {
                  NpcMiscInventory invPre = cont.data.get(ceil - 1);
                  boolean open = player.isCreative() || ceil == 0 ||
                          cont.data.bank.ceilSettings.get(ceil - 1).isFree ||
                          (invPre != null && invPre.getSizeInventory() == cont.data.bank.ceilSettings.get(ceil - 1).maxCells);
                  if (open & !player.isCreative()) {
                     if (!cs.openStack.isEmpty()) {
                        Map<ItemStack, Integer> items = new HashMap<>();
                        items.put(cs.openStack, cs.openStack.getCount());
                        open = Util.instance.canRemoveItems(player.inventory.mainInventory, items, false, false);
                        if (!open) { player.sendMessage(Component.translatable("hover.operation.not.items")); }
                     }
                     if (cs.openMoney > 0) {
                        if (open) { open = data.game.getMoney() >= cs.openMoney; }
                        if (!open) { player.sendMessage(Component.translatable("hover.operation.not.money")); }
                     }
                     if (cs.openDonat > 0) {
                        if (open) { open = data.game.getDonat() >= cs.openDonat; }
                        if (!open) { player.sendMessage(Component.translatable("hover.operation.not.donat")); }
                     }
                     if (open) {
                        if (!cs.openStack.isEmpty()) { Util.instance.removeItem(player, cs.openStack, false, false); }
                        if (cs.openMoney > 0) { data.game.addMoney(-cs.openMoney); }
                        if (cs.openDonat > 0) { data.game.addDonat(-cs.openDonat); }
                     }
                  }
                  update = open && cont.data.openNew(ceil);
               } // open
               else {
                  boolean upgrade = true;
                  if (!player.isCreative()) {
                     if (!cs.upgradeStack.isEmpty()) {
                        Map<ItemStack, Integer> items = new HashMap<>();
                        items.put(cs.upgradeStack, cs.upgradeStack.getCount() * size);
                        upgrade = Util.instance.canRemoveItems(player.inventory.mainInventory, items, false, false);
                        if (!upgrade) { player.sendMessage(Component.translatable("hover.operation.not.items")); }
                     }
                     if (cs.upgradeMoney > 0) {
                        if (upgrade) { upgrade = data.game.getMoney() >= (long) cs.upgradeMoney * (long) size; }
                        if (!upgrade) { player.sendMessage(Component.translatable("hover.operation.not.money")); }
                     }
                     if (cs.upgradeDonat > 0) {
                        if (upgrade) { upgrade = data.game.getDonat() >= cs.upgradeDonat; }
                        if (!upgrade) { player.sendMessage(Component.translatable("hover.operation.not.donat")); }
                     }
                     if (upgrade) {
                        if (!cs.openStack.isEmpty()) { Util.instance.removeItem(player, cs.upgradeStack, cs.upgradeStack.getCount() * size, false, false); }
                        if (cs.upgradeMoney > 0) { data.game.addMoney((long) -cs.upgradeMoney * (long) size); }
                        if (cs.upgradeDonat > 0) { data.game.addDonat((long) -cs.upgradeDonat * (long) size); }
                     }
                  }
                  if (upgrade) {
                     inv.setNewSize(Math.min(cs.maxCells, inv.getSizeInventory() + size));
                     cont.data.setChanged();
                     update = true;
                  }
               } // upgrade
            }
         }
         if (!update) { cont.data.openToPlayer(player, ceil, scrollY, ceilPos, size); }
      }
      CustomNpcs.debugData.end("Packets");
   }

}
