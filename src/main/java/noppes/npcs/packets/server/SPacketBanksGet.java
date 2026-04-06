package noppes.npcs.packets.server;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.BankController;
import noppes.npcs.controllers.data.Bank;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketBanksGet extends PacketServerBasic {

   protected static int channelId;

   public static void encode(SPacketBanksGet ignoredMsg, FriendlyByteBuf ignoredBuf) { }

   public static SPacketBanksGet decode(FriendlyByteBuf ignoredBuf) { return new SPacketBanksGet(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      sendBankDataAll(player);
      CustomNpcs.debugData.end("Packets");
   }

   public static void sendBankDataAll(ServerPlayer player) {
      Map<String, Integer> map = new HashMap<>();
      for (Bank bank : BankController.getInstance().getBanks()) { map.put(bank.name, bank.id); }
      NoppesUtilServer.sendScrollData(player, map);
   }

}
