package noppes.npcs.items;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import noppes.npcs.api.item.INPCToolItem;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;
import noppes.npcs.packets.client.PacketGuiOpen;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemNbtBook extends Item implements INPCToolItem {

   public ItemNbtBook() {
      super((new Properties()).stacksTo(1));
   }

   public void blockEvent(ServerPlayer player, BlockPos pos) {
      if (player == null || pos == null || player.level().getBlockState(pos).isAir()) { return; }
      Packets.send(player, new PacketGuiOpen(EnumGuiType.NbtBook, pos));
      CompoundTag data = new CompoundTag();
      BlockEntity tile = player.level().getBlockEntity(pos);
      if (tile != null) { data = tile.saveWithFullMetadata(); }
      CompoundTag compound = new CompoundTag();
      compound.put("Data", data);
      Packets.send(player, new PacketGuiData(compound));
   }

   public void entityEvent(ServerPlayer player, Entity target) {
      if (player == null || target == null) { return; }
      Packets.send(player, new PacketGuiOpen(EnumGuiType.NbtBook, BlockPos.ZERO));
      CompoundTag data = new CompoundTag();
      target.saveAsPassenger(data);
      CompoundTag compound = new CompoundTag();
      compound.putInt("EntityId", target.getId());
      compound.put("Data", data);
      Packets.send(player, new PacketGuiData(compound));
   }

   // New from Unofficial (BetaZavr)
   public void itemEvent(ServerPlayer player) {
      if (player == null) { return; }
      Packets.send(player, new PacketGuiOpen(EnumGuiType.NbtBook, new BlockPos((int) Math.floor(player.getX()), (int) Math.floor(player.getY()), (int) Math.floor(player.getZ()))));
      CompoundTag compound = new CompoundTag();
      compound.putBoolean("Item", true);
      compound.put("Data", player.getOffhandItem().save(new CompoundTag()));
      Packets.send(player, new PacketGuiData(compound));
   }

   @Override
   public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level, @Nonnull List<Component> list, @Nonnull TooltipFlag flagIn) {
      list.add(Component.translatable("info.item.nbt.book"));
   }

}
