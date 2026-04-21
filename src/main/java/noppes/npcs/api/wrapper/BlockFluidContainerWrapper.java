package noppes.npcs.api.wrapper;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.api.block.IBlockFluidContainer;

public class BlockFluidContainerWrapper extends BlockWrapper implements IBlockFluidContainer {

   private final IFluidBlock block;

   public BlockFluidContainerWrapper(Level level, BlockState state, BlockPos pos) {
      super(level, state, pos);
      block = (IFluidBlock) state.getBlock();
   }

   @Override
   public float getFluidPercentage() {
      return level == null ? 0.0f :
              block.getFilledPercentage(level.getMCLevel(), iPos.blockPos);
   }

   @Override
   public float getFluidDensity() {
      return level == null ? 0.0f :
              (float) block.getFluid().getFluidType().getDensity(level.getMCLevel().getFluidState(iPos.blockPos),
                      level.getMCLevel(), iPos.blockPos);
   }

   @Override
   public float getFluidTemperature() {
      return level == null ? 0.0f :
              (float) block.getFluid().getFluidType().getTemperature(level.getMCLevel().getFluidState(iPos.blockPos),
                      level.getMCLevel(), iPos.blockPos);
   }

   @Override
   public String getFluidName() {
      ResourceLocation registerName = ForgeRegistries.FLUID_TYPES.get().getKey(block.getFluid().getFluidType());
      return registerName != null ? registerName.toString() : "minecraft:air";
   }

}
