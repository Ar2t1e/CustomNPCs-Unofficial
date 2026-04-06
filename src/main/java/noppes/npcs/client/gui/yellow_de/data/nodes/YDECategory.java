package noppes.npcs.client.gui.yellow_de.data.nodes;

import net.minecraft.nbt.CompoundTag;
import noppes.npcs.client.gui.yellow_de.data.EnumYDEType;
import noppes.npcs.client.gui.yellow_de.data.YDENode;
import noppes.npcs.util.ValueUtil;

public class YDECategory extends YDENode {

    protected float scale = 1.0f;

    public YDECategory(int idIn, int categoryIdIn) {
        type = EnumYDEType.CATEGORY;
        id = idIn;
        categoryId = categoryIdIn;
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        type = EnumYDEType.CATEGORY;
        scale = ValueUtil.correctFloat(compound.getFloat("Scale"), 0.1f, 1.0f);
    }

    @Override
    public CompoundTag save() {
        CompoundTag compound = super.save();
        compound.putFloat("Scale", scale);
        return compound;
    }

    public float getScale() { return scale; }

    public void setScale(float scaleIn) { scale = ValueUtil.correctFloat(scaleIn, 0.1f, 1.0f); }

}
