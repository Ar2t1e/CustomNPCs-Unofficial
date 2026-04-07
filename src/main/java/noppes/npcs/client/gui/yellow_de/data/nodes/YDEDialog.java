package noppes.npcs.client.gui.yellow_de.data.nodes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import noppes.npcs.client.gui.yellow_de.data.EnumYDEType;
import noppes.npcs.client.gui.yellow_de.data.YDENode;

public class YDEDialog extends YDENode {

    public int dialogId;

    public YDEDialog(int idIn, String categoryIn, int dialogIdIn) {
        type = EnumYDEType.DIALOG;
        title = Component.translatable("dialog.dialog").append(Component.literal("ID: " + dialogIdIn));

        id = idIn;
        category = categoryIn;
        dialogId = dialogIdIn;
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        type = EnumYDEType.DIALOG;
        dialogId = compound.getInt("DialogID");
    }

    @Override
    public CompoundTag save() {
        CompoundTag compound = super.save();
        compound.putInt("DialogID", dialogId);
        return compound;
    }

}
