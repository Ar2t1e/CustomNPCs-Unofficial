package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.TransportController;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Collections;
import java.util.List;

public class SPacketTransportCategoryRemove extends PacketServerBasic {

   protected static int channelId;
   private final int id;

   public SPacketTransportCategoryRemove(int idIn) { id = idIn; }

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

   @Override
   public List<PermissionNode<Boolean>> getPermission() { return Collections.singletonList(CustomNpcsPermissions.GLOBAL_TRANSPORT); }

   public static void encode(SPacketTransportCategoryRemove msg, FriendlyByteBuf buf) {
      buf.writeInt(msg.id);
   }

   public static SPacketTransportCategoryRemove decode(FriendlyByteBuf buf) {
      return new SPacketTransportCategoryRemove(buf.readInt());
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      TransportController.getInstance().removeCategory(id);
      TransportController.getInstance().sendTo(player);
      Packets.send(player, new PacketGuiData(new CompoundTag()));
      CustomNpcs.debugData.start("Packets");
   }

}
