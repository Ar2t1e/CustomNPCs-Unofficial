package noppes.npcs.client.gui.yellow_de.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import noppes.npcs.client.gui.yellow_de.data.nodes.*;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.DialogCategory;
import noppes.npcs.controllers.data.DialogOption;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.ValueUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class YDEData {

    public final Map<Integer, YDENode> nodes = new TreeMap<>();
    public final List<YDELink> links = new ArrayList<>();

    public void load(CompoundTag compound) {
        ListTag listNodes = compound.getList("Nodes", 10);
        for (int i = 0; i < listNodes.size(); i++) {
            CompoundTag nbt = listNodes.getCompound(i);
            YDENode node = switch (EnumYDEType.values()[ValueUtil.onlyPositiveInt(nbt.getInt("Type"), EnumYDEType.values().length)]) {
                case CATEGORY -> new YDECategory(this, -1, "");
                case NPC -> new YDENpc(this, -1, "", null);
                case OPTION -> new YDEOption(this, -1, "", -1, new DialogOption());
                case QUEST -> new YDEQuest(this, -1, "", -1);
                case AREA -> new YDEArea(this, -1, "", "");
                default -> new YDEDialog(this, -1, "", -1);
            };
            try {
                node.load(listNodes.getCompound(i));
                nodes.put(node.id, node);
            }
            catch (Exception e) { LogWriter.error(e); }
        }
        ListTag listLinks = compound.getList("Links", 10);
        for (int i = 0; i < listLinks.size(); i++) {
            YDELink link = new YDELink(0, 0, EnumYDEType.DIALOG);
            link.load(listLinks.getCompound(i));
            links.add(link);
        }
    }

    public CompoundTag save() {
        CompoundTag compound = new CompoundTag();

        ListTag listNodes = new ListTag();
        for (YDENode node : new ArrayList<>(nodes.values())) { listNodes.add(node.save()); }
        compound.put("Nodes", listNodes);

        ListTag listLinks = new ListTag();
        for (YDELink link : new ArrayList<>(links)) { listLinks.add(link.save()); }
        compound.put("Links", listLinks);

        return compound;
    }

    public int getEmptyNodeId() {
        int id = 0;
        while (nodes.containsKey(id)) { id++; }
        return id;
    }

    public YDEData check() {
        boolean isChanged = false;
        // remove missing nodes
        Iterator<Map.Entry<Integer, YDENode>> it = nodes.entrySet().iterator();
        while (it.hasNext()) {
            YDENode node = it.next().getValue();
            if (node instanceof YDEDialog d) {
                if (!DialogController.instance.hasDialog(d.dialogId)) {
                    it.remove();
                    isChanged = true;
                }
            }
            else if (node instanceof YDEOption o) {
                boolean exists = false;
                for (Dialog dialog : DialogController.instance.dialogs.values()) {
                    if (dialog.options.containsValue(o.option)) { exists = true; break; }
                }
                if (!exists) {
                    it.remove();
                    isChanged = true;
                }
            }
            else if (node instanceof YDENpc n) {
                boolean parentExists = false;
                for (YDENode parent : nodes.values()) {
                    if (parent instanceof YDEDialog d && d.dialog != null
                            && d.dialog.startedNpcs.contains(n.npcData)) {
                        parentExists = true; break;
                    }
                }
                if (!parentExists) {
                    it.remove();
                    isChanged = true;
                }
            }
            else if (node instanceof YDEQuest q) {
                boolean parentExists = false;
                for (YDENode parent : nodes.values()) {
                    if (parent instanceof YDEDialog d && d.dialog != null
                            && d.dialog.quest == q.questId) {
                        parentExists = true; break;
                    }
                }
                if (!parentExists) {
                    it.remove();
                    isChanged = true;
                }
            }
        }
        // remove missing links
        if (!isChanged) {
            Iterator<YDELink> il = links.iterator();
            while (il.hasNext()) {
                YDELink link = il.next();
                if (!nodes.containsKey(link.back) || !nodes.containsKey(link.next)) {
                    il.remove();
                    isChanged = true;
                }
            }
        }
        // check and add new nodes
        if (!isChanged) {
            for (Dialog dialog : DialogController.instance.dialogs.values()) {
                if (!hasCategory(dialog.category.title)) {
                    isChanged = true;
                    break;
                } // has category node
                if (getDialog(dialog) == null) {
                    isChanged = true;
                    break;
                } // has dialog node
                if (getQuest(dialog.category.title, dialog.quest) == null) {
                    isChanged = true;
                    break;
                } // has quest node
                for (Dialog.StartedNpcData snd : dialog.startedNpcs) {
                    if (getNpc(dialog.category.title, snd) == null) {
                        isChanged = true;
                        break;
                    }
                } // has npc node
                if (isChanged) { break; }
                for (Map.Entry<Integer, DialogOption> entry : dialog.options.entrySet()) {
                    entry.getValue().slot = entry.getKey();
                    if (getOption(entry.getValue()) == null) {
                        isChanged = true;
                        break;
                    }
                }  // has option node
                if (isChanged) { break; }
            }
        }
        if (isChanged) { reBuilding(); }
        return this;
    }

    private void reBuilding() {
        nodes.clear();
        links.clear();
        for (DialogCategory category : DialogController.instance.categories.values()) { createCategory(category); }
    }

    private void createCategory(DialogCategory category) {
        YDECategory yde_category = new YDECategory(this, getEmptyNodeId(), category.title);
        nodes.put(yde_category.id, yde_category);
        // create all branches
        int branchYPos = 0;
        for (Dialog dialog : category.dialogs.values()) {
            YDEDialog node = getDialog(dialog);
            if (node == null) {
                YDEDialog yde_dialog = new YDEDialog(this, getEmptyNodeId(), dialog.category.title, dialog.id);
                yde_dialog.dialog = dialog;
                nodes.put(yde_dialog.id, yde_dialog);

                DialogBranch branch = new DialogBranch();
                buildBranchRecursive(branch, 0, yde_dialog);
                branch.calculateMetrics(branchYPos);

                YDEArea area = new YDEArea(this, getEmptyNodeId(), dialog.category.title, dialog.title);
                area.x = branch.x;
                area.y = branch.y + branchYPos;
                area.width = branch.width;
                area.height = branch.height;
                nodes.put(area.id, area);

                branchYPos += branch.height + 20;
            } // new branch
        }
    }

    private void buildBranchRecursive(DialogBranch branch, int column, YDENode node) {
        branch.addNode(node, column);
        if (node instanceof YDEDialog dialogNode) {
            //LogWriter.info("[DEBUG] column "+column+"; dialog: "+(dialogNode.dialog != null ? dialogNode.dialog.title : dialogNode.dialogId));
            if (dialogNode.dialog != null) {
                // quest (this column)
                if (dialogNode.dialog.quest > -1) {
                    YDEQuest quest = getQuest(dialogNode.category, dialogNode.dialog.quest);
                    if (quest == null) {
                        quest = new YDEQuest(this, getEmptyNodeId(), dialogNode.category, dialogNode.dialog.quest);
                        nodes.put(quest.id, quest);
                        branch.addNode(quest, column);
                    }
                    links.add(new YDELink(dialogNode.id, quest.id, EnumYDEType.QUEST));
                }
                // npcs (column - 1)
                for (Dialog.StartedNpcData npcData : dialogNode.dialog.startedNpcs) {
                    YDENpc npc = getNpc(dialogNode.category, npcData);
                    if (npc == null) {
                        npc = new YDENpc(this, getEmptyNodeId(), dialogNode.category, npcData);
                        nodes.put(npc.id, npc);
                        branch.addNode(npc, column - 1);
                    }
                    links.add(new YDELink(dialogNode.id, npc.id, EnumYDEType.NPC));
                }
                // options (column + 1)
                for (DialogOption option : dialogNode.dialog.options.values()) {
                    YDEOption optNode = getOption(option);
                    if (optNode == null) {
                        optNode = new YDEOption(this, getEmptyNodeId(), dialogNode.category, dialogNode.dialogId, option);
                        nodes.put(optNode.id, optNode);
                        buildBranchRecursive(branch, column + 1, optNode);
                    }
                    links.add(new YDELink(dialogNode.id, optNode.id, EnumYDEType.OPTION));
                }
            }
        } // dialog
        else if (node instanceof YDEOption optionNode) {
            //LogWriter.info("[DEBUG] column "+column+"; option: "+optionNode.option.title+"; dialogId: "+optionNode.dialogId);
            if (optionNode.option.hasDialogs()) {
                for (DialogOption.OptionDialogID od : optionNode.option.dialogs) {
                    YDEDialog nextDialog = getDialog(od.dialogId);
                    if (nextDialog == null) {
                        nextDialog = new YDEDialog(this, getEmptyNodeId(), optionNode.category, od.dialogId);
                        if (nextDialog.dialog == null || optionNode.category.equals(nextDialog.dialog.category.title)) {
                            nodes.put(nextDialog.id, nextDialog);
                            buildBranchRecursive(branch, column + 1, nextDialog);
                        }
                    }
                    links.add(new YDELink(optionNode.id, nextDialog.id, EnumYDEType.OPTION));
                }
            }
        } // option
    }

    @SuppressWarnings("unused")
    public YDEArea getArea(String category, int areaId) {
        for (YDENode node : new ArrayList<>(nodes.values())) {
            if (node instanceof YDEArea area && area.category.equals(category) && area.id == areaId) { return area; }
        }
        return null;
    }

    public YDEOption getOption(DialogOption optionIn) {
        for (YDENode node : new ArrayList<>(nodes.values())) {
            if (node.type == EnumYDEType.OPTION && node instanceof YDEOption option &&
                    option.option.dialogs.size() == optionIn.dialogs.size() &&
                    option.option.equals(optionIn)) {
                boolean equalDialogsData = true;
                for (int i = 0; i < optionIn.dialogs.size(); i++) {
                    DialogOption.OptionDialogID od0 = optionIn.dialogs.get(i);
                    DialogOption.OptionDialogID od1 = option.option.dialogs.get(i);
                    if (od0.dialogId != od1.dialogId) {
                        equalDialogsData = false;
                        break;
                    }
                }
                if (equalDialogsData) { return option; }
            }
        }
        return null;
    }

    public YDEOption createOption(@Nonnull String categoryTitle, @Nonnull DialogOption dialogOption, @Nullable Dialog dialog) {
        YDEOption yde_option = new YDEOption(this, getEmptyNodeId(), categoryTitle, dialog == null ? -1 : dialog.id, dialogOption);
        nodes.put(yde_option.id, yde_option);
        if (dialog != null) {
            YDEDialog yde_dialog = getDialog(dialog);
            if (yde_dialog != null) { links.add(new YDELink(yde_dialog.id, yde_option.id, EnumYDEType.DIALOG)); }
        }
        return yde_option;
    }

    public YDENpc getNpc(String category, Dialog.StartedNpcData npcData) {
        for (YDENode node : new ArrayList<>(nodes.values())) {
            if (node.type == EnumYDEType.NPC && node instanceof YDENpc npc &&
                    npc.category.equals(category) && npc.npcData.equals(npcData)) { return npc; }
        }
        return null;
    }

    public YDEQuest getQuest(String category, int questId) {
        for (YDENode node : new ArrayList<>(nodes.values())) {
            if (node.type == EnumYDEType.QUEST && node instanceof YDEQuest quest &&
                    quest.category.equals(category) && quest.questId == questId) { return quest; }
        }
        return null;
    }

    public YDEDialog getDialog(@Nonnull Dialog dialog) {
        for (YDENode node : new ArrayList<>(nodes.values())) {
            if (node.type == EnumYDEType.DIALOG && node instanceof YDEDialog yde_dialog &&
                    (dialog.equals(yde_dialog.dialog) || (yde_dialog.dialog != null && yde_dialog.dialog.id == dialog.id) || yde_dialog.dialogId == dialog.id)) { return yde_dialog; }
        }
        return null;
    }

    public YDEDialog getDialog(int dialogId) {
        Dialog dialog = DialogController.instance.get(dialogId);
        for (YDENode node : new ArrayList<>(nodes.values())) {
            if (node.type == EnumYDEType.DIALOG && node instanceof YDEDialog yde_dialog &&
                    (dialog != null && dialog.equals(yde_dialog.dialog) || yde_dialog.dialogId == dialogId)) { return yde_dialog; }
        }
        return null;
    }

    public YDEDialog createDialog(@Nonnull Dialog dialog) {
        YDEDialog yde_dialog = getDialog(dialog);
        if (yde_dialog == null) {
            yde_dialog = new YDEDialog(this, getEmptyNodeId(), dialog.category.title, dialog.id);
            yde_dialog.dialog = dialog;
            nodes.put(yde_dialog.id, yde_dialog);
        }
        return yde_dialog;
    }

    public boolean hasCategory(String categoryTitle) {
        for (YDENode node : new ArrayList<>(nodes.values())) {
            if (node instanceof YDECategory cat) {
                if (cat.category.equals(categoryTitle)) { return true; }
            }
        }
        return false;
    }

    public @Nonnull YDECategory getCategory(String categoryTitle) {
        YDECategory empty = null;
        for (YDENode node : new ArrayList<>(nodes.values())) {
            if (node instanceof YDECategory cat) {
                if (cat.category.equals(categoryTitle)) { return cat; }
                if (cat.category.isEmpty()) { empty = cat; }
            }
        }
        if (empty == null) { nodes.put(-1, empty = new YDECategory(this, -1, "")); }
        empty.category = categoryTitle;
        return empty;
    }

    public List<YDENode> getToLinks(int nodeId) {
        List<YDENode> list = new ArrayList<>();
        for (YDELink link : new ArrayList<>(links)) {
            if (link.next == nodeId && nodes.containsKey(link.back)) {
                list.add(nodes.get(link.back));
                break;
            }
        }
        return list;
    }

    public List<YDENode> getFromLinks(int nodeId) {
        List<YDENode> list = new ArrayList<>();
        for (YDELink link : new ArrayList<>(links)) {
            if (link.back == nodeId && nodes.containsKey(link.next)) {
                list.add(nodes.get(link.next));
                break;
            }
        }
        return list;
    }

    public List<YDELink> getLinks(String categoryTitle) {
        List<YDELink> list = new ArrayList<>();
        for (YDELink link : new ArrayList<>(links)) {
            YDENode nodeB = nodes.get(link.back);
            YDENode nodeN = nodes.get(link.next);
            if ((nodeB != null && nodeB.category.equals(categoryTitle)) ||
                    (nodeN != null && nodeN.category.equals(categoryTitle))) { list.add(link); }
        }
        return list;
    }

    public void removeLink(YDELink selectLink) { links.remove(selectLink); }

    private static class DialogBranch {

        private final Map<Integer, List<YDENode>> columns = new TreeMap<>();
        private final Set<Integer> nodeIds = new HashSet<>();

        private int x = 0;
        private int y = 0;
        private int height = 0;
        private int width = 0;

        void addNode(YDENode node, int colX) {
            if (nodeIds.contains(node.id)) return;
            nodeIds.add(node.id);
            columns.computeIfAbsent(colX, k -> new ArrayList<>()).add(node);
        }

        void calculateMetrics(int branchYPos) {
            height = 0;
            Map<Integer, Integer> columnHeights = new HashMap<>();
            // max height
            for (Map.Entry<Integer, List<YDENode>> entry : columns.entrySet()) {
                int colHeight = 0;
                for (YDENode node : entry.getValue()) {
                    if (colHeight > 0) { colHeight += 20; }
                    colHeight += node.height;
                }
                columnHeights.put(entry.getKey(), colHeight);
                if (colHeight > height) { height = colHeight; }
            }
            // calculate posses
            int center = height / 2;
            y = 0;
            x = 0;
            int maxX = -1;
            for (Map.Entry<Integer, List<YDENode>> entry : columns.entrySet()) {
                int colY = branchYPos + center - columnHeights.get(entry.getKey()) / 2;
                for (YDENode node : entry.getValue()) {
                    node.x = entry.getKey() * 200;
                    node.y = colY;
                    colY += node.height + 20;
                    if (node instanceof YDENpc) { node.x += 90; }
                    else if (node instanceof YDEQuest) { node.x += 45; }
                    if (x > node.x) { x = node.x; }
                    if (maxX < node.x + node.width) { maxX = node.x + node.width; }
                }
            }
            width = maxX - x;
        }
    }

}
