package noppes.npcs.client.gui.yellow_de.data;

import net.minecraft.nbt.CompoundTag;
import noppes.npcs.util.ValueUtil;

public class YDELink {

    public EnumYDEType type;
    public int back;
    public int next;

    public YDELink parent;

    public YDELink(int backNodeIdIn, int nextNodIdIn, EnumYDEType typeIn) {
        type = typeIn;
        back = backNodeIdIn;
        next = nextNodIdIn;
    }

    public CompoundTag save() {
        CompoundTag compound = new CompoundTag();
        compound.putInt("type", type.ordinal());
        compound.putInt("back", back);
        compound.putInt("next", next);
        return compound;
    }

    public void load(CompoundTag compound) {
        type = EnumYDEType.values()[ValueUtil.onlyPositiveInt(compound.getInt("type"), EnumYDEType.values().length)];
        back = compound.getInt("back");
        next = compound.getInt("next");
    }

}
