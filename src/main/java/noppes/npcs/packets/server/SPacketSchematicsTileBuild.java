package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.blocks.tiles.TileBuilder;
import noppes.npcs.controllers.SchematicController;
import noppes.npcs.schematics.Schematic;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.schematics.SchematicWrapper;

public class SPacketSchematicsTileBuild extends PacketServerBasic {

   protected static int channelId;
   private BlockPos pos;
   private int rotation;
   private NBTTagCompound compound;

   public SPacketSchematicsTileBuild() { }

   public SPacketSchematicsTileBuild(BlockPos posIn, int rotationIn, NBTTagCompound compoundIn) {
      pos = posIn;
      rotation = rotationIn;
      compound = compoundIn;
   }

   @Override
   public boolean toolAllowed(ItemStack item) {
      return item.getItem() == CustomItems.wand || item.getItem() == CustomBlocks.builder_item || item.getItem() == CustomBlocks.copy_item;
   }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeBlockPos(pos);
      buf.writeInt(rotation);
      buf.writeNbt(compound);
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      pos = buf.readBlockPos();
      rotation = buf.readInt();
      compound = buf.readAnySizeNbt();
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      TileEntity tile = player.world.getTileEntity(pos);
      if (tile instanceof TileBuilder && compound.getKeySet().isEmpty()) {
         SchematicWrapper schem = ((TileBuilder) tile).getSchematic();
         schem.init(pos.add(1, ((TileBuilder) tile).yOffset, 1), player.world, ((TileBuilder) tile).rotation * 90);
         SchematicController.Instance.build(((TileBuilder) tile).getSchematic(), player);
         player.world.setBlockToAir(pos);
      }
      else {
         Schematic schema = new Schematic("");
         schema.load(compound);
         SchematicController.buildBlocks(player, pos, rotation, schema);
      }
      CustomNpcs.debugData.start("Packets");
   }
}
