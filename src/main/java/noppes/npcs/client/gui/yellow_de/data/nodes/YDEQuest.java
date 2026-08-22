package noppes.npcs.client.gui.yellow_de.data.nodes;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.Component;
import noppes.npcs.client.gui.yellow_de.data.EnumYDEType;
import noppes.npcs.client.gui.yellow_de.data.YDEData;
import noppes.npcs.client.gui.yellow_de.data.YDENode;

public class YDEQuest extends YDENode {

    public int questId;

    public YDEQuest(YDEData parent, int idIn, String categoryIn, int questIdIn) {
        super(parent);
        type = EnumYDEType.QUEST;
        id = idIn;
        width = 90;
        height = 60;
        category = categoryIn;
        questId = questIdIn;
    }

    @Override
    public void load(NBTTagCompound compound) {
        super.load(compound);
        type = EnumYDEType.QUEST;
        questId = compound.getInteger("QuestID");
    }

    @Override
    public NBTTagCompound save() {
        NBTTagCompound compound = super.save();
        compound.setInteger("QuestID", questId);
        return compound;
    }

    @Override
    public Component getTitle() { return Component.translatable("gui.quest", " ID: " + questId); }

}