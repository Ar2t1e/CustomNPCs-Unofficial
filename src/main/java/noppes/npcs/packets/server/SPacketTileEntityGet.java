package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketTileEntityGet extends PacketServerBasic {

   protected static int channelId;
   private BlockPos pos;

   public SPacketTileEntityGet() { }

   public SPacketTileEntityGet(BlockPos posIn) { pos = posIn; }

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   @Override
   public void encode(FriendlyByteBuf buf) { buf.writeBlockPos(pos); }

   @Override
   public void decode(FriendlyByteBuf buf) { pos = buf.readBlockPos(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      TileEntity tile = player.world.getTileEntity(pos);
      if (tile != null) { Packets.send(player, new PacketGuiData(tile.serializeNBT())); }
      CustomNpcs.debugData.end("Packets");
   }

}
