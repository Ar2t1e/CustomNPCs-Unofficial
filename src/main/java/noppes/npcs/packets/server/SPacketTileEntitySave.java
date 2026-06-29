package noppes.npcs.packets.server;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketTileEntitySave extends PacketServerBasic {

   protected static int channelId;
   private final CompoundTag nbtTile;

   public SPacketTileEntitySave(CompoundTag nbtTileIn) { nbtTile = nbtTileIn; }

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public List<PermissionNode<Boolean>> getPermission() { return null; }

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   public static void encode(SPacketTileEntitySave msg, FriendlyByteBuf buf) { buf.writeNbt(msg.nbtTile); }

   public static SPacketTileEntitySave decode(FriendlyByteBuf buf) { return new SPacketTileEntitySave(buf.readNbt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      saveTileEntity(player, nbtTile);
      CustomNpcs.debugData.end("Packets");
   }

   public static BlockEntity saveTileEntity(ServerPlayer player, CompoundTag compound) {
      BlockPos pos = new BlockPos(compound.getInt("x"), compound.getInt("y"), compound.getInt("z"));
      BlockEntity tile = player.level().getBlockEntity(pos);
      if (tile != null) {
         tile.load(compound);
         tile.setChanged();
      }
      return tile;
   }

}
