package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.blocks.tiles.TileBuilder;
import noppes.npcs.controllers.SchematicController;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketSchematicsTileGet extends PacketServerBasic {

   protected static int channelId;
   private BlockPos pos;

   public SPacketSchematicsTileGet() { }

   public SPacketSchematicsTileGet(BlockPos posIn) { pos = posIn; }

   @Override
   public boolean toolAllowed(ItemStack item) {
      return item.getItem() == CustomItems.wand || item.getItem() == CustomBlocks.builder_item || item.getItem() == CustomBlocks.copy_item;
   }

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
      if (tile instanceof TileBuilder) {
         TileBuilder tileBuilder = (TileBuilder) tile;
         Packets.send(player, new PacketGuiData(tileBuilder.savePartNBT(new NBTTagCompound())));
         NoppesUtilServer.sendScrollData(player, SchematicController.Instance.list());
         if (tileBuilder.hasSchematic()) {
            Packets.send(player, new PacketGuiData(tileBuilder.getSchematic().getNBTSmall()));
         }
      }
      CustomNpcs.debugData.end("Packets");
   }
}
