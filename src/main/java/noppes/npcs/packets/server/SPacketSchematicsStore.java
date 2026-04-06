package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tileentity.TileEntity;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.blocks.tiles.TileCopy;
import noppes.npcs.controllers.SchematicController;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketSchematicsStore extends PacketServerBasic {

   protected static int channelId;
   private int type;
   private String name;
   private NBTTagCompound data;

   public SPacketSchematicsStore() { }

   public SPacketSchematicsStore(String nameIn, int typeIn, NBTTagCompound dataIn) {
      type = typeIn;
      name = nameIn;
      data = dataIn;
   }

   @Override
   public boolean toolAllowed(ItemStack item) {
      return item.getItem() == CustomItems.wand || item.getItem() == CustomBlocks.copy_item;
   }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeUtf(name);
      buf.writeInt(type);
      buf.writeNbt(data);
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      name = buf.readUtf();
      type = buf.readInt();
      data = buf.readNbt();
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      TileEntity tile = SPacketTileEntitySave.saveTileEntity(player, data);
      if (tile instanceof TileCopy && !name.isEmpty()) {
         TileCopy tileCopy = (TileCopy) tile;
         SchematicController.Instance.save(player, name, type, tileCopy.getPos(),
                 tileCopy.height, tileCopy.width, tileCopy.length);
      }
      CustomNpcs.debugData.end("Packets");
   }
}
