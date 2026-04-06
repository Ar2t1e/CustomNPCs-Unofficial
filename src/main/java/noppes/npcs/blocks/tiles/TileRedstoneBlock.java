package noppes.npcs.blocks.tiles;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomNpcs;
import noppes.npcs.blocks.BlockNpcRedstone;
import noppes.npcs.controllers.data.Availability;
import org.jetbrains.annotations.NotNull;

public class TileRedstoneBlock extends TileNpcEntity {

   public int onRange = 12;
   public int offRange = 20;
   public int onRangeX = 12;
   public int onRangeY = 12;
   public int onRangeZ = 12;
   public int offRangeX = 20;
   public int offRangeY = 20;
   public int offRangeZ = 20;
   public boolean isDetailed = false;
   public Availability availability = new Availability();
   public boolean isActivated = false;
   private int ticks = 10;

   public TileRedstoneBlock(BlockPos pos, BlockState state) {
      super(CustomBlocks.tile_redstoneblock, pos, state);
   }

   public static void tick(Level level, BlockPos pos, BlockState state, TileRedstoneBlock tile) {
      if (tile.level == null || tile.level.isClientSide) { return; }
      --tile.ticks;
      if (tile.ticks > 0) { return; }

      tile.ticks = tile.onRange > 10 ? 20 : 10;
      Block block = state.getBlock();
      if (!(block instanceof BlockNpcRedstone)) { return; }
      if (CustomNpcs.FreezeNPCs) {
         if (tile.isActivated) { tile.setActive(block, false); }
      } else {
         int x;
         int y;
         int z;
         List<Player> list;
         Iterator<Player> var9;
         Player player;
         if (!tile.isActivated) {
            x = tile.isDetailed ? tile.onRangeX : tile.onRange;
            y = tile.isDetailed ? tile.onRangeY : tile.onRange;
            z = tile.isDetailed ? tile.onRangeZ : tile.onRange;
            list = tile.getPlayerList(x, y, z);
            if (list.isEmpty()) {
               return;
            }
            var9 = list.iterator();
            while(var9.hasNext()) {
               player = var9.next();
               if (tile.availability.isAvailable(player)) {
                  tile.setActive(block, true);
                  return;
               }
            }
         } else {
            x = tile.isDetailed ? tile.offRangeX : tile.offRange;
            y = tile.isDetailed ? tile.offRangeY : tile.offRange;
            z = tile.isDetailed ? tile.offRangeZ : tile.offRange;
            list = tile.getPlayerList(x, y, z);
            var9 = list.iterator();
            while(var9.hasNext()) {
               player = var9.next();
               if (tile.availability.isAvailable(player)) {
                  return;
               }
            }
            tile.setActive(block, false);
         }

      }
   }

   private void setActive(Block block, boolean bo) {
      this.isActivated = bo;
      BlockState state = block.defaultBlockState().setValue(BlockNpcRedstone.ACTIVE, this.isActivated);
      if (level != null) { level.setBlock(this.worldPosition, state, 2); }
      this.setChanged();
      if (level != null) {
         level.sendBlockUpdated(this.worldPosition, state, state, 3);
         block.onPlace(state, level, this.worldPosition, state, false);
      }
   }

   private List<Player> getPlayerList(int x, int y, int z) {
      if (level == null) { return Collections.emptyList(); }
      return level.getEntitiesOfClass(Player.class, (new AABB(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), this.worldPosition.getX() + 1, this.worldPosition.getY() + 1, this.worldPosition.getZ() + 1)).inflate(x, y, z));
   }

   public void load(@NotNull CompoundTag compound) {
      super.load(compound);
      this.onRange = compound.getInt("BlockOnRange");
      this.offRange = compound.getInt("BlockOffRange");
      this.isDetailed = compound.getBoolean("BlockIsDetailed");
      if (compound.contains("BlockOnRangeX")) {
         this.isDetailed = true;
         this.onRangeX = compound.getInt("BlockOnRangeX");
         this.onRangeY = compound.getInt("BlockOnRangeY");
         this.onRangeZ = compound.getInt("BlockOnRangeZ");
         this.offRangeX = compound.getInt("BlockOffRangeX");
         this.offRangeY = compound.getInt("BlockOffRangeY");
         this.offRangeZ = compound.getInt("BlockOffRangeZ");
      }

      if (compound.contains("BlockActivated")) {
         this.isActivated = compound.getBoolean("BlockActivated");
      }

      this.availability.load(compound);
   }

   public void saveAdditional(@NotNull CompoundTag compound) {
      compound.putInt("BlockOnRange", this.onRange);
      compound.putInt("BlockOffRange", this.offRange);
      compound.putBoolean("BlockActivated", this.isActivated);
      compound.putBoolean("BlockIsDetailed", this.isDetailed);
      if (this.isDetailed) {
         compound.putInt("BlockOnRangeX", this.onRangeX);
         compound.putInt("BlockOnRangeY", this.onRangeY);
         compound.putInt("BlockOnRangeZ", this.onRangeZ);
         compound.putInt("BlockOffRangeX", this.offRangeX);
         compound.putInt("BlockOffRangeY", this.offRangeY);
         compound.putInt("BlockOffRangeZ", this.offRangeZ);
      }

      this.availability.save(compound);
      super.saveAdditional(compound);
   }

}
