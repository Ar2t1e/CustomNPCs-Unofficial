package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.TransportController;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.shared.common.util.LogWriter;

public class SPacketTransportCategoriesGet extends PacketServerBasic {

   protected static int channelId;

   @Override
   public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.GLOBAL_TRANSPORT; }

   public static void encode(SPacketTransportCategoriesGet ignoredMsg, FriendlyByteBuf ignoredBuf) {}

   public static SPacketTransportCategoriesGet decode(FriendlyByteBuf ignoredBuf) { return new SPacketTransportCategoriesGet(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      TransportController.getInstance().sendTo(player);
      Packets.send(player, new PacketGuiData(new CompoundTag()));
      CustomNpcs.debugData.end("Packets");
   }

}
