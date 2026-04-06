package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.controllers.MusicController;
import noppes.npcs.shared.common.PacketBasic;

public class PacketPlayMusic extends PacketBasic {

   protected static int channelId;
   private final ResourceLocation name;
   private final boolean streaming;
   private final boolean looping;

   public PacketPlayMusic(ResourceLocation nameIn, boolean streamingIn, boolean loopingIn) {
      name = nameIn;
      streaming = streamingIn;
      looping = loopingIn;
   }

   public static void encode(PacketPlayMusic msg, FriendlyByteBuf buf) {
      buf.writeResourceLocation(msg.name);
      buf.writeBoolean(msg.streaming);
      buf.writeBoolean(msg.looping);
   }

   public static PacketPlayMusic decode(FriendlyByteBuf buf) {
      return new PacketPlayMusic(buf.readResourceLocation(), buf.readBoolean(), buf.readBoolean());
   }

   @Override
   public int getChannelId() { return channelId; }

   @OnlyIn(Dist.CLIENT)
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (streaming) { MusicController.Instance.playStreaming(name, player, looping); }
      else { MusicController.Instance.playMusic(name, player, looping); }
      CustomNpcs.debugData.end("Packets");
   }

}
