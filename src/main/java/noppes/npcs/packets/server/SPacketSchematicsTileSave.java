package noppes.npcs.packets.server;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.blocks.tiles.TileBuilder;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketSchematicsTileSave extends PacketServerBasic {

   protected static int channelId;
   private final BlockPos pos;
   private final CompoundTag data;

   public SPacketSchematicsTileSave(BlockPos posIn, CompoundTag dataIn) {
      pos = posIn;
      data = dataIn;
   }

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public List<PermissionNode<Boolean>> getPermission() { return null; }

   @Override
   public boolean toolAllowed(ItemStack item) {
      return item.getItem() == CustomItems.wand || item.getItem() == CustomBlocks.builder_item || item.getItem() == CustomBlocks.copy_item;
   }

   public static void encode(SPacketSchematicsTileSave msg, FriendlyByteBuf buf) {
      buf.writeBlockPos(msg.pos);
      buf.writeNbt(msg.data);
   }

   public static SPacketSchematicsTileSave decode(FriendlyByteBuf buf) {
      return new SPacketSchematicsTileSave(buf.readBlockPos(), buf.readNbt());
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (player.level().getBlockEntity(pos) instanceof TileBuilder tile) {
         tile.loadPartNBT(data);
         player.level().getChunkAt(tile.getBlockPos()).setUnsaved(true);
      }
      CustomNpcs.debugData.end("Packets");
   }
}
