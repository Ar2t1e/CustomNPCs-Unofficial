package noppes.npcs.api.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import noppes.npcs.api.*;
import noppes.npcs.api.entity.data.IData;
import noppes.npcs.api.interfaces.ParamName;

public interface IBlock {

   int getX();

   int getY();

   int getZ();

   IPos getPos();

   Object getProperty(@ParamName("name") String name);

   void setProperty(@ParamName("name") String name, @ParamName("value") Object value);

   String[] getProperties();

   String getName();

   void remove();

   boolean isRemoved();

   boolean isAir();

   IBlock setBlock(@ParamName("name") String name);

   IBlock setBlock(@ParamName("block") IBlock block);

   boolean hasTileEntity();

   boolean isContainer();

   IContainer getContainer();

   IData getTempdata();

   IData getStoreddata();

   IWorld getWorld();

   INbt getBlockEntityNBT();

   void setTileEntityNBT(@ParamName("nbt") INbt nbt);

   BlockEntity getMCTileEntity();

   Block getMCBlock();

   void blockEvent(@ParamName("type") int type,@ParamName("data")  int data);

   String getDisplayName();

   BlockState getMCBlockState();

   void interact(@ParamName("side") int side);

}
