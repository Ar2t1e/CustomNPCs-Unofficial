package noppes.npcs.packets.server;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.BankController;
import noppes.npcs.controllers.data.Bank;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketBanksGet extends PacketServerBasic {

   protected static int channelId;

   @Override
   public void encode(FriendlyByteBuf buf) { }

   @Override
   public void decode(FriendlyByteBuf buf) { }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      sendBankDataAll(player);
      CustomNpcs.debugData.end("Packets");
   }

   public static void sendBankDataAll(EntityPlayerMP player) {
      Map<String, Integer> map = new HashMap<>();
      for (Bank bank : BankController.getInstance().getBanks()) { map.put(bank.name, bank.id); }
      NoppesUtilServer.sendScrollData(player, map);
   }

}
