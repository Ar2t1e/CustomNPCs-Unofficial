package noppes.npcs.api.handler;

import noppes.npcs.api.INbt;
import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.handler.data.IWorldInfo;

public interface IDimensionHandler {

	IWorldInfo createDimension();

	void deleteDimension(@ParamName("dimensionId") int dimensionId);

	int[] getAllIDs();

	IWorldInfo getMCWorldInfo(@ParamName("dimensionId") int dimensionId);

	INbt getNbt();

	void setNbt(@ParamName("nbt") INbt nbt);

}
