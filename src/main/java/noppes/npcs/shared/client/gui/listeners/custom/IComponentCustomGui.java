package noppes.npcs.shared.client.gui.listeners.custom;

import noppes.npcs.api.gui.ICustomGuiComponent;
import noppes.npcs.shared.client.gui.listeners.IComponentGui;

public interface IComponentCustomGui extends IComponentGui {

    void init();

    ICustomGuiComponent component();

}
