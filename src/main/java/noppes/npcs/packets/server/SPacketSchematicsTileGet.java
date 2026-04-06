package noppes.npcs.packets.server;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.blocks.tiles.TileBuilder;
import noppes.npcs.controllers.SchematicController;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;
import noppes.npcs.shared.common.util.LogWriter;

public class SPacketSchematicsTileGet extends PacketServerBasic {

   protected static int channelId;
   private final BlockPos pos;

   public SPacketSchematicsTileGet(BlockPos posIn) { pos = posIn; }

   @Override
   public boolean toolAllowed(ItemStack item) {
      return item.getItem() == CustomItems.wand || item.getItem() == CustomBlocks.builder_item || item.getItem() == CustomBlocks.copy_item;
   }

   public static void encode(SPacketSchematicsTileGet msg, FriendlyByteBuf buf) { buf.writeBlockPos(msg.pos); }

   public static SPacketSchematicsTileGet decode(FriendlyByteBuf buf) { return new SPacketSchematicsTileGet(buf.readBlockPos()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (player.level().getBlockEntity(pos) instanceof TileBuilder tile) {
         Packets.send(player, new PacketGuiData(tile.savePartNBT(new CompoundTag())));
         NoppesUtilServer.sendScrollData(player, SchematicController.Instance.list());
         if (tile.hasSchematic()) { Packets.send(player, new PacketGuiData(tile.getSchematic().getNBTSmall())); }
      }
      CustomNpcs.debugData.end("Packets");
   }
}
