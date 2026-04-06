package noppes.npcs.client.gui.yellow_de.data.nodes;

import net.minecraft.nbt.CompoundTag;
import noppes.npcs.client.gui.yellow_de.data.EnumYDEType;
import noppes.npcs.client.gui.yellow_de.data.YDENode;
import noppes.npcs.controllers.data.Dialog;

public class YDENpc extends YDENode {

    public Dialog.StartedNpcData npcData;

    public YDENpc(int idIn, String categoryIn, Dialog.StartedNpcData npcDataIn) {
        type = EnumYDEType.NPC;
        id = idIn;
        category = categoryIn;
        npcData = npcDataIn;
        width = 90;
        height = 60;
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        type = EnumYDEType.NPC;
        npcData = new Dialog.StartedNpcData(compound.getCompound("StartedNpcData"));
    }

    @Override
    public CompoundTag save() {
        CompoundTag compound = super.save();
        compound.put("StartedNpcData", npcData.save());
        return compound;
    }

}