package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.blocks.tiles.TileBuilder;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketSchematicsTileSave extends PacketServerBasic {

   protected static int channelId;
   private BlockPos pos;
   private NBTTagCompound data;

   public SPacketSchematicsTileSave() { }

   public SPacketSchematicsTileSave(BlockPos posIn, NBTTagCompound dataIn) {
      pos = posIn;
      data = dataIn;
   }

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public List<CustomNpcsPermissions.Permission> getPermission() { return null; }

   @Override
   public boolean toolAllowed(ItemStack item) {
      return item.getItem() == CustomItems.wand || item.getItem() == CustomBlocks.builder_item || item.getItem() == CustomBlocks.copy_item;
   }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeBlockPos(pos);
      buf.writeNbt(data);
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      pos = buf.readBlockPos();
      data = buf.readNbt();
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      TileEntity tile = player.world.getTileEntity(pos);
      if (tile instanceof TileBuilder) {
         ((TileBuilder) tile).loadPartNBT(data);
         player.world.getChunkFromBlockCoords(tile.getPos()).setModified(true);
      }
      CustomNpcs.debugData.end("Packets");
   }
}
