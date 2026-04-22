package noppes.npcs.client.gui.yellow_de.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.Component;
import noppes.npcs.util.ValueUtil;

import javax.annotation.Nonnull;

public abstract class YDENode {

    protected final @Nonnull YDEData parent;
    public EnumYDEType type = EnumYDEType.DIALOG;
    public String category = "";
    public int id = -1;
    public int x = 0;
    public int y = 0;
    public int width = 180;
    public int height = 120;
    public boolean isLock = false;

    public YDENode(@Nonnull YDEData parentIn) { parent = parentIn; }

    public void load(NBTTagCompound compound) {
        type = EnumYDEType.values()[ValueUtil.onlyPositiveInt(compound.getInteger("type"), EnumYDEType.values().length)];
        id = compound.getInteger("id");
        x = compound.getInteger("x");
        y = compound.getInteger("y");
        width = compound.getInteger("width");
        height = compound.getInteger("height");
        isLock = compound.getBoolean("isLock");
        category = compound.getString("category");
    }

    public NBTTagCompound save() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("type", type.ordinal());
        compound.setInteger("id", id);
        compound.setInteger("x", x);
        compound.setInteger("y", y);
        compound.setInteger("width", width);
        compound.setInteger("height", height);
        compound.setBoolean("isLock", isLock);
        compound.setString("category", category);
        return compound;
    }

    public @Nonnull YDEData getParent() { return parent; }

    public abstract Component getTitle();

}
