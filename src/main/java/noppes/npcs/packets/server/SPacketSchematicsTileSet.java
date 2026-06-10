package noppes.npcs.packets.server;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.blocks.tiles.TileBuilder;
import noppes.npcs.controllers.SchematicController;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

import java.util.List;

public class SPacketSchematicsTileSet extends PacketServerBasic {

   protected static int channelId;
   private final BlockPos pos;
   private final String name;

   public SPacketSchematicsTileSet(BlockPos posIn, String nameIn) {
      pos = posIn;
      name = nameIn;
   }

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public List<PermissionNode<Boolean>> getPermission() { return null; }

   @Override
   public boolean toolAllowed(ItemStack item) {
      return item.getItem() == CustomItems.wand || item.getItem() == CustomBlocks.builder_item || item.getItem() == CustomBlocks.copy_item;
   }

   public static void encode(SPacketSchematicsTileSet msg, FriendlyByteBuf buf) {
      buf.writeBlockPos(msg.pos);
      buf.writeUtf(msg.name);
   }

   public static SPacketSchematicsTileSet decode(FriendlyByteBuf buf) {
      return new SPacketSchematicsTileSet(buf.readBlockPos(), buf.readUtf());
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (player.level().getBlockEntity(pos) instanceof TileBuilder tile) {
         tile.setSchematic(SchematicController.Instance.load(name));
         player.level().getChunkAt(tile.getBlockPos()).setUnsaved(true);
         if (tile.hasSchematic()) { Packets.send(player, new PacketGuiData(tile.getSchematic().getNBTSmall())); }
      }
      CustomNpcs.debugData.end("Packets");
   }
}
