package noppes.npcs.api.block;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import noppes.npcs.api.*;
import noppes.npcs.api.entity.data.IData;
import noppes.npcs.api.interfaces.ParamName;

import java.util.List;

public interface IBlock {

	<T extends Comparable<T>> T getProperty(String name);

	<T extends Comparable<T>> void setProperty(String name, Comparable<T> value);

	List<String> getProperties();

	@SuppressWarnings("unused")
	String getStateName();

	@SuppressWarnings("unused")
	void blockEvent(@ParamName("type") int type, @ParamName("data") int data);

	IContainer getContainer();

	String getDisplayName();

	Block getMCBlock();

	IBlockState getMCBlockState();

	@SuppressWarnings("unused")
	TileEntity getMCTileEntity();

	boolean isEmpty();

	int getMetadata();

	String getName();

	IPos getPos();

	IData getStoreddata();

	IData getTempdata();

	INbt getBlockEntityNBT();

	IWorld getWorld();

	int getX();

	int getY();

	int getZ();

	boolean hasTileEntity();

	void interact(@ParamName("side") int side);

	boolean isAir();

	boolean isContainer();

	boolean isRemoved();

	void remove();

	IBlock setBlock(@ParamName("block") IBlock block);

	IBlock setBlock(@ParamName("name") String name);

	void setMetadata(@ParamName("meta") int meta);

	void setTileEntityNBT(@ParamName("nbt") INbt nbt);

}
