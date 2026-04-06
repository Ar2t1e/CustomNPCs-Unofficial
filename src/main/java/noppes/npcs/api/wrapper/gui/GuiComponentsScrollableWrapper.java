package noppes.npcs.api.wrapper.gui;

import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.gui.IComponentsScrollableWrapper;
import noppes.npcs.api.gui.ICustomGuiComponent;

public class GuiComponentsScrollableWrapper extends GuiComponentsWrapper implements IComponentsScrollableWrapper {

    private boolean enabled = false;
    public int x;
    public int y;
    public int width;
    public int height;
    public int scrollAmount = 0;
    public GuiComponentsWrapper parent;

    public GuiComponentsScrollableWrapper(GuiComponentsWrapper parentIn, IPlayer<?> player) {
        super(player);
        parent = parentIn;
    }

    public GuiComponentsScrollableWrapper init(int xIn, int yIn, int widthIn, int heightIn) {
        enabled = true;
        x = xIn;
        y = yIn;
        width = widthIn;
        height = heightIn;
        return this;
    }

    public NBTTagCompound getComponentNbt() {
        NBTTagCompound comp = super.getComponentNbt();
        comp.setBoolean("enabled", enabled);
        comp.setInteger("x", x);
        comp.setInteger("y", y);
        comp.setInteger("width", width);
        comp.setInteger("height", height);
        return comp;
    }

    public void setComponentNbt(NBTTagCompound comp) {
        super.setComponentNbt(comp);
        enabled = comp.getBoolean("enabled");
        x = comp.getInteger("x");
        y = comp.getInteger("y");
        width = comp.getInteger("width");
        height = comp.getInteger("height");
    }

    public boolean isVisible(ICustomGuiComponent component) {
        return component.getPosY() >= scrollAmount && component.getPosY() + component.getHeight() <= height + scrollAmount;
    }

}
