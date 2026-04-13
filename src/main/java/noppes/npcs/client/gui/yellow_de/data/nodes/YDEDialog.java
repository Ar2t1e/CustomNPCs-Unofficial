package noppes.npcs.client.gui.yellow_de.data.nodes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import noppes.npcs.client.gui.yellow_de.data.EnumYDEType;
import noppes.npcs.client.gui.yellow_de.data.YDEData;
import noppes.npcs.client.gui.yellow_de.data.YDENode;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.Dialog;

public class YDEDialog extends YDENode {

    public int dialogId;
    public Dialog dialog;

    public YDEDialog(YDEData parent, int idIn, String categoryIn, int dialogIdIn) {
        super(parent);
        type = EnumYDEType.DIALOG;
        title = Component.translatable("dialog.dialog").append(Component.literal("ID: " + dialogIdIn));

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

}
