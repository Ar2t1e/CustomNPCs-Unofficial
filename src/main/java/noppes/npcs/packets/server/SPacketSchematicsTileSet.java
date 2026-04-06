package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.blocks.tiles.TileBuilder;
import noppes.npcs.controllers.SchematicController;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketSchematicsTileSet extends PacketServerBasic {

   protected static int channelId;
   private BlockPos pos;
   private String name;

   public SPacketSchematicsTileSet() { }

   public SPacketSchematicsTileSet(BlockPos posIn, String nameIn) {
      pos = posIn;
      name = nameIn;
   }

   @Override
   public boolean toolAllowed(ItemStack item) {
      return item.getItem() == CustomItems.wand || item.getItem() == CustomBlocks.builder_item || item.getItem() == CustomBlocks.copy_item;
   }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeBlockPos(pos);
      buf.writeUtf(name);
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      pos = buf.readBlockPos();
      name = buf.readUtf();
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      TileEntity tile = player.world.getTileEntity(pos);
      if (tile instanceof TileBuilder) {
         ((TileBuilder) tile).setSchematic(SchematicController.Instance.load(name));
         player.world.getChunkFromBlockCoords(tile.getPos()).setModified(true);
         if (((TileBuilder) tile).hasSchematic()) {
            Packets.send(player, new PacketGuiData(((TileBuilder) tile).getSchematic().getNBTSmall()));
         }
      }
      CustomNpcs.debugData.end("Packets");
   }
}
