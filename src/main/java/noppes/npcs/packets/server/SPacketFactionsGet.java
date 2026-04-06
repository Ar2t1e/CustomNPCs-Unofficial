package noppes.npcs.packets.server;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketFactionsGet extends PacketServerBasic {

   protected static int channelId;

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   public static void encode(SPacketFactionsGet ignoredMsg, FriendlyByteBuf ignoredBuf) { }

   public static SPacketFactionsGet decode(FriendlyByteBuf ignoredBuf) { return new SPacketFactionsGet(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      sendFactionDataAll(player);
      CustomNpcs.debugData.end("Packets");
   }

   public static void sendFactionDataAll(ServerPlayer player) {
      Map<String, Integer> map = new HashMap<>();
      for (Faction faction : FactionController.instance.factions.values()) {
         map.put(faction.name, faction.id);
      }
      NoppesUtilServer.sendScrollData(player, map);
   }

}
