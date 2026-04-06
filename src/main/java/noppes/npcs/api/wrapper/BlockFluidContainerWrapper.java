package noppes.npcs.api.wrapper;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.api.block.IBlockFluidContainer;

public class BlockFluidContainerWrapper extends BlockWrapper implements IBlockFluidContainer {

   private final IFluidBlock block;

   public BlockFluidContainerWrapper(Level level, Block block, BlockPos pos) {
      super(level, block, pos);
      this.block = (IFluidBlock)block;
   }

   public float getFluidPercentage() {
      return this.block.getFilledPercentage(this.level.getMCLevel(), this.pos);
   }

   public float getFluidDensity() {
      return (float)this.block.getFluid().getFluidType().getDensity(this.level.getMCLevel().getFluidState(this.pos), this.level.getMCLevel(), this.pos);
   }

   public float getFluidTemperature() {
      return (float)this.block.getFluid().getFluidType().getTemperature(this.level.getMCLevel().getFluidState(this.pos), this.level.getMCLevel(), this.pos);
   }

   public String getFluidName() {
      ResourceLocation registerName = ForgeRegistries.FLUID_TYPES.get().getKey(this.block.getFluid().getFluidType());
      return registerName != null ? registerName.toString() : "minecraft:air";
   }

}
