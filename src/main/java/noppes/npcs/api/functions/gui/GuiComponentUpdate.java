package noppes.npcs.api.functions.gui;

import noppes.npcs.api.interfaces.IgnoreForAPI;
import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.gui.ICustomGui;
import noppes.npcs.api.gui.ICustomGuiComponent;

@FunctionalInterface
@IgnoreForAPI
public interface GuiComponentUpdate<T extends ICustomGuiComponent> {

   void onChange(@ParamName("gui") ICustomGui gui, @ParamName("component") T component);

}
