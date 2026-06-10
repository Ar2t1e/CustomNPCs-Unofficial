package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketPlayerCloseContainer extends PacketServerBasic {

   protected static int channelId;

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public List<PermissionNode<Boolean>> getPermission() { return null; }

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   public static void encode(SPacketPlayerCloseContainer ignoredMsg, FriendlyByteBuf ignoredBuf) { }

   public static SPacketPlayerCloseContainer decode(FriendlyByteBuf ignoredBuf) { return new SPacketPlayerCloseContainer(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      player.closeContainer();
      CustomNpcs.debugData.end("Packets");
   }

}
