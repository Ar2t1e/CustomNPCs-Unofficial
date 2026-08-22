package noppes.npcs.api.block;

@SuppressWarnings("all")
public interface IBlockFluidContainer extends IBlock {

	String getFluidName();

	float getFluidPercentage();

	float getFluidValue();

	float getFluidDensity();

	float getFluidTemperature();

}
