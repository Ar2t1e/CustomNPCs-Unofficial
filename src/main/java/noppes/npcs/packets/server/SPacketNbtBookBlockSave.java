package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Collections;
import java.util.List;

public class SPacketNbtBookBlockSave extends PacketServerBasic {

   protected static int channelId;
   private BlockPos pos;
   private NBTTagCompound data;

   public SPacketNbtBookBlockSave() { }

   public SPacketNbtBookBlockSave(BlockPos posIn, NBTTagCompound dataIn) {
      pos = posIn;
      data = dataIn;
   }

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.nbt_book; }

   @Override
   public List<CustomNpcsPermissions.Permission> getPermission() { return Collections.singletonList(CustomNpcsPermissions.TOOL_NBTBOOK); }

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
      if (tile != null) {
         tile.readFromNBT(data);
         tile.markDirty();
      }
      CustomNpcs.debugData.end("Packets");
   }

}
