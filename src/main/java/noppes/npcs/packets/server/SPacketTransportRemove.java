package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.TransportController;
import noppes.npcs.controllers.data.TransportLocation;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Collections;
import java.util.List;

public class SPacketTransportRemove extends PacketServerBasic {

   protected static int channelId;
   private final int id;

   public SPacketTransportRemove(int idIn) { id = idIn; }

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

   @Override
   public List<PermissionNode<Boolean>> getPermission() { return Collections.singletonList(CustomNpcsPermissions.GLOBAL_TRANSPORT); }

   public static void encode(SPacketTransportRemove msg, FriendlyByteBuf buf) { buf.writeInt(msg.id); }

   public static SPacketTransportRemove decode(FriendlyByteBuf buf) { return new SPacketTransportRemove(buf.readInt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      TransportLocation loc = TransportController.getInstance().removeLocation(id);
      if (loc != null) { SPacketTransportGet.sendTransportData(player, loc.category.id); }
      CustomNpcs.debugData.end("Packets");
   }
}
