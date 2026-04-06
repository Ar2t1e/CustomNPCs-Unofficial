package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.controllers.MusicController;
import noppes.npcs.shared.common.PacketBasic;

public class PacketPlaySound extends PacketBasic {

   protected static int channelId;
   private final String name;
   private final SoundSource category;
   private final double x;
   private final double y;
   private final double z;
   private final float volume;
   private final float pitch;

   public PacketPlaySound(String nameIn, SoundSource categoryIn, double xIn, double yIn, double zIn, float volumeIn, float pitchIn) {
      name = nameIn;
      category = categoryIn;
      x = xIn;
      y = yIn;
      z = zIn;
      volume = volumeIn;
      pitch = pitchIn;
   }

   public static void encode(PacketPlaySound msg, FriendlyByteBuf buf) {
      buf.writeUtf(msg.name);
      buf.writeInt(msg.category.ordinal());
      buf.writeDouble(msg.x);
      buf.writeDouble(msg.y);
      buf.writeDouble(msg.z);
      buf.writeFloat(msg.volume);
      buf.writeFloat(msg.pitch);
   }

   public static PacketPlaySound decode(FriendlyByteBuf buf) {
      return new PacketPlaySound(buf.readUtf(), SoundSource.values()[buf.readInt()],
              buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readFloat(), buf.readFloat());
   }

   @Override
   public int getChannelId() { return channelId; }

   @OnlyIn(Dist.CLIENT)
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      MusicController.Instance.playSound(category, name, x, y, z, volume, pitch);
      CustomNpcs.debugData.end("Packets");
   }

}
