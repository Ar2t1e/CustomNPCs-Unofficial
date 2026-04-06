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
                case OPTION -> new YDEOption(-1, "", -1, -1);
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

    public YDEData() { }

    private int processDialog(YDEDialog yde_dialog, int x, int y) {
        int yMax = y + 140;
        yde_dialog.x = x;
        yde_dialog.y = y;
        Dialog dialog = DialogController.instance.get(yde_dialog.dialogId);
        if (dialog != null) {
            if (dialog.quest > -1) {
                YDEQuest yde_quest = getQuest(yde_dialog.category, dialog.quest);
                if (yde_quest == null) {
                    yde_quest = new YDEQuest(nodes.size(), yde_dialog.category, dialog.quest);
                    yde_quest.title = Component.translatable("gui.quest", " ID: " + dialog.quest);
                    yde_quest.x = x + 45;
                    yde_quest.y = yde_dialog.y + 140;
                    nodes.put(yde_quest.id, yde_quest);
                }
                yde_dialog.links.add(new YDELink(yde_dialog.id, yde_quest.id, EnumYDEType.QUEST));
            }
            if (!dialog.startedNpcs.isEmpty()) {
                int yN = 120;
                for (Dialog.StartedNpcData npcData : dialog.startedNpcs) {
                    YDENpc yde_npc = getNpc(yde_dialog.category, npcData);
                    if (yde_npc == null) {
                        yde_npc = new YDENpc(nodes.size(), yde_dialog.category, npcData);
                        yde_npc.title = Component.literal("NPC: ");
                        yde_npc.x = x - 100;
                        yde_npc.y = yN;
                        nodes.put(yde_npc.id, yde_npc);
                    }
                    yde_dialog.links.add(new YDELink(yde_dialog.id, yde_npc.id, EnumYDEType.NPC));
                    yN += 20;
                }
            }
            if (!dialog.options.isEmpty()) {
                x += 200;
                int yN = 0;
                for (Map.Entry<Integer, DialogOption> entry : dialog.options.entrySet()) {
                    entry.getValue().slot = entry.getKey();
                    YDEOption yde_option = new YDEOption(nodes.size(), yde_dialog.category, yde_dialog.dialogId, entry.getKey());
                    yde_option.title = Component.translatable("gui.answer").append(Component.literal(" # " + entry.getKey()));
                    yde_option.x = x;
                    yde_option.y = y + yN;
                    yde_dialog.links.add(new YDELink(yde_dialog.id, yde_option.id, EnumYDEType.OPTION));
                    nodes.put(yde_option.id, yde_option);
                    if (entry.getValue().hasDialogs()) {
                        for (DialogOption.OptionDialogID optionDialog : entry.getValue().dialogs) {
                            YDEDialog yde_next_dialog = getDialog(optionDialog.dialogId);
                            if (yde_next_dialog == null) {
                                yde_next_dialog = new YDEDialog(nodes.size(), yde_dialog.category, optionDialog.dialogId);
                                yde_next_dialog.title = Component.translatable("dialog.dialog").append(Component.literal("ID: " + optionDialog.dialogId));
                                Dialog nextDialog = DialogController.instance.get(optionDialog.dialogId);
                                LogWriter.info("TEST: next dialog id "+optionDialog.dialogId+" - "+nextDialog);
                                if (nextDialog == null) {
                                    DialogCategory category = DialogController.instance.getCategory(yde_dialog.category);
                                    nextDialog = new Dialog(category);
                                    nextDialog.id = optionDialog.dialogId;
                                    DialogController.instance.saveDialog(category, nextDialog);
                                }
                                yde_option.links.add(new YDELink(yde_option.id, yde_next_dialog.id, EnumYDEType.DIALOG));
                                nodes.put(yde_next_dialog.id, yde_next_dialog);
                                int yD = processDialog(yde_next_dialog, x + 200, yde_option.y);
                                if (yD > yMax) { yMax = yD; }
                            } // Dialogue not found in mod data
                        }
                    }
                    yN += 140;
                }
                if (y + yN > yMax) { yMax = y + yN; }
            }
        }
        return yMax;
    }

    private YDEArea getArea(String category, int areaId) {
        for (YDENode node : nodes.values()) {
            if (node instanceof YDEArea area && area.category.equals(category) && area.id == areaId) { return area; }
        }
        return null;
    }

    private YDEOption getOption(int dialogId, int slot) {
        for (YDENode node : nodes.values()) {
            if (node.type == EnumYDEType.OPTION && node instanceof YDEOption option && option.dialogId == dialogId && option.slot == slot) { return option; }
        }
        return null;
    }

    private YDENpc getNpc(String category, Dialog.StartedNpcData npcData) {
        for (YDENode node : nodes.values()) {
            if (node.type == EnumYDEType.NPC && node instanceof YDENpc npc &&
                    npc.category.equals(category) && npc.npcData.equals(npcData)) { return npc; }
        }
        return null;
    }

    private YDEQuest getQuest(String category, int questId) {
        for (YDENode node : nodes.values()) {
            if (node.type == EnumYDEType.QUEST && node instanceof YDEQuest quest &&
                    quest.category.equals(category) && quest.questId == questId) { return quest; }
        }
        return null;
    }

    private YDEDialog getDialog(int dialogId) {
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

    public YDEData check() {
        // create from current data
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
        int y = 0;
        for (DialogCategory category : DialogController.instance.categories.values()) {
            for (Dialog dialog : category.dialogs.values()) {
                YDEDialog yde_dialog = getDialog(dialog.id);
                if (yde_dialog == null) {
                    yde_dialog = new YDEDialog(nodes.size(), category.title, dialog.id);
                    yde_dialog.title = Component.translatable("dialog.dialog").append(Component.literal(" ID: " + dialog.id));
                    nodes.put(yde_dialog.id, yde_dialog);
                    y = processDialog(yde_dialog, 0, y == 0 ? y : y + 60);
                }
            }
        }
        return this;
    }

}
