package noppes.npcs.api.functions.gui;

import noppes.npcs.api.interfaces.IgnoreForAPI;
import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.gui.ICustomGui;
import noppes.npcs.api.gui.IItemSlot;

@FunctionalInterface
@IgnoreForAPI
public interface GuiItemSlotUpdate {

    void onUpdate(@ParamName("gui") ICustomGui gui, @ParamName("slot") IItemSlot slot);

}
