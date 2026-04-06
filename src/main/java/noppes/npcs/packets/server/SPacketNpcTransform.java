package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketNpcTransform extends PacketServerBasic {

   protected static int channelId;
   private final boolean isActive;

   public SPacketNpcTransform(boolean isActiveIn) { isActive = isActiveIn; }

   @Override
   public boolean requiresNpc() { return true; }

   @Override
   public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.NPC_ADVANCED; }

   public static void encode(SPacketNpcTransform msg, FriendlyByteBuf buf) { buf.writeBoolean(msg.isActive); }

   public static SPacketNpcTransform decode(FriendlyByteBuf buf) { return new SPacketNpcTransform(buf.readBoolean()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (npc.transform.isValid()) { npc.transform.transform(isActive); }
      CustomNpcs.debugData.end("Packets");
   }

}
