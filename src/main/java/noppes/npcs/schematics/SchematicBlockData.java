package noppes.npcs.schematics;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import noppes.npcs.util.CustomNPCsScheduler;

// New from Unofficial (BetaZavr)
public class SchematicBlockData {

	public BlockPos pos;
	public IBlockState state;
	public NBTTagCompound nbtTile;
	public World world;
	public int meta = 0, id = 0;

	public SchematicBlockData(World worldIn, IBlockState stateIn, BlockPos posIn) {
		world = worldIn;
		pos = posIn;
		state = stateIn;
		meta = state.getBlock().getMetaFromState(state);
		nbtTile = null;
		if (state.getBlock() instanceof ITileEntityProvider && world != null && world.getTileEntity(pos) != null) {
			nbtTile = new NBTTagCompound();
			TileEntity tile = world.getTileEntity(pos);
			if (tile != null) { nbtTile = tile.writeToNBT(nbtTile); }
		}
	}

	public SchematicBlockData(World worldIn, ItemStack stack) {
		world = worldIn;
		pos = null;
		Block b = Block.getBlockFromItem(stack.getItem());
		state = b.getDefaultState();
		if (stack.getItemDamage() < b.getBlockState().getValidStates().size()) { state = b.getStateFromMeta(stack.getItemDamage()); }
		nbtTile = null;
		if (stack.getTagCompound() != null) { nbtTile = stack.getTagCompound().copy(); }
	}

	public void set(BlockPos pos) {
		if (world == null || pos == null || state == null) {
			return;
		}
		world.setBlockState(pos, state);
		if (nbtTile != null) {
			nbtTile.setInteger("x", pos.getX());
			nbtTile.setInteger("y", pos.getY());
			nbtTile.setInteger("z", pos.getZ());
			CustomNPCsScheduler.runTack(() -> {
				TileEntity tile = world.getTileEntity(pos);
				if (tile == null) {
					tile = state.getBlock().createTileEntity(world, state);
				}
                assert tile != null;
                tile.readFromNBT(nbtTile);
				nbtTile.setInteger("x", pos.getX());
				nbtTile.setInteger("y", pos.getY());
				nbtTile.setInteger("z", pos.getZ());
			}, 200);
		}
	}

	public void setMeta(int metaIn) {
		meta = metaIn;
		if (meta < state.getBlock().getBlockState().getValidStates().size()) { state = state.getBlock().getBlockState().getValidStates().get(meta); }
	}

	@Override
	public String toString() {
        return "SchematicBlockData [ ID:" + id + "; state:" + state + "," + "; pos:" + pos
				+ "; meta:" + meta + "; hasNbt:" + (nbtTile != null) + " ]";
	}

}
