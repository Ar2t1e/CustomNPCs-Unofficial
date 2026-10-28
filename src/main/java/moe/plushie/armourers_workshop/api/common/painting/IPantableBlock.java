package moe.plushie.armourers_workshop.api.common.painting;

import moe.plushie.armourers_workshop.api.common.skin.cubes.ICubeColour;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

@SuppressWarnings("unused")
public interface IPantableBlock {

	ICubeColour getColour(IBlockAccess world, BlockPos pos);

	int getColour(IBlockAccess world, BlockPos pos, EnumFacing facing);

	IPaintType getPaintType(IBlockAccess world, BlockPos pos, EnumFacing facing);

	boolean isRemoteOnly(IBlockAccess world, BlockPos pos, EnumFacing facing);

	boolean setColour(IBlockAccess world, BlockPos pos, byte[] rgb, EnumFacing facing);

	@Deprecated
	boolean setColour(IBlockAccess world, BlockPos pos, int colour, EnumFacing facing);

	void setPaintType(IBlockAccess world, BlockPos pos, IPaintType paintType, EnumFacing facing);

}
