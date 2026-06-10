package noppes.npcs.packets.server;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

import java.util.List;

public class SPacketTileEntityGet extends PacketServerBasic {

   protected static int channelId;
   private final BlockPos pos;

   public SPacketTileEntityGet(BlockPos posIn) { pos = posIn; }

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public List<PermissionNode<Boolean>> getPermission() { return null; }

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   public static void encode(SPacketTileEntityGet msg, FriendlyByteBuf buf) { buf.writeBlockPos(msg.pos); }

   public static SPacketTileEntityGet decode(FriendlyByteBuf buf) {
      return new SPacketTileEntityGet(buf.readBlockPos());
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      BlockEntity tile = player.level().getBlockEntity(pos);
      if (tile != null) { Packets.send(player, new PacketGuiData(tile.serializeNBT())); }
      CustomNpcs.debugData.end("Packets");
   }

}
