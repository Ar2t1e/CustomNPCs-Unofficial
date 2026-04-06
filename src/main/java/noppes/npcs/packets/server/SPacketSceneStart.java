package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.entity.data.DataScenes;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketSceneStart extends PacketServerBasic {

   protected static int channelId;
   private int scene;

   public SPacketSceneStart() { }

   public SPacketSceneStart(int sceneIn) { scene = sceneIn; }

   @Override
   public CustomNpcsPermissions.Permission getPermission() { return CustomNpcsPermissions.SCENES; }

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   @Override
   public void encode(FriendlyByteBuf buf) { buf.writeInt(scene); }

   @Override
   public void decode(FriendlyByteBuf buf) { scene = buf.readInt(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (CustomNpcs.SceneButtonsEnabled) { DataScenes.Toggle(scene + "btn"); }
      CustomNpcs.debugData.end("Packets");
   }

}
