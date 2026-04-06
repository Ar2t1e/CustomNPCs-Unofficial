package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.entity.data.DataScenes;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketSceneReset extends PacketServerBasic {

   protected static int channelId;

   @Override
   public CustomNpcsPermissions.Permission getPermission() { return CustomNpcsPermissions.SCENES; }

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   @Override
   public void encode(FriendlyByteBuf buf) { }

   @Override
   public void decode(FriendlyByteBuf buf) { }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (CustomNpcs.SceneButtonsEnabled) { DataScenes.Reset(player, null); }
      CustomNpcs.debugData.end("Packets");
   }

}
