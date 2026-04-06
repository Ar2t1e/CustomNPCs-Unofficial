package noppes.npcs.controllers.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

public class BlockData {

   public static @Nonnull BlockData getData(@Nonnull Level level, @Nonnull CompoundTag compound) {
      BlockPos pos = new BlockPos(compound.getInt("BuildX"), compound.getInt("BuildY"), compound.getInt("BuildZ"));
      BlockState state = NbtUtils.readBlockState(level.holderLookup(Registries.BLOCK), compound.getCompound("BlockState"));
      CompoundTag tile = null;
      if (compound.contains("Tile")) { tile = compound.getCompound("Tile"); }
      return new BlockData(pos, state, tile);
   }

   public BlockPos pos;
   public BlockState state;
   public CompoundTag tile;
   private ItemStack stack;

   public BlockData(BlockPos posIn, BlockState stateIn, CompoundTag tileCompoundIn) {
      pos = posIn;
      state = stateIn;
      tile = tileCompoundIn;
   }

   public CompoundTag getNBT() {
      CompoundTag compound = new CompoundTag();
      compound.putInt("BuildX", pos.getX());
      compound.putInt("BuildY", pos.getY());
      compound.putInt("BuildZ", pos.getZ());
      compound.put("Block", NbtUtils.writeBlockState(state));
      if (tile != null) { compound.put("Tile", tile); }
      return compound;
   }

   public ItemStack getStack() {
      if (stack == null) { stack = new ItemStack(state.getBlock(), 1); }
      return stack;
   }

}
