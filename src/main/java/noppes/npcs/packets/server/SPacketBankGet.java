package noppes.npcs.packets.server;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.containers.ContainerManageBanks;
import noppes.npcs.controllers.BankController;
import noppes.npcs.controllers.data.Bank;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

import java.util.List;

public class SPacketBankGet extends PacketServerBasic {

   protected static int channelId;
   private int bank;
   private int ceil;

   public SPacketBankGet() { }

   public SPacketBankGet(int bankIn, int ceilIn) {
      bank = bankIn;
      ceil = ceilIn;
   }

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

   @Override
   public List<CustomNpcsPermissions.Permission> getPermission() { return null; }

   @Override
   public void decode(FriendlyByteBuf buf) {
      bank = buf.readInt();
      ceil = buf.readInt();
   }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeInt(bank);
      buf.writeInt(ceil);
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      sendBank(player, BankController.getInstance().getBank(bank), ceil);
      CustomNpcs.debugData.end("Packets");
   }

   public static void sendBank(EntityPlayerMP player, Bank bank, int ceil) {
      Packets.send(player, new PacketGuiData(bank.save()));
      if (player.openContainer instanceof ContainerManageBanks) { ((ContainerManageBanks) player.openContainer).setBank(bank, ceil); }
      player.openContainer.detectAndSendChanges();
   }

}
