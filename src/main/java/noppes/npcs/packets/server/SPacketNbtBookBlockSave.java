package noppes.npcs.packets.server;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.shared.common.util.LogWriter;

import java.util.Collections;
import java.util.List;

public class SPacketNbtBookBlockSave extends PacketServerBasic {

   protected static int channelId;
   private final BlockPos pos;
   private final CompoundTag data;

   public SPacketNbtBookBlockSave(BlockPos posIn, CompoundTag dataIn) {
      pos = posIn;
      data = dataIn;
   }

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.nbt_book; }

   @Override
   public List<PermissionNode<Boolean>> getPermission() { return Collections.singletonList(CustomNpcsPermissions.TOOL_NBTBOOK); }

   public static void encode(SPacketNbtBookBlockSave msg, FriendlyByteBuf buf) {
      buf.writeBlockPos(msg.pos);
      buf.writeNbt(msg.data);
   }

   public static SPacketNbtBookBlockSave decode(FriendlyByteBuf buf) { return new SPacketNbtBookBlockSave(buf.readBlockPos(), buf.readNbt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      BlockEntity tile = player.level().getBlockEntity(pos);
      if (tile != null) {
         tile.load(data);
         tile.setChanged();
         LogWriter.info(player.getName() + " edited NBT of BlockEntity in dimension ID:\"" + player.level().dimension().location() + "\" at " + pos);
      }
      CustomNpcs.debugData.end("Packets");
   }

}
