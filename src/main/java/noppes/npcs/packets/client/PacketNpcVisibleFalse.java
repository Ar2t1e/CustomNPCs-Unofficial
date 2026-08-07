package noppes.npcs.packets.client;

import net.minecraft.entity.Entity;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

import javax.annotation.Nonnull;
import java.util.UUID;

public class PacketNpcVisibleFalse extends PacketBasic {

   protected static int channelId;
   public int id;
   public UUID uuid;

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
   protected void handle() { Client.processPacket(this); }

}
