package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.SoundCategory;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.controllers.MusicController;
import noppes.npcs.shared.common.PacketBasic;

public class PacketPlaySound extends PacketBasic {

   protected static int channelId;
   private String name;
   private SoundCategory category;
   private double x;
   private double y;
   private double z;
   private float volume;
   private float pitch;

   public PacketPlaySound() { }

   public PacketPlaySound(String nameIn, SoundCategory categoryIn, double xIn, double yIn, double zIn, float volumeIn, float pitchIn) {
      name = nameIn;
      category = categoryIn;
      x = xIn;
      y = yIn;
      z = zIn;
      volume = volumeIn;
      pitch = pitchIn;
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      name = buf.readUtf();
      category = SoundCategory.values()[buf.readInt()];
      x = buf.readDouble();
      y = buf.readDouble();
      z = buf.readDouble();
      volume = buf.readFloat();
      pitch = buf.readFloat();
   }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeUtf(name);
      buf.writeInt(category.ordinal());
      buf.writeDouble(x);
      buf.writeDouble(y);
      buf.writeDouble(z);
      buf.writeFloat(volume);
      buf.writeFloat(pitch);
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      MusicController.Instance.playSound(category, name, x, y, z, volume, pitch);
      CustomNpcs.debugData.end("Packets");
   }

}
