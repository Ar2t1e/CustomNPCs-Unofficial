package noppes.npcs.client.gui.yellow_de.data.nodes;

import net.minecraft.nbt.CompoundTag;
import noppes.npcs.client.gui.yellow_de.data.EnumYDEType;
import noppes.npcs.client.gui.yellow_de.data.YDENode;

public class YDEArea extends YDENode {

    public YDEArea(int categoryIdIn) {
        type = EnumYDEType.AREA;
        categoryId = categoryIdIn;
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        type = EnumYDEType.AREA;
    }

}
