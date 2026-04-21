package noppes.npcs.api.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import noppes.npcs.api.*;
import noppes.npcs.api.entity.data.IData;
import noppes.npcs.api.interfaces.ParamName;

import java.util.List;

public interface IBlock {

   int getX();

   int getY();

   int getZ();

   IPos getPos();

   <T extends Comparable<T>> T getProperty(@ParamName("name") String name);

   @SuppressWarnings("unused")
   <T extends Comparable<T>> void setProperty(@ParamName("name") String name, @ParamName("value") Comparable<T> value);

   List<String> getProperties();

   String getName();

   void remove();

   boolean isRemoved();

   boolean isAir();

   IBlock setBlock(@ParamName("name") String name);

   IBlock setBlock(@ParamName("block") IBlock block);

   @SuppressWarnings("unused")
   boolean hasTileEntity();

   boolean isContainer();

   IContainer getContainer();

   IData getTempdata();

   IData getStoreddata();

   IWorld getWorld();

   INbt getBlockEntityNBT();

   void setTileEntityNBT(@ParamName("nbt") INbt nbt);

   @SuppressWarnings("unused")
   BlockEntity getMCTileEntity();

   Block getMCBlock();

   void blockEvent(@ParamName("type") int type,@ParamName("data")  int data);

   @SuppressWarnings("unused")
   String getStateName();

   String getDisplayName();

   BlockState getMCBlockState();

   void interact(@ParamName("side") int side);

   boolean isEmpty();

}
