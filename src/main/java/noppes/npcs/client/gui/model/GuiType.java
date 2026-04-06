package noppes.npcs.client.gui.model;

import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;

public abstract class GuiType {

    public String name;

    public GuiType(String nameIn) { name = nameIn; }

    public void init() { }

    public void buttonEvent(GuiButtonNop button) { }

    public void scrollClicked(GuiCustomScrollNop scroll) { }

    public void unFocused(GuiTextFieldNop textfield) { }

}