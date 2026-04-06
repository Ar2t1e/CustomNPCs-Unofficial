package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.TransportController;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketTransportCategoryRemove extends PacketServerBasic {

   protected static int channelId;
   private int id;

   public SPacketTransportCategoryRemove() { }

   public SPacketTransportCategoryRemove(int idIn) { id = idIn; }

   @Override
   public CustomNpcsPermissions.Permission getPermission() { return CustomNpcsPermissions.GLOBAL_TRANSPORT; }

   @Override
   public void encode(FriendlyByteBuf buf) { buf.writeInt(id); }

   @Override
   public void decode(FriendlyByteBuf buf) { id = buf.readInt(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      TransportController.getInstance().removeCategory(id);
      SPacketTransportCategoriesGet.sendTransportCategoryData(player);
      CustomNpcs.debugData.start("Packets");
   }

}
