package noppes.npcs.shared.client.gui.components;

import noppes.npcs.shared.client.gui.listeners.IGuiInterface;

public class GuiButtonYesNo extends GuiButtonNop {

    public GuiButtonYesNo(IGuiInterface gui, int id, int x, int y, boolean bo, OnPress onPress) {
        super(gui, id, x, y, onPress == null ? clicked : onPress, bo ? 1 : 0, "gui.no", "gui.yes");
        setSize(50, 20);
    }

    public GuiButtonYesNo(IGuiInterface gui, int id, int x, int y, boolean bo) {
        super(gui, id, x, y, clicked, bo ? 1 : 0, "gui.no", "gui.yes");
        setSize(50, 20);
    }

    @Override
    protected boolean isValidClickButton(int mouseButton) { return mouseButton == 0; }

    public boolean getBoolean() { return getValue() == 1; }

}

