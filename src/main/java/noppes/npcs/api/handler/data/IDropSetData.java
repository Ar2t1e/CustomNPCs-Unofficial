package noppes.npcs.api.handler.data;

import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.entity.data.DropSet;

public interface IDropSetData {

    int getNpcLevel();

    boolean removeDrop(@ParamName("dropSet") DropSet dropSet);

}
