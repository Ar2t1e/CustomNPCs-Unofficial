package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketNpcDialogsGet extends PacketServerBasic {

   protected static int channelId;

   @Override
   public boolean requiresNpc() { return true; }

   @Override
   public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.NPC_GUI; }

   public static void encode(SPacketNpcDialogsGet ignoredMsg, FriendlyByteBuf ignoredBuf) { }

   public static SPacketNpcDialogsGet decode(FriendlyByteBuf ignoredBuf) { return new SPacketNpcDialogsGet(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      NoppesUtilServer.sendNpcDialogs(player);
      CustomNpcs.debugData.end("Packets");
   }

}
