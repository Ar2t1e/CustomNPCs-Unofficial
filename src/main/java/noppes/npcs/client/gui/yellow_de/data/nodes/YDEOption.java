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

    public @Nonnull DialogOption option;
    public int dialogId;
    public Dialog dialog;

    public YDEOption(YDEData parent, int idIn, String categoryIn, int dialogIdIn, @Nonnull DialogOption optionIn) {
        super(parent);
        type = EnumYDEType.OPTION;

        id = idIn;
        dialogId = dialogIdIn;
        category = categoryIn;
        option = optionIn;
        refresh();
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        type = EnumYDEType.OPTION;
        dialogId = compound.getInt("DialogId");
        option.load(compound.getCompound("Option"));
        refresh();
    }

    @Override
    public CompoundTag save() {
        CompoundTag compound = super.save();
        compound.putInt("DialogId", dialogId);
        compound.put("Option", option.save());
        return compound;
    }

    @Override
    public void refresh() {
        if (dialog == null) { dialog = DialogController.instance.get(dialogId); }
        if (dialog == null) {
            dialog = new Dialog(DialogController.instance.getCategory(category));
            if (option.slot < 0) { option.slot = 0; }
            dialog.options.put(option.slot, option);
        }
        else if (option.slot > -1 && dialog.options.containsKey(option.slot)) {
            option = dialog.options.get(option.slot);
        }
        title = Component.translatable("gui.answer").append(" # " + option.slot);
    }

}
