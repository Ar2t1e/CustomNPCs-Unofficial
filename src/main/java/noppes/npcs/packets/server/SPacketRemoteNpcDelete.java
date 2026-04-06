package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketNpcDelete;

public class SPacketRemoteNpcDelete extends PacketServerBasic {

   protected static int channelId;
   private final int entityId;
   private final boolean isAll;

   public SPacketRemoteNpcDelete(int entity, boolean all) {
      entityId = entity;
      isAll = all;
   }

   @Override
   public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.NPC_DELETE; }

   public static void encode(SPacketRemoteNpcDelete msg, FriendlyByteBuf buf) {
      buf.writeInt(msg.entityId);
      buf.writeBoolean(msg.isAll);
   }

   public static SPacketRemoteNpcDelete decode(FriendlyByteBuf buf) {
      return new SPacketRemoteNpcDelete(buf.readInt(), buf.readBoolean());
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      Entity entity = player.level().getEntity(entityId);
      if (entity != null) {
         if (entity instanceof EntityNPCInterface npcIn) { npcIn.delete(); }
         else { entity.remove(Entity.RemovalReason.DISCARDED); }
         Packets.sendNearby(entity, new PacketNpcDelete(entityId));
         SPacketRemoteNpcsGet.sendNearbyNpcs(player, isAll);
      }
      CustomNpcs.debugData.end("Packets");
   }

}
