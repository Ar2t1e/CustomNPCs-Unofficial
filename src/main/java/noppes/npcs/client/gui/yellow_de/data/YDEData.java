package noppes.npcs.client.gui.yellow_de.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import noppes.npcs.client.gui.yellow_de.data.nodes.*;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.DialogCategory;
import noppes.npcs.controllers.data.DialogOption;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.ValueUtil;

import javax.annotation.Nonnull;
import java.util.*;

public class YDEData {

    public final Map<Integer, YDENode> nodes = new TreeMap<>();

    public YDEData(ListTag listNodes) {
        for (int i = 0; i < listNodes.size(); i++) {
            CompoundTag compound = listNodes.getCompound(i);
            YDENode node = switch (EnumYDEType.values()[ValueUtil.onlyPositiveInt(compound.getInt("Type"), EnumYDEType.values().length)]) {
                case CATEGORY -> new YDECategory(-1, "");
                case NPC -> new YDENpc(-1, "", null);
                case OPTION -> new YDEOption(-1, "", new DialogOption());
                case QUEST -> new YDEQuest(-1, "", -1);
                case AREA -> new YDEArea("");
                default -> new YDEDialog(-1, "", -1);
            };
            try {
                node.load(listNodes.getCompound(i));
                nodes.put(node.id, node);
            }
            catch (Exception e) { LogWriter.error(e); }
        }
    }

    public YDEData() {
        check();
        resetYpos();
    }

    public YDEData check() {
        // process categories
        for (DialogCategory category : DialogController.instance.categories.values()) {
            YDECategory yde_category = null;
            for (YDENode node : nodes.values()) {
                if (node instanceof YDECategory catNode) {
                    if (catNode.category.equals(category.title)) { yde_category = catNode; }
                }
            }
            if (yde_category == null) {
                yde_category = new YDECategory(nodes.size(), category.title);
                if (category.id > -1) {
                    yde_category.title = Component.literal("ID: " + category.id);
                }
            }
            nodes.put(yde_category.id, yde_category);
        }
        // process dialogues
        for (DialogCategory category : DialogController.instance.categories.values()) {
            for (Dialog dialog : category.dialogs.values()) {
                YDEDialog yde_dialog = getDialog(dialog.id);
                if (yde_dialog == null) {
                    yde_dialog = new YDEDialog(nodes.size(), category.title, dialog.id);
                    nodes.put(yde_dialog.id, yde_dialog);
                    processDialog(yde_dialog, 0, getLastY(yde_dialog));
                }
            }
        }
        return this;
    }

    private void processDialog(YDEDialog yde_dialog, int x, int y) {
        yde_dialog.x = x;
        yde_dialog.y = y;
        Dialog dialog = DialogController.instance.get(yde_dialog.dialogId);
        if (dialog != null) {
            if (dialog.quest > -1) {
                YDEQuest yde_quest = getQuest(yde_dialog.category, dialog.quest);
                if (yde_quest == null) {
                    yde_quest = new YDEQuest(nodes.size(), yde_dialog.category, dialog.quest);
                    yde_quest.x = x + 45;
                    yde_quest.y = yde_dialog.y + 140;
                    nodes.put(yde_quest.id, yde_quest);
                }
                yde_dialog.links.add(new YDELink(yde_dialog.id, yde_quest.id, EnumYDEType.QUEST));
            }
            if (!dialog.startedNpcs.isEmpty()) {
                for (Dialog.StartedNpcData npcData : dialog.startedNpcs) {
                    YDENpc yde_npc = getNpc(yde_dialog.category, npcData);
                    if (yde_npc == null) {
                        yde_npc = new YDENpc(nodes.size(), yde_dialog.category, npcData);
                        yde_npc.x = x - 100;
                        yde_npc.y = getLastY(yde_npc);
                        nodes.put(yde_npc.id, yde_npc);
                    }
                    yde_dialog.links.add(new YDELink(yde_dialog.id, yde_npc.id, EnumYDEType.NPC));
                }
            }
            if (!dialog.options.isEmpty()) {
                x += 200;
                for (Map.Entry<Integer, DialogOption> entry : dialog.options.entrySet()) {
                    entry.getValue().slot = entry.getKey();
                    YDEOption yde_option = getOption(entry.getValue());
                    if (yde_option == null) {
                        yde_option = new YDEOption(nodes.size(), yde_dialog.category, entry.getValue());
                        yde_option.x = x;
                        yde_option.y = getLastY(yde_option);
                        nodes.put(yde_option.id, yde_option);
                        if (entry.getValue().hasDialogs()) {
                            for (DialogOption.OptionDialogID optionDialog : entry.getValue().dialogs) {
                                YDEDialog yde_next_dialog = getDialog(optionDialog.dialogId);
                                if (yde_next_dialog == null) {
                                    yde_next_dialog = new YDEDialog(nodes.size(), yde_dialog.category, optionDialog.dialogId);
                                    Dialog nextDialog = DialogController.instance.get(optionDialog.dialogId);
                                    if (nextDialog == null) {
                                        DialogCategory category = DialogController.instance.getCategory(yde_dialog.category);
                                        nextDialog = new Dialog(category);
                                        nextDialog.id = optionDialog.dialogId;
                                        DialogController.instance.saveDialog(category, nextDialog);
                                    }
                                    yde_option.links.add(new YDELink(yde_option.id, yde_next_dialog.id, EnumYDEType.DIALOG));
                                    nodes.put(yde_next_dialog.id, yde_next_dialog);
                                    processDialog(yde_next_dialog, x + 200, yde_option.y);
                                } // Dialogue not found in mod data
                            }
                        }
                    }
                    else {
                        yde_option.title = Component.translatable("gui.answer").append(" # ")
                            .append(Component.translatable("gui.several"));
                    }
                    yde_dialog.links.add(new YDELink(yde_dialog.id, yde_option.id, EnumYDEType.OPTION));
                }
            }
        }
    }

    private int getLastY(YDENode node) {
        int yMax = 0;
        for (YDENode n : new ArrayList<>(nodes.values())) {
            if (n instanceof YDECategory || n instanceof YDEArea || n.equals(node)) { continue; }
            if (n.x + n.width >= node.x && n.x < node.x + node.width) {
                int y = n.y + n.height + 20;
                if (yMax < y) { yMax = y; }
            }
        }
        return yMax;
    }

    private void resetYpos() {
        List<Map<Integer, YDENode>> tempList = new ArrayList<>();
        int x = 0;
        // rows
        while (true) {
            Map<Integer, YDENode> map = new TreeMap<>();
            for (YDENode node : new ArrayList<>(nodes.values())) {
                if (node instanceof YDECategory || node instanceof YDEArea) { continue; }
                boolean added = true;
                for (Map<Integer, YDENode> tempNodes : tempList) {
                    for (YDENode n : tempNodes.values()) {
                        if (n.equals(node)) {
                            added = false;
                            break;
                        }
                    }
                }
                if (added && node.x + node.width >= x && node.x < x + 200) { map.put(node.y, node); }
            }
            if (map.isEmpty()) { break; }
            else {
                tempList.add(map);
                x += 200;
            }
        }
        // y sets
        int yCenter = 0;
        for (Map<Integer, YDENode> tempNodes : tempList) {
            List<YDENode> hasLinks = new ArrayList<>();
            int y = 0;
            for (YDENode node : tempNodes.values()) {
                y += node.height + 20;
                if ((node instanceof YDEDialog || node instanceof YDEOption) && !node.links.isEmpty()) { hasLinks.add(node); }
            }
            y -= 20;
            y /= -2;
            for (YDENode node : tempNodes.values()) {
                node.y = yCenter + y;
                y += node.height + 20;
            }
            if (!hasLinks.isEmpty()) {
                yCenter = 0;
                for (YDENode node : hasLinks) {
                    yCenter += node.y + node.height / 2;
                }
                if (hasLinks.size() > 1) { yCenter /= 2; }
            }
        }
    }

    public YDEArea getArea(String category, int areaId) {
        for (YDENode node : nodes.values()) {
            if (node instanceof YDEArea area && area.category.equals(category) && area.id == areaId) { return area; }
        }
        return null;
    }

    public YDEOption getOption(DialogOption optionIn) {
        for (YDENode node : nodes.values()) {
            if (node.type == EnumYDEType.OPTION && node instanceof YDEOption option &&
                    option.option.equals(optionIn)) { return option; }
        }
        return null;
    }

    public YDENpc getNpc(String category, Dialog.StartedNpcData npcData) {
        for (YDENode node : nodes.values()) {
            if (node.type == EnumYDEType.NPC && node instanceof YDENpc npc &&
                    npc.category.equals(category) && npc.npcData.equals(npcData)) { return npc; }
        }
        return null;
    }

    public YDEQuest getQuest(String category, int questId) {
        for (YDENode node : nodes.values()) {
            if (node.type == EnumYDEType.QUEST && node instanceof YDEQuest quest &&
                    quest.category.equals(category) && quest.questId == questId) { return quest; }
        }
        return null;
    }

    public YDEDialog getDialog(int dialogId) {
        for (YDENode node : nodes.values()) {
            if (node.type == EnumYDEType.DIALOG && node instanceof YDEDialog dialog && dialog.dialogId == dialogId) { return dialog; }
        }
        return null;
    }

    public @Nonnull YDECategory getCategory(String category) {
        YDECategory empty = null;
        for (YDENode node : nodes.values()) {
            if (node instanceof YDECategory cat) {
                if (cat.category.equals(category)) { return cat; }
                if (cat.category.isEmpty()) { empty = cat; }
            }
        }
        if (empty == null) { nodes.put(-1, empty = new YDECategory(-1, "")); }
        return empty;
    }

    public ListTag save() {
        ListTag listNodes = new ListTag();
        for (YDENode node : nodes.values()) { listNodes.add(node.save()); }
        return listNodes;
    }

}
