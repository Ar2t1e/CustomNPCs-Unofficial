package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketNpcFactionSet extends PacketServerBasic {

   protected static int channelId;
   private final int id;

   public SPacketNpcFactionSet(int idIn) { id = idIn; }

   @Override
   public boolean requiresNpc() { return true; }

   @Override
   public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.NPC_ADVANCED; }

   public static void encode(SPacketNpcFactionSet msg, FriendlyByteBuf buf) { buf.writeInt(msg.id); }

   public static SPacketNpcFactionSet decode(FriendlyByteBuf buf) { return new SPacketNpcFactionSet(buf.readInt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      npc.setFaction(id);
      CustomNpcs.debugData.end("Packets");
   }

}
