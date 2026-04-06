package noppes.npcs.blocks.tiles;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import noppes.npcs.api.wrapper.data.Data;

import javax.annotation.Nonnull;

public class TileNpcEntity extends BlockEntity {

   public Data tempData = new Data();
   public Data storedData = new Data();

   public TileNpcEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
      super(type, pos, state);
   }

   public void load(@Nonnull CompoundTag compound) {
      super.load(compound);
      CompoundTag extraData = compound.getCompound("CustomNPCsData");
      if (!extraData.isEmpty()) { storedData.setNbt(extraData); }
   }

   public void saveAdditional(@Nonnull CompoundTag compound) {
      super.saveAdditional(compound);
      compound.put("CustomNPCsData", storedData.getNbt().getMCNBT());
   }

}
