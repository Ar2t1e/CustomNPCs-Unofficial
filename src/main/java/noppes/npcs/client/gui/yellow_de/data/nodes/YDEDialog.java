package noppes.npcs.client.gui.yellow_de.data.nodes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import noppes.npcs.client.gui.yellow_de.data.EnumYDEType;
import noppes.npcs.client.gui.yellow_de.data.YDEData;
import noppes.npcs.client.gui.yellow_de.data.YDENode;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.Dialog;

import java.util.ArrayList;

public class YDEDialog extends YDENode {

    public int dialogId;
    public Dialog dialog;

    public YDEDialog(YDEData parent, int idIn, String categoryIn, int dialogIdIn) {
        super(parent);
        type = EnumYDEType.DIALOG;

        id = idIn;
        dialog = DialogController.instance.get(dialogId);
        category = categoryIn;
        dialogId = dialogIdIn;
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        type = EnumYDEType.DIALOG;
        dialogId = compound.getInt("DialogID");
        dialog = DialogController.instance.get(dialogId);
    }

    @Override
    public CompoundTag save() {
        CompoundTag compound = super.save();
        compound.putInt("DialogID", dialogId);
        return compound;
    }

    @Override
    public Component getTitle() {
        if (dialog == null && dialogId > -1) { dialog = DialogController.instance.get(dialogId); }
        else if (dialog != null && dialog.id == -1) {
            for (Dialog d : new ArrayList<>(DialogController.instance.dialogs.values())) {
                if (d.title.equals(dialog.title)) {
                    dialog = d;
                    break;
                }
            }
        }
        if (id < 0) {
            parent.nodes.remove(-1);
            id = parent.getEmptyNodeId();
            parent.nodes.put(id, this);
        }
        dialogId = dialog != null ? dialog.id : -1;
        return Component.translatable("dialog.dialog").append(Component.literal("ID: " + dialogId));
    }

}
