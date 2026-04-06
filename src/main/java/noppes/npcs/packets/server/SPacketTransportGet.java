package noppes.npcs.packets.server;

import java.util.HashMap;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.TransportController;
import noppes.npcs.controllers.data.TransportCategory;
import noppes.npcs.controllers.data.TransportLocation;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketTransportGet extends PacketServerBasic {

   protected static int channelId;
   private final int id;

   public SPacketTransportGet(int idIn) { id = idIn; }

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   public static void encode(SPacketTransportGet msg, FriendlyByteBuf buf) { buf.writeInt(msg.id); }

   public static SPacketTransportGet decode(FriendlyByteBuf buf) { return new SPacketTransportGet(buf.readInt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      sendTransportData(player, id);
      CustomNpcs.debugData.end("Packets");
   }

   public static void sendTransportData(ServerPlayer player, int categoryid) {
      TransportCategory category = TransportController.getInstance().getCategory(null, categoryid);
      if (category.id > -1) {
         HashMap<String, Integer> map = new HashMap<>();
         for (TransportLocation transport : category.locations.values()) {
            map.put(transport.name, transport.id);
         }
         NoppesUtilServer.sendScrollData(player, map);
      }
   }

}
