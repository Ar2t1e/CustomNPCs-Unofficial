package noppes.npcs.packets.server;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.TransportController;
import noppes.npcs.controllers.data.TransportCategory;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketTransportCategoriesGet extends PacketServerBasic {

   protected static int channelId;

   @Override
   public CustomNpcsPermissions.Permission getPermission() { return CustomNpcsPermissions.GLOBAL_TRANSPORT; }

   @Override
   public void encode(FriendlyByteBuf buf) { }

   @Override
   public void decode(FriendlyByteBuf buf) { }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      sendTransportCategoryData(player);
      CustomNpcs.debugData.end("Packets");
   }

   public static void sendTransportCategoryData(EntityPlayerMP player) {
      HashMap<String, Integer> map = new HashMap<>();
      for (TransportCategory category : TransportController.getInstance().categories.values()) { map.put(category.title, category.id); }
      NoppesUtilServer.sendScrollData(player, map);
   }

}
