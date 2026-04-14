package noppes.npcs.client.gui.yellow_de.data.nodes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import noppes.npcs.client.gui.yellow_de.data.EnumYDEType;
import noppes.npcs.client.gui.yellow_de.data.YDEData;
import noppes.npcs.client.gui.yellow_de.data.YDENode;
import noppes.npcs.controllers.data.Dialog;

public class YDENpc extends YDENode {

    public Dialog.StartedNpcData npcData;
    public Dialog dialog;

    public YDENpc(YDEData parent, int idIn, String categoryIn, Dialog.StartedNpcData npcDataIn) {
        super(parent);
        type = EnumYDEType.NPC;
        title = Component.literal("NPC: ");

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

    @Override
    public void refresh() { }

}