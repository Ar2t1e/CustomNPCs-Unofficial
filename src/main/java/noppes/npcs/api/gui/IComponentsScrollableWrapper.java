package noppes.npcs.api.gui;

import noppes.npcs.api.interfaces.ParamName;

public interface IComponentsScrollableWrapper extends IComponentsWrapper {

    IComponentsScrollableWrapper init(@ParamName("x") int x, @ParamName("y") int y, @ParamName("width") int width, @ParamName("height") int height);

}
