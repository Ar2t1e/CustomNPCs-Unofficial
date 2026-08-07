package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

public class PacketConfigFont extends PacketBasic {

   protected static int channelId;
   public String font;
   public int size;

   public PacketConfigFont() { }

   public PacketConfigFont(String fontIn, int sizeIn) {
      font = fontIn;
      size = sizeIn;
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      font = buf.readUtf();
      size = buf.readInt();
   }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeUtf(font);
      buf.writeInt(size);
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() { Client.processPacket(this); }

}
