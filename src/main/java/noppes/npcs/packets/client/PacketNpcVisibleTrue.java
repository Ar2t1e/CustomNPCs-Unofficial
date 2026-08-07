package noppes.npcs.packets.client;

import net.minecraft.entity.Entity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.common.network.internal.EntitySpawnMessageHelper;
import net.minecraftforge.fml.common.network.internal.FMLMessage;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

import java.util.UUID;

public class PacketNpcVisibleTrue extends PacketBasic {

   protected static int channelId;
   public FMLMessage.EntitySpawnMessage pkt;
   public int id;
   public UUID uuid;

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
   protected void handle() { Client.processPacket(this); }

}
