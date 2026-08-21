package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

public class PacketPlayMusic extends PacketBasic {

   protected static int channelId;
   public ResourceLocation name;
   public boolean streaming;
   public boolean looping;

   public PacketPlayMusic() { }

   public PacketPlayMusic(ResourceLocation nameIn, boolean streamingIn, boolean loopingIn) {
      name = nameIn;
      streaming = streamingIn;
      looping = loopingIn;
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      name = buf.readResourceLocation();
      streaming = buf.readBoolean();
      looping = buf.readBoolean();
   }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeResourceLocation(name);
      buf.writeBoolean(streaming);
      buf.writeBoolean(looping);
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() { Client.processPacket(this); }

}
