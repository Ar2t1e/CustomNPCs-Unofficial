package noppes.npcs.client.gui.yellow_de.data.nodes;

import net.minecraft.nbt.CompoundTag;
import noppes.npcs.client.gui.yellow_de.data.EnumYDEType;
import noppes.npcs.client.gui.yellow_de.data.YDENode;

public class YDEOption extends YDENode {

    public int dialogId;
    public int slot;

    public YDEOption(int idIn, int categoryIdIn, int dialogIdIn, int slotIn) {
        type = EnumYDEType.OPTION;
        id = idIn;
        categoryId = categoryIdIn;
        dialogId = dialogIdIn;
        slot = slotIn;
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        type = EnumYDEType.OPTION;
        dialogId = compound.getInt("DialogID");
        slot = compound.getInt("SlotID");
    }

    @Override
    public CompoundTag save() {
        CompoundTag compound = super.save();
        compound.putInt("DialogID", dialogId);
        compound.putInt("SlotID", slot);
        return compound;
    }

}
