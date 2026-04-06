package noppes.npcs.packets.client;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketBasic;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public class PacketNpcVisibleFalse extends PacketBasic {

   protected static int channelId;
   private int id;
   private UUID uuid;

   public PacketNpcVisibleFalse() { }

   public PacketNpcVisibleFalse(@Nonnull Entity entityIn) {
      id = entityIn.getEntityId();
      uuid = entityIn.getUniqueID();
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      id = buf.readInt();
      uuid = buf.readUUID();
   }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeInt(id);
      buf.writeUUID(uuid);
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      WorldClient world = (WorldClient) player.world;
      List<EntityNPCInterface> npcInterfaces = world.getEntities(EntityNPCInterface.class, entity -> entity.getUniqueID().equals(uuid) && entity.getEntityId() == id);
      for (EntityNPCInterface npc : npcInterfaces) {
         if (npc == null) { continue; }
         world.removeEntity(npc);
      }
      CustomNpcs.debugData.end("Packets");
   }

}
