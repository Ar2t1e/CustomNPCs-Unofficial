package noppes.npcs.packets.server;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketTileEntitySave extends PacketServerBasic {

   protected static int channelId;
   private NBTTagCompound nbtTile;

   public SPacketTileEntitySave() { }

   public SPacketTileEntitySave(NBTTagCompound nbtTileIn) { nbtTile = nbtTileIn; }

   public boolean toolAllowed(ItemStack item) {
      return item.getItem() == CustomItems.wand || item.getItem() == CustomBlocks.border_item || item.getItem() == CustomBlocks.copy_item ||
              item.getItem() == CustomBlocks.redstone_item || item.getItem() == CustomBlocks.scripted_item || item.getItem() == CustomBlocks.waypoint_item;
   }

   @Override
   public void encode(FriendlyByteBuf buf) { buf.writeNbt(nbtTile); }

   @Override
   public void decode(FriendlyByteBuf buf) { nbtTile = buf.readNbt(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      saveTileEntity(player, nbtTile);
      CustomNpcs.debugData.end("Packets");
   }

   public static TileEntity saveTileEntity(EntityPlayerMP player, NBTTagCompound compound) {
      int x = compound.getInteger("x");
      int y = compound.getInteger("y");
      int z = compound.getInteger("z");
      BlockPos pos = new BlockPos(x, y, z);
      TileEntity tile = player.world.getTileEntity(pos);
      if (tile != null) {
         tile.readFromNBT(compound);
         tile.markDirty();
      }
      return tile;
   }

}
