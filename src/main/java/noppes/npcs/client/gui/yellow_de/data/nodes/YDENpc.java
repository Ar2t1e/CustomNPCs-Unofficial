package noppes.npcs.client.gui.yellow_de.data.nodes;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.Component;
import noppes.npcs.client.gui.yellow_de.data.EnumYDEType;
import noppes.npcs.client.gui.yellow_de.data.YDEData;
import noppes.npcs.client.gui.yellow_de.data.YDENode;
import noppes.npcs.controllers.data.Dialog;

public class YDENpc extends YDENode {

    public Dialog.StartedNpcData npcData;

    public YDENpc(YDEData parent, int idIn, String categoryIn, Dialog.StartedNpcData npcDataIn) {
        super(parent);
        type = EnumYDEType.NPC;

        id = idIn;
        category = categoryIn;
        npcData = npcDataIn;
        width = 90;
        height = 60;
    }

    @Override
    public void load(NBTTagCompound compound) {
        super.load(compound);
        type = EnumYDEType.NPC;
        npcData = new Dialog.StartedNpcData(compound.getCompoundTag("StartedNpcData"));
    }

    @Override
    public NBTTagCompound save() {
        NBTTagCompound compound = super.save();
        compound.setTag("StartedNpcData", npcData.save());
        return compound;
    }

    @Override
    public Component getTitle() { return Component.literal("NPC: "); }

}