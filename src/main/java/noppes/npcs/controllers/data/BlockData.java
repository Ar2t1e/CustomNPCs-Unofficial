package noppes.npcs.controllers.data;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

public class BlockData {

	public static @Nonnull BlockData getData(@Nonnull NBTTagCompound compound) {
		Block b = Block.getBlockFromName(compound.getString("Block"));
		if (b == null) { b = Blocks.AIR; }
		BlockPos pos = new BlockPos(compound.getInteger("BuildX"), compound.getInteger("BuildY"),
				compound.getInteger("BuildZ"));
		@SuppressWarnings("deprecation")
		IBlockState state = b.getStateFromMeta(compound.getInteger("Meta"));
		NBTTagCompound tile = null;
		if (compound.hasKey("Tile")) { tile = compound.getCompoundTag("Tile"); }
		return new BlockData(pos, state, tile);
	}

	public BlockPos pos;
	private ItemStack stack;
	public IBlockState state;
	public NBTTagCompound tile;

	public BlockData(BlockPos posIn, IBlockState stateIn, NBTTagCompound tileCompoundIn) {
		pos = posIn;
		state = stateIn;
		tile = tileCompoundIn;
	}

	public NBTTagCompound getNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setInteger("BuildX", pos.getX());
		compound.setInteger("BuildY", pos.getY());
		compound.setInteger("BuildZ", pos.getZ());
		compound.setString("Block", (Block.REGISTRY.getNameForObject(state.getBlock())).toString());
		compound.setInteger("Meta", state.getBlock().getMetaFromState(state));
		if (tile != null) { compound.setTag("Tile", tile); }
		return compound;
	}

	public ItemStack getStack() {
		if (stack == null) { stack = new ItemStack(state.getBlock(), 1, state.getBlock().damageDropped(state)); }
		return stack;
	}

}
