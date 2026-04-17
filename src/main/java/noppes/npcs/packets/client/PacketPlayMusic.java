package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.controllers.MusicController;
import noppes.npcs.shared.common.PacketBasic;

public class PacketPlayMusic extends PacketBasic {

   protected static int channelId;
   private ResourceLocation name;
   private boolean streaming;
   private boolean looping;

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
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (streaming) { MusicController.Instance.playStreaming(name, player); }
      else { MusicController.Instance.playMusic(name, player, looping); }
      CustomNpcs.debugData.end("Packets");
   }

}
