package noppes.npcs.blocks.tiles;

import com.google.common.base.Predicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import noppes.npcs.CustomBlocks;
import noppes.npcs.blocks.BlockBorder;
import noppes.npcs.controllers.data.Availability;
import org.jetbrains.annotations.NotNull;

public class TileBorder extends TileNpcEntity implements Predicate<Entity> {

   public Availability availability = new Availability();
   public int rotation = 0;
   public int height = 10;
   public String message = "availability.areaNotAvailable";
   public boolean creative = false;

   public TileBorder(BlockPos pos, BlockState state) { super(CustomBlocks.tile_border, pos, state); }

   @Override
   public boolean apply(Entity entity) { return entity instanceof ServerPlayer || entity instanceof ThrownEnderpearl; }

   @Override
   public void load(@NotNull CompoundTag compound) {
      super.load(compound);
      readExtraNBT(compound);
      if (getLevel() != null) {
         getLevel().setBlockAndUpdate(getBlockPos(), CustomBlocks.border.defaultBlockState().setValue(BlockBorder.ROTATION, rotation));
      }
   }

   public void readExtraNBT(CompoundTag compound) {
      availability.load(compound.getCompound("BorderAvailability"));
      rotation = compound.getInt("BorderRotation");
      height = compound.getInt("BorderHeight");
      message = compound.getString("BorderMessage");
      creative = compound.getBoolean("Bordercreative");
   }

   @Override
   public void saveAdditional(@NotNull CompoundTag compound) {
      writeExtraNBT(compound);
      super.saveAdditional(compound);
   }

   public void writeExtraNBT(CompoundTag compound) {
      compound.put("BorderAvailability", availability.save(new CompoundTag()));
      compound.putInt("BorderRotation", rotation);
      compound.putInt("BorderHeight", height);
      compound.putString("BorderMessage", message);
      compound.putBoolean("Bordercreative", creative);
   }

   public static void tick(Level level, BlockPos pos, BlockState ignoredState, TileBorder tile) {
      if (level.isClientSide || tile.level == null) { return; }
      List<Entity> list = new ArrayList<>();
      AABB box = new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + tile.height + 1, pos.getZ() + 1);
      try { list = level.getEntitiesOfClass(Entity.class, box, tile); }
      catch (Exception ignored) { }
      for (Entity entity : list) {
         if (entity instanceof ThrownEnderpearl pearl) {
            if (pearl.getOwner() instanceof Player player && checkPlayer(tile, player, (int) (entity.getY() + 0.5d))) {
               entity.setRemoved(RemovalReason.DISCARDED);
            }
         }
         else if (entity instanceof Player player) { checkPlayer(tile, player, (int) (entity.getY() + 0.5d)); }
      }
   }

   private static boolean checkPlayer(TileBorder tile, Player player, int startY) {
      if ((player.isCreative() && !tile.creative) || tile.availability.isAvailable(player)) { return false; }
      BlockPos newPos = new BlockPos(tile.worldPosition.getX(), startY, tile.worldPosition.getZ());
      if (tile.rotation == 2) { newPos = newPos.south(); }
      else if (tile.rotation == 0) { newPos = newPos.north(); }
      else if (tile.rotation == 1) { newPos = newPos.east(); }
      else if (tile.rotation == 3) { newPos = newPos.west(); }
      int i = startY - tile.worldPosition.getY();
      while (i < tile.height && tile.level != null && (!tile.level.isEmptyBlock(newPos) || !tile.level.isEmptyBlock(newPos.above()))) {
         newPos = newPos.above();
         i++;
      }
      player.teleportTo(newPos.getX() + 0.5, newPos.getY(), newPos.getZ() + 0.5);
      if (!tile.message.isEmpty()) {
         player.displayClientMessage(Component.translatable(tile.message), true);
      }
      return true;
   }

   public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
      handleUpdateTag(Objects.requireNonNull(pkt.getTag()));
   }

   public void handleUpdateTag(CompoundTag compound) {
      rotation = compound.getInt("Rotation");
      height = compound.getInt("Height");
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }

   public @NotNull CompoundTag getUpdateTag() {
      CompoundTag compound = new CompoundTag();
      compound.putInt("x", worldPosition.getX());
      compound.putInt("y", worldPosition.getY());
      compound.putInt("z", worldPosition.getZ());
      compound.putInt("Rotation", rotation);
      compound.putInt("Height", height);
      return compound;
   }

}
