package noppes.npcs.api.entity.data;

import noppes.npcs.api.IPos;
import noppes.npcs.api.interfaces.ParamName;

import java.util.List;

public interface IMiniMapData {

    int getColor();

    List<String> getDimensions();

    String getIcon();

    int getId();

    String getName();

    IPos getPos();

    List<String> getSpecificKeys();

    String getSpecificValue(@ParamName("key") String key);

    String getType();

    boolean isEnable();

    void setColor(@ParamName("color") int color);

    void setDimensions(@ParamName("dimensions") String ... dimensions);

    void setIcon(@ParamName("icon") String icon);

    void setName(@ParamName("name") String name);

    void setPos(@ParamName("x") int x, @ParamName("y") int y, @ParamName("z") int z);

    void setPos(@ParamName("pos") IPos pos);

    void setType(@ParamName("type") String type);

}
