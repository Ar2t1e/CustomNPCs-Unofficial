package noppes.npcs.schematics;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.block.state.BlockState;
import noppes.npcs.api.IPos;

public interface ISchematic {

   short getWidth();

   short getHeight();

   short getLength();

   int getBlockEntityDimensions();

   CompoundTag getBlockEntity(int var1);

   String getName();

   BlockState getBlockState(int var1, int var2, int var3);

   BlockState getBlockState(int var1);

   CompoundTag getNBT();

   IPos getOffset();

   // New from Unofficial (BetaZavr)
   boolean hasEntitys();

   ListTag getEntitys();

}
