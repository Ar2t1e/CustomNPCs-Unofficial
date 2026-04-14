package noppes.npcs.client.gui.yellow_de.data.nodes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import noppes.npcs.client.gui.yellow_de.data.EnumYDEType;
import noppes.npcs.client.gui.yellow_de.data.YDEData;
import noppes.npcs.client.gui.yellow_de.data.YDENode;
import noppes.npcs.controllers.data.Dialog;

public class YDEQuest extends YDENode {

    public int questId;
    public Dialog dialog;

    public YDEQuest(YDEData parent, int idIn, String categoryIn, int questIdIn) {
        super(parent);
        type = EnumYDEType.QUEST;
        title = Component.translatable("gui.quest", " ID: " + questIdIn);

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

    @Override
    public void refresh() { }

}
