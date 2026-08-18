package noppes.npcs.dimensions;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProviderSurface;
import noppes.npcs.controllers.DimensionController;

import javax.annotation.Nonnull;

public class CustomWorldProvider extends WorldProviderSurface {

	protected final DimensionType dimensionType;
	private final NBTTagCompound nbtData;

	public CustomWorldProvider(DimensionType dimensionTypeIn) {
		super();
		dimensionType = dimensionTypeIn;
		nbtData = DimensionController.getInstance().getProviderInfo(dimensionType.getId()).getMCNBT();
	}

	@Override
	public @Nonnull DimensionType getDimensionType() { return dimensionType; }

	@Override
	public boolean isSurfaceWorld() { return nbtData.hasKey("isSurfaceWorld", 1) && nbtData.getBoolean("isSurfaceWorld"); }

}
