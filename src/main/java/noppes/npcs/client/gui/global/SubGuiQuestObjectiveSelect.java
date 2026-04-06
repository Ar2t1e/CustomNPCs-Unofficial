package noppes.npcs.client.gui.global;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.client.gui.questtypes.*;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.client.gui.util.quests.QuestObjective;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.constants.EnumQuestTask;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketQuestOpen;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;

// New from Unofficial (BetaZavr)
public class SubGuiQuestObjectiveSelect extends GuiNPCInterface {

    public Quest quest;
    public Screen parent;

    public SubGuiQuestObjectiveSelect(Screen gui) {
        setBackground("companion_empty.png");
        imageWidth = 172;
        imageHeight = 167;
        closeOnEsc = true;

        parent = gui;
        quest = NoppesUtilServer.getEditingQuest(player);
    }

    public void buttonEvent(GuiButtonNop guiButton) {
        QuestObjective task;
        switch (guiButton.id) {
            case 66: onClose(); return;
            case 71: {
                task = (QuestObjective) quest.addTask();
                if (task == null) { return; }
                task.setType(EnumQuestTask.ITEM);
                SubGuiNpcQuestTypeItem.parent = this;
                Packets.sendServer(new SPacketQuestOpen(EnumGuiType.QuestTypeItem, quest.save(new CompoundTag()), quest.questInterface.getPos(task)));
                return;
            } // collect item
            case 72: {
                task = (QuestObjective) quest.addTask();
                if (task == null) { return; }
                task.setType(EnumQuestTask.CRAFT);
                SubGuiNpcQuestTypeItem.parent = this;
                Packets.sendServer(new SPacketQuestOpen(EnumGuiType.QuestTypeItem, quest.save(new CompoundTag()), quest.questInterface.getPos(task)));
                return;
            } // craft item
            case 73: {
                task = (QuestObjective) quest.addTask();
                if (task == null) { return; }
                task.setType(EnumQuestTask.KILL);
                setSubGui(new SubGuiNpcQuestTypeKill(npc, task, parent));
                return;
            } // kill
            case 74: {
                task = (QuestObjective) quest.addTask();
                if (task == null) { return; }
                task.setType(EnumQuestTask.AREAKILL);
                setSubGui(new SubGuiNpcQuestTypeKill(npc, task, parent));
                return;
            } // area kill
            case 75: {
                task = (QuestObjective) quest.addTask();
                if (task == null) { return; }
                task.setType(EnumQuestTask.DIALOG);
                setSubGui(new SubGuiNpcQuestTypeDialog(npc, task, parent));
                return;
            } // dialog
            case 76: {
                task = (QuestObjective) quest.addTask();
                if (task == null) { return; }
                task.setType(EnumQuestTask.LOCATION);
                setSubGui(new SubGuiNpcQuestTypeLocation(npc, task, parent));
                return;
            } // location
            case 77: {
                task = (QuestObjective) quest.addTask();
                if (task == null) { return; }
                task.setType(EnumQuestTask.MANUAL);
                setSubGui(new SubGuiNpcQuestTypeManual(npc, task, parent));
            } // manual
        }
    }

    @Override
    public void init() {
        super.init();
        int x0 = guiLeft + 4;
        int x1 = x0 + 83;
        int y = guiTop + 5;
        addLabel(80, x0, y, "task.chose")
                .setSize(imageWidth - 8, 10);
        addButton(71, x0, y += 12, "enum.quest.item")
                .setSize(80, 20)
                .setHoverTexts("drop.hover.task.0");
        addButton(72, x1, y, "enum.quest.craft")
                .setSize(80, 20)
                .setHoverTexts("drop.hover.task.1");
        addButton(73, x0, y += 22, "enum.quest.kill")
                .setSize(80, 20)
                .setHoverTexts("drop.hover.task.2");
        addButton(74, x1, y, "enum.quest.area_kill")
                .setSize(80, 20)
                .setHoverTexts("drop.hover.task.3");
        addButton(75, x0, y += 22, "enum.quest.dialog")
                .setSize(80, 20)
                .setHoverTexts("drop.hover.task.4");
        addButton(76, x1, y, "enum.quest.location")
                .setSize(80, 20)
                .setHoverTexts("drop.hover.task.5");
        addButton(77, x0, y + 22, "enum.quest.manual")
                .setSize(80, 20)
                .setHoverTexts("drop.hover.task.6");
        addButton(66, x0, guiTop + 142, "gui.back")
                .setSize(80, 20)
                .setHoverTexts("hover.back");
    }

}
