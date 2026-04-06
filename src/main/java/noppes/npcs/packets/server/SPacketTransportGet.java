package noppes.npcs.packets.server;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.TransportController;
import noppes.npcs.controllers.data.TransportCategory;
import noppes.npcs.controllers.data.TransportLocation;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketTransportGet extends PacketServerBasic {

   protected static int channelId;
   private int id;

   public SPacketTransportGet() { }

   public SPacketTransportGet(int idIn) { id = idIn; }

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   @Override
   public void encode(FriendlyByteBuf buf) { buf.writeInt(id); }

   @Override
   public void decode(FriendlyByteBuf buf) { id = buf.readInt(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      sendTransportData(player, id);
      CustomNpcs.debugData.end("Packets");
   }

   public static void sendTransportData(EntityPlayerMP player, int categoryid) {
      TransportCategory category = TransportController.getInstance().categories.get(categoryid);
      if (category != null) {
         HashMap<String, Integer> map = new HashMap<>();
         for (TransportLocation transport : category.locations.values()) { map.put(transport.name, transport.id); }
         NoppesUtilServer.sendScrollData(player, map);
      }
   }

}
