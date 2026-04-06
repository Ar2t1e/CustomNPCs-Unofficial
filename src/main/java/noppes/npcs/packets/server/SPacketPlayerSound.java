package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.SoundCategory;
import noppes.npcs.CustomNpcs;
import noppes.npcs.EventHooks;
import noppes.npcs.api.event.PlayerEvent;
import noppes.npcs.client.util.MusicData;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerScriptData;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketPlayerSound extends PacketServerBasic {

   protected static int channelId;
   private boolean isStart;
   private double x;
   private double y;
   private double z;
   private float volume;
   private float pitch;
   private String name;
   private String resource;
   private String category;
   private boolean looping;

   public SPacketPlayerSound() { }

   public SPacketPlayerSound(boolean isStartIn, MusicData md) {
      isStart = isStartIn;
      name = md.resource.getResourcePath().replaceAll("/", ".");
      resource = md.name;
      category = md.category.getName();
      looping = false;
      float[] pos = md.getPos();
      x = pos[0];
      y = pos[1];
      z = pos[2];
      volume = md.sound.getVolume();
      pitch = md.sound.getPitch();
   }

   public SPacketPlayerSound(boolean isStartIn, String soundIn, String resourceIn, String categoryIn, boolean loopingIn,
                             double xIn, double yIn, double zIn, float volumeIn, float pitchIn) {
      isStart = isStartIn;
      name = soundIn;
      resource = resourceIn;
      category = categoryIn;
      looping = loopingIn;
      x = xIn;
      y = yIn;
      z = zIn;
      volume = volumeIn;
      pitch = pitchIn;
   }

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeBoolean(isStart);
      buf.writeUtf(name == null ? "" : name);
      buf.writeUtf(resource == null ? "" : resource);
      buf.writeUtf(category == null ? SoundCategory.MASTER.getName() : category);
      buf.writeBoolean(looping);
      buf.writeDouble(x);
      buf.writeDouble(y);
      buf.writeDouble(z);
      buf.writeFloat(volume);
      buf.writeFloat(pitch);
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      isStart = buf.readBoolean();
      name = buf.readUtf();
      resource = buf.readUtf();
      category = buf.readUtf();
      looping = buf.readBoolean();
      x = buf.readDouble();
      y = buf.readDouble();
      z = buf.readDouble();
      volume = buf.readFloat();
      pitch = buf.readFloat();
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      PlayerScriptData handler = PlayerData.get(player).scriptData;
      EventHooks.onEvent(handler, isStart ? EnumScriptType.PLAY_SOUND : EnumScriptType.STOP_SOUND,
              new PlayerEvent.PlayerSound(handler.getPlayer(), name, resource, category, looping, x, y, z, volume, pitch));
      CustomNpcs.debugData.end("Packets");
   }

}
