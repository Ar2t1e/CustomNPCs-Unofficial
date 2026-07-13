package noppes.npcs.client.gui.yellow_de.data.nodes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import noppes.npcs.client.gui.yellow_de.data.EnumYDEType;
import noppes.npcs.client.gui.yellow_de.data.YDEData;
import noppes.npcs.client.gui.yellow_de.data.YDENode;

public class YDEArea extends YDENode {

    protected String name;

    public YDEArea(YDEData parent, int idIn, String categoryIn, String nameIn) {
        super(parent);
        id = idIn;
        type = EnumYDEType.AREA;
        category = categoryIn;
        name = nameIn;
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        type = EnumYDEType.AREA;
        name = compound.getString("name");
    }

    @Override
    public CompoundTag save() {
        CompoundTag compound = super.save();
        compound.putString("name", name);
        return compound;
    }

    @Override
    public Component getTitle() { return Component.translatable(name); }

}
