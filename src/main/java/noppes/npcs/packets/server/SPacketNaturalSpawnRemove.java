package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.SpawnController;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketNaturalSpawnRemove extends PacketServerBasic {

   protected static int channelId;
   private final int id;

   public SPacketNaturalSpawnRemove(int idIn) { id = idIn; }

   @Override
   public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.GLOBAL_NATURALSPAWN; }

   public static void encode(SPacketNaturalSpawnRemove msg, FriendlyByteBuf buf) {
      buf.writeInt(msg.id);
   }

   public static SPacketNaturalSpawnRemove decode(FriendlyByteBuf buf) { return new SPacketNaturalSpawnRemove(buf.readInt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      SpawnController.instance.removeSpawnData(id);
      NoppesUtilServer.sendScrollData(player, SpawnController.instance.getScroll());
      CustomNpcs.debugData.end("Packets");
   }

}
