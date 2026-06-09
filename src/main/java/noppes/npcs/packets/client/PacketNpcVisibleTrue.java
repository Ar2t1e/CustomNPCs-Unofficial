package noppes.npcs.packets.client;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.common.network.internal.EntitySpawnMessageHelper;
import net.minecraftforge.fml.common.network.internal.FMLMessage;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketBasic;

import java.util.List;
import java.util.UUID;

public class PacketNpcVisibleTrue extends PacketBasic {

   protected static int channelId;
   private FMLMessage.EntitySpawnMessage pkt;
   private int id;
   private UUID uuid;

   public PacketNpcVisibleTrue() { }

   public PacketNpcVisibleTrue(Entity entityIn, FMLMessage.EntitySpawnMessage pktIn) {
      id = entityIn.getEntityId();
      uuid = entityIn.getUniqueID();
      pkt = pktIn;
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      id = buf.readInt();
      uuid = buf.readUUID();
      pkt = EntitySpawnMessageHelper.fromBytes(buf);
   }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeInt(id);
      buf.writeUUID(uuid);
      EntitySpawnMessageHelper.toBytes(pkt, buf);
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      WorldClient world = (WorldClient) player.world;
      List<EntityNPCInterface> npcInterfaces = world.getEntities(EntityNPCInterface.class,
              entity -> entity.getUniqueID().equals(uuid));
      if (npcInterfaces.isEmpty()) {
         npcInterfaces = world.getEntities(EntityNPCInterface.class, entity -> entity.getEntityId() == id);
      }
      if (npcInterfaces.isEmpty()) {
         LogWriter.debug("Tries to visible summon an entity into the client world.");
         EntitySpawnMessageHelper.spawn(pkt);
      }
      if (!npcInterfaces.isEmpty()) {
         for (EntityNPCInterface npc : npcInterfaces) {
            if (npc == null) {
               LogWriter.debug("Tries to visible summon an NPC into the client world.");
               EntitySpawnMessageHelper.spawn(pkt);
            }
         }
      }
      CustomNpcs.debugData.end("Packets");
   }

}
