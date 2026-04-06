package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketNpcFactionSet extends PacketServerBasic {

   protected static int channelId;
   private int id;

   public SPacketNpcFactionSet() { }

   public SPacketNpcFactionSet(int idIn) { id = idIn; }

   @Override
   public boolean requiresNpc() {
      return true;
   }

   @Override
   public CustomNpcsPermissions.Permission getPermission() { return CustomNpcsPermissions.NPC_ADVANCED; }

   @Override
   public void encode(FriendlyByteBuf buf) { buf.writeInt(id); }

   @Override
   public void decode(FriendlyByteBuf buf) { id = buf.readInt(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      npc.setFaction(id);
      CustomNpcs.debugData.end("Packets");
   }

}
