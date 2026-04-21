package noppes.npcs.api.wrapper;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import noppes.npcs.api.block.IBlockFluidContainer;

public class BlockFluidContainerWrapper extends BlockWrapper implements IBlockFluidContainer {

	private final BlockFluidBase block;

	public BlockFluidContainerWrapper(World world, IBlockState state, BlockPos pos) {
		super(world, state, pos);
		block = (BlockFluidBase) state.getBlock();
	}

	@Override
	public String getFluidName() { return block.getFluid().getName(); }

	@Override
	public float getFluidPercentage() { return world == null ? 0.0f : block.getFilledPercentage(world.getMCWorld(), iPos.blockPos); }

	@Override
	public float getFluidValue() { return world == null ? 0.0f : block.getQuantaValue(world.getMCWorld(), iPos.blockPos); }

	@Override
	public float getFluidDensity() { return world == null ? 0.0f : BlockFluidBase.getDensity(world.getMCWorld(), iPos.blockPos); }

	@Override
	public float getFluidTemperature() { return world == null ? 0.0f : BlockFluidBase.getTemperature(world.getMCWorld(), iPos.blockPos); }

}
