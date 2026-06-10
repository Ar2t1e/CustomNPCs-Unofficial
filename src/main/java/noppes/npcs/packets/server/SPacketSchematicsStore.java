package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.blocks.tiles.TileCopy;
import noppes.npcs.controllers.SchematicController;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketSchematicsStore extends PacketServerBasic {

   protected static int channelId;
   private final int type;
   private final String name;
   private final CompoundTag data;

   public SPacketSchematicsStore(String nameIn, int typeIn, CompoundTag dataIn) {
      type = typeIn;
      name = nameIn;
      data = dataIn;
   }

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public List<PermissionNode<Boolean>> getPermission() { return null; }

   @Override
   public boolean toolAllowed(ItemStack item) {
      return item.getItem() == CustomItems.wand || item.getItem() == CustomBlocks.copy_item;
   }

   public static void encode(SPacketSchematicsStore msg, FriendlyByteBuf buf) {
      buf.writeUtf(msg.name);
      buf.writeInt(msg.type);
      buf.writeNbt(msg.data);
   }

   public static SPacketSchematicsStore decode(FriendlyByteBuf buf) { return new SPacketSchematicsStore(buf.readUtf(), buf.readInt(), buf.readNbt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      BlockEntity tile = SPacketTileEntitySave.saveTileEntity(player, data);
      if (tile instanceof TileCopy tileCopy && !name.isEmpty()) {
         SchematicController.Instance.save(player.createCommandSourceStack(), name, type,
                 tile.getBlockPos(), tileCopy.height, tileCopy.width, tileCopy.length);
      }
      CustomNpcs.debugData.end("Packets");
   }
}
