package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiScrollSelected;

public class SPacketRemoteFreeze extends PacketServerBasic {

   protected static int channelId;

   @Override
   public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.NPC_FREEZE; }

   public static void encode(SPacketRemoteFreeze ignoredMsg, FriendlyByteBuf ignoredBuf) {}

   public static SPacketRemoteFreeze decode(FriendlyByteBuf ignoredBuf) { return new SPacketRemoteFreeze(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      CustomNpcs.FreezeNPCs = !CustomNpcs.FreezeNPCs;
      Packets.send(player, new PacketGuiScrollSelected(CustomNpcs.FreezeNPCs ? "Unfreeze Npcs" : "Freeze Npcs"));
      CustomNpcs.debugData.end("Packets");
   }

}
