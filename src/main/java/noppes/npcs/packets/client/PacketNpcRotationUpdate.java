package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

public class PacketNpcRotationUpdate extends PacketBasic {

   protected static int channelId;
   public int id;
   public int orientation;

   public PacketNpcRotationUpdate() { }

   public PacketNpcRotationUpdate(int idIn, int orientationIn) {
      id = idIn;
      orientation = orientationIn;
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      id = buf.readInt();
      orientation = buf.readInt();
   }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeInt(id);
      buf.writeInt(orientation);
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() { Client.processPacket(this); }

}
