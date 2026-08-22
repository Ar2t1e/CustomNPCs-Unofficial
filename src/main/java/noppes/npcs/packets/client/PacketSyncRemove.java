package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

public class PacketSyncRemove extends PacketBasic {

   protected static int channelId;
   public int id;
   public int type;

   public PacketSyncRemove() { }

   public PacketSyncRemove(int idIn, int typeIn) {
      id = idIn;
      type = typeIn;
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      id = buf.readInt();
      type = buf.readInt();
   }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeInt(id);
      buf.writeInt(type);
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() { Client.processPacket(this); }

}
