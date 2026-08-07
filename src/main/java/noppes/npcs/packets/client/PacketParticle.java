package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

public class PacketParticle extends PacketBasic {

   protected static int channelId;
   public double posX;
   public double posY;
   public double posZ;
   public float height;
   public float width;
   public String name;

   public PacketParticle() { }

   @SuppressWarnings("unused")
   public PacketParticle(double x, double y, double z, float heightIn, float widthIn, String nameIn) {
      posX = x;
      posY = y;
      posZ = z;
      height = heightIn;
      width = widthIn;
      name = nameIn;
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      posX = buf.readDouble();
      posY = buf.readDouble();
      posZ = buf.readDouble();
      height = buf.readFloat();
      width = buf.readFloat();
      name = buf.readUtf();
   }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeDouble(posX);
      buf.writeDouble(posY);
      buf.writeDouble(posZ);
      buf.writeFloat(height);
      buf.writeFloat(width);
      buf.writeUtf(name);
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() { Client.processPacket(this); }

}
