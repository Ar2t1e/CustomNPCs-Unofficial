package noppes.npcs.packets.server;

import net.minecraft.entity.Entity;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketRemoteNpcTp extends PacketServerBasic {

   protected static int channelId;
   private int entityId;

   public SPacketRemoteNpcTp() { }

   public SPacketRemoteNpcTp(int entityIdIn) { entityId = entityIdIn; }

   @Override
   public void encode(FriendlyByteBuf buf) { buf.writeInt(entityId); }

   @Override
   public void decode(FriendlyByteBuf buf) { entityId = buf.readInt(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      Entity entity = player.world.getEntityByID(entityId);
      if (entity != null) {
         if (entity instanceof EntityNPCInterface) { npc = (EntityNPCInterface) entity; }
         SPacketDimensionTeleport.teleportPlayer(player, player.world.provider.getDimension(), entity.posX, entity.posY, entity.posZ, player.rotationYaw, player.rotationPitch);
      }
      CustomNpcs.debugData.end("Packets");
   }

}
