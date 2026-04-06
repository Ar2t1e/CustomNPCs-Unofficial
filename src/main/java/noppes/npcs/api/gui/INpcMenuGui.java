package noppes.npcs.api.gui;

import noppes.npcs.api.interfaces.IgnoreForAPI;
import noppes.npcs.api.interfaces.ParamName;

@IgnoreForAPI
public interface INpcMenuGui {

    void setMenuData(@ParamName("display") boolean display, @ParamName("stats") boolean stats, @ParamName("ai") boolean ai, @ParamName("inventory") boolean inventory, @ParamName("advanced") boolean advanced);

}
