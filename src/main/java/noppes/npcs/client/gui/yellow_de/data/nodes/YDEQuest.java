package noppes.npcs.client.gui.yellow_de.data.nodes;

import net.minecraft.nbt.CompoundTag;
import noppes.npcs.client.gui.yellow_de.data.EnumYDEType;
import noppes.npcs.client.gui.yellow_de.data.YDENode;

public class YDEQuest extends YDENode {

    public int questId;

    public YDEQuest(int idIn, String categoryIn, int questIdIn) {
        type = EnumYDEType.QUEST;
        id = idIn;
        width = 90;
        height = 60;
        category = categoryIn;
        questId = questIdIn;
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        type = EnumYDEType.QUEST;
        questId = compound.getInt("QuestID");
    }

    @Override
    public CompoundTag save() {
        CompoundTag compound = super.save();
        compound.putInt("QuestID", questId);
        return compound;
    }

}
