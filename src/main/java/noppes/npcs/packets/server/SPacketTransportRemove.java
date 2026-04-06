package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.TransportController;
import noppes.npcs.controllers.data.TransportLocation;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketTransportRemove extends PacketServerBasic {

   protected static int channelId;
   private final int id;

   public SPacketTransportRemove(int idIn) { id = idIn; }

   @Override
   public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.GLOBAL_TRANSPORT; }

   public static void encode(SPacketTransportRemove msg, FriendlyByteBuf buf) { buf.writeInt(msg.id); }

   public static SPacketTransportRemove decode(FriendlyByteBuf buf) { return new SPacketTransportRemove(buf.readInt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      TransportLocation loc = TransportController.getInstance().removeLocation(id);
      if (loc != null) { SPacketTransportGet.sendTransportData(player, loc.category.id); }
      CustomNpcs.debugData.end("Packets");
   }
}
