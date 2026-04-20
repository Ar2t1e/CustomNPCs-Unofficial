package noppes.npcs.client.gui.yellow_de.data;

import net.minecraft.nbt.CompoundTag;
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

    public void load(CompoundTag compound) {
        type = EnumYDEType.values()[ValueUtil.onlyPositiveInt(compound.getInt("type"), EnumYDEType.values().length)];
        id = compound.getInt("id");
        x = compound.getInt("x");
        y = compound.getInt("y");
        width = compound.getInt("width");
        height = compound.getInt("height");
        isLock = compound.getBoolean("isLock");
        category = compound.getString("category");
    }

    public CompoundTag save() {
        CompoundTag compound = new CompoundTag();
        compound.putInt("type", type.ordinal());
        compound.putInt("id", id);
        compound.putInt("x", x);
        compound.putInt("y", y);
        compound.putInt("width", width);
        compound.putInt("height", height);
        compound.putBoolean("isLock", isLock);
        compound.putString("category", category);
        return compound;
    }

    public @Nonnull YDEData getParent() { return parent; }

    public abstract Component getTitle();

}
