package noppes.npcs.api.entity.data;

import noppes.npcs.api.interfaces.ParamName;

public interface IPlayerMiniMap {

    IMiniMapData addPoint(@ParamName("dimensionId") String dimensionId);

    IMiniMapData[] getAllPoints();

    String getModName();

    IMiniMapData getPoint(@ParamName("id") int id);

    IMiniMapData getPoint(@ParamName("name") String name);

    IMiniMapData[] getPoints(@ParamName("dimensionId") String dimensionId);

    String[] getSpecificKeys();

    Object getSpecificValue(@ParamName("key") String key);

    boolean removePoint(@ParamName("id") int id);

    boolean removePoint(@ParamName("name") String name);

    boolean removePoints(@ParamName("dimensionId") String dimensionId);

}
