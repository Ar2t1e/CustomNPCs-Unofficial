package noppes.npcs.client.gui.yellow_de.data.nodes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import noppes.npcs.client.gui.yellow_de.data.EnumYDEType;
import noppes.npcs.client.gui.yellow_de.data.YDEData;
import noppes.npcs.client.gui.yellow_de.data.YDENode;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.DialogOption;

import javax.annotation.Nonnull;

public class YDEOption extends YDENode {

    public final @Nonnull DialogOption option;
    public int dialogId;
    public Dialog dialog;

    public YDEOption(YDEData parent, int idIn, String categoryIn, int dialogIdIn, @Nonnull DialogOption optionIn) {
        super(parent);
        type = EnumYDEType.OPTION;
        title = Component.translatable("gui.answer").append(" # " + optionIn.slot);

        id = idIn;
        dialogId = dialogIdIn;
        dialog = DialogController.instance.get(dialogId);
        category = categoryIn;
        option = optionIn;
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        type = EnumYDEType.OPTION;
        dialogId = compound.getInt("DialogId");
        dialog = DialogController.instance.get(dialogId);
        option.load(compound.getCompound("Option"));
    }

    @Override
    public CompoundTag save() {
        CompoundTag compound = super.save();
        compound.putInt("DialogId", dialogId);
        compound.put("Option", option.save());
        return compound;
    }

}
