package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Collections;
import java.util.List;

public class SPacketCustomGuiSubGuiClosed extends PacketServerBasic {

   protected static int channelId;

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   @Override
   public List<PermissionNode<Boolean>> getPermission() {
      if (player.containerMenu instanceof ContainerCustomGui container) { return Collections.singletonList(container.activeGui.getPermission()); }
      return null;
   }

   public static void encode(SPacketCustomGuiSubGuiClosed ignoredMsg, FriendlyByteBuf ignoredBuf) { }

   public static SPacketCustomGuiSubGuiClosed decode(FriendlyByteBuf ignoredBuf) { return new SPacketCustomGuiSubGuiClosed(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (player.containerMenu instanceof ContainerCustomGui container && container.customGui.hasSubGui()) { container.activeGui.close(); }
      CustomNpcs.debugData.end("Packets");
   }

}
