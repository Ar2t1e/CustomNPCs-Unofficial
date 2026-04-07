package noppes.npcs.client.gui.yellow_de.data.nodes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import noppes.npcs.client.gui.yellow_de.data.EnumYDEType;
import noppes.npcs.client.gui.yellow_de.data.YDENode;
import noppes.npcs.controllers.data.DialogOption;

import javax.annotation.Nonnull;

public class YDEOption extends YDENode {

    public final @Nonnull DialogOption option;

    public YDEOption(int idIn, String categoryIn, @Nonnull DialogOption optionIn) {
        type = EnumYDEType.OPTION;
        title = Component.translatable("gui.answer").append(" # " + optionIn.slot);

        id = idIn;
        category = categoryIn;
        option = optionIn;
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        type = EnumYDEType.OPTION;
        option.load(compound.getCompound("Option"));
    }

    @Override
    public CompoundTag save() {
        CompoundTag compound = super.save();
        compound.put("Option", option.save());
        return compound;
    }

}
