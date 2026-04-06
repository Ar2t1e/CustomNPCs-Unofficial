package noppes.npcs.api.functions.gui;

import noppes.npcs.api.interfaces.IgnoreForAPI;
import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.gui.ICustomGui;
import noppes.npcs.api.gui.ICustomGuiComponent;

@FunctionalInterface
@IgnoreForAPI
public interface GuiComponentClicked<T extends ICustomGuiComponent> {

    void onClick(@ParamName("gui") ICustomGui gui, @ParamName("component") T component);

}
