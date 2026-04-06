package noppes.npcs.api.block;

@SuppressWarnings("all")
public interface IBlockFluidContainer extends IBlock {

   float getFluidPercentage();

   float getFluidDensity();

   float getFluidTemperature();

   String getFluidName();

}
