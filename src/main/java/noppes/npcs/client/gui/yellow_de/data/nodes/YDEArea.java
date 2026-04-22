package noppes.npcs.client.gui.yellow_de.data.nodes;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.Component;
import noppes.npcs.client.gui.yellow_de.data.EnumYDEType;
import noppes.npcs.client.gui.yellow_de.data.YDEData;
import noppes.npcs.client.gui.yellow_de.data.YDENode;

public class YDEArea extends YDENode {

    protected String name;

    public YDEArea(YDEData parent, String categoryIn, String nameIn) {
        super(parent);
        type = EnumYDEType.AREA;
        category = categoryIn;
        name = nameIn;
    }

    @Override
    public void load(NBTTagCompound compound) {
        super.load(compound);
        type = EnumYDEType.AREA;
        name = compound.getString("name");
    }

    @Override
    public NBTTagCompound save() {
        NBTTagCompound compound = super.save();
        compound.setString("name", name);
        return compound;
    }

    @Override
    public Component getTitle() { return Component.translatable(name); }

}
