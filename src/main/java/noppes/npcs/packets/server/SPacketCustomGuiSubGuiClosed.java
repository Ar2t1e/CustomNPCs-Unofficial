package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketCustomGuiSubGuiClosed extends PacketServerBasic {

   protected static int channelId;

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   @Override
   public CustomNpcsPermissions.Permission getPermission() {
      if (player.openContainer instanceof ContainerCustomGui) {
         return ((ContainerCustomGui) player.openContainer).activeGui.getPermission();
      }
      return null;
   }

   @Override
   public void encode(FriendlyByteBuf buf) { }

   @Override
   public void decode(FriendlyByteBuf buf) { }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (player.openContainer instanceof ContainerCustomGui && ((ContainerCustomGui) player.openContainer).customGui.hasSubGui()) {
         ((ContainerCustomGui) player.openContainer).activeGui.close();
      }
      CustomNpcs.debugData.end("Packets");
   }

}
