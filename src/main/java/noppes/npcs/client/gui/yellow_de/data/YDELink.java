package noppes.npcs.client.gui.yellow_de.data;

import net.minecraft.nbt.CompoundTag;
import noppes.npcs.util.ValueUtil;

public class YDELink {

    public EnumYDEType type;
    public int backNodeId;
    public int nextNodId;

    public YDELink(int backNodeIdIn, int nextNodIdIn, EnumYDEType typeIn) {
        type = typeIn;
        backNodeId = backNodeIdIn;
        nextNodId = nextNodIdIn;
    }

    public CompoundTag save() {
        CompoundTag compound = new CompoundTag();
        compound.putInt("Type", type.ordinal());
        compound.putInt("BackNodeId", backNodeId);
        compound.putInt("NextNodeId", nextNodId);
        return compound;
    }

    public void load(CompoundTag compound) {
        type = EnumYDEType.values()[ValueUtil.onlyPositiveInt(compound.getInt("Type"), EnumYDEType.values().length)];
        backNodeId = compound.getInt("BackNodeId");
        nextNodId = compound.getInt("NextNodeId");
    }

}
