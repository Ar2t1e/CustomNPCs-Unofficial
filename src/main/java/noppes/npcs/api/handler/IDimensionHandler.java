package noppes.npcs.api.handler;

import noppes.npcs.api.INbt;
import noppes.npcs.api.handler.data.IWorldInfo;
import noppes.npcs.api.interfaces.ParamName;

public interface IDimensionHandler {

    IWorldInfo createDimension();

    void deleteDimension(@ParamName("dimensionId") String dimensionId);

    int[] getAllIDs();

    IWorldInfo getMCWorldInfo(@ParamName("dimensionId") String dimensionId);

    INbt getNbt();

    void setNbt(@ParamName("nbt") INbt nbt);

}
