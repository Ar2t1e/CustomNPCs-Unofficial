package noppes.npcs.api.wrapper.gui;

import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.constants.GuiComponentType;
import noppes.npcs.api.gui.ITextArea;

public class CustomGuiTextAreaWrapper extends CustomGuiTextFieldWrapper implements ITextArea {

    private boolean codeTheme = false;

    public CustomGuiTextAreaWrapper() {}

    public CustomGuiTextAreaWrapper(int id, int x, int y, int width, int height) {
        super(id, x, y, width, height);
    }

    public int getType() { return GuiComponentType.TEXT_AREA.get(); }

    public NBTTagCompound toNBT(NBTTagCompound nbt) {
        super.toNBT(nbt);
        nbt.setBoolean("codetheme", this.codeTheme);
        return nbt;
    }

    public CustomGuiComponentWrapper fromNBT(NBTTagCompound nbt) {
        super.fromNBT(nbt);
        this.setCodeTheme(nbt.getBoolean("codetheme"));
        return this;
    }

    public void setCodeTheme(boolean bo) {
        this.codeTheme = bo;
    }

    public boolean getCodeTheme() {
        return this.codeTheme;
    }

}
