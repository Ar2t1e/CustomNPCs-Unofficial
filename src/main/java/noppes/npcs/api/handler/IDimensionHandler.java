package noppes.npcs.api.handler;

import noppes.npcs.api.INbt;
import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.handler.data.IWorldInfo;

import java.util.List;

@SuppressWarnings("unused")
public interface IDimensionHandler {

	IWorldInfo createNewDimension();

	void deleteDimension(@ParamName("dimensionId") int dimensionId);

    int copyDimension(int dimensionId);

    List<Integer> getAllIDs();

	IWorldInfo getMCWorldInfo(@ParamName("dimensionId") int dimensionId);

	INbt getNbt();

	void setNbt(@ParamName("nbt") INbt nbt);

	INbt getProviderInfo(int id);

}
