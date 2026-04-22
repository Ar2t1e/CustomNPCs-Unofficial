package noppes.npcs.client.gui.yellow_de.data;

import net.minecraft.nbt.NBTTagCompound;
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

    public NBTTagCompound save() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("type", type.ordinal());
        compound.setInteger("back", back);
        compound.setInteger("next", next);
        return compound;
    }

    public void load(NBTTagCompound compound) {
        type = EnumYDEType.values()[ValueUtil.onlyPositiveInt(compound.getInteger("type"), EnumYDEType.values().length)];
        back = compound.getInteger("back");
        next = compound.getInteger("next");
    }

}
