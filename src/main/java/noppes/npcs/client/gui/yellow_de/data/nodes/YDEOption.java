package noppes.npcs.client.gui.yellow_de.data.nodes;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.Component;
import noppes.npcs.client.gui.yellow_de.data.EnumYDEType;
import noppes.npcs.client.gui.yellow_de.data.YDEData;
import noppes.npcs.client.gui.yellow_de.data.YDENode;
import noppes.npcs.controllers.data.DialogOption;

import javax.annotation.Nonnull;
import java.util.List;

public class YDEOption extends YDENode {

    public @Nonnull DialogOption option;
    public int dialogId;

    public YDEOption(YDEData parent, int idIn, String categoryIn, int dialogIdIn, @Nonnull DialogOption optionIn) {
        super(parent);
        type = EnumYDEType.OPTION;

        id = idIn;
        dialogId = dialogIdIn;
        category = categoryIn;
        option = optionIn;
    }

    @Override
    public void load(NBTTagCompound compound) {
        super.load(compound);
        type = EnumYDEType.OPTION;
        dialogId = compound.getInteger("DialogId");
        option.load(compound.getCompoundTag("Option"));
    }

    @Override
    public NBTTagCompound save() {
        NBTTagCompound compound = super.save();
        compound.setInteger("DialogId", dialogId);
        compound.setTag("Option", option.save());
        return compound;
    }

    @Override
    public Component getTitle() {
        List<YDENode> list = parent.getToLinks(id);
        if (list.size() <= 1) { return Component.translatable("gui.answer").append(" # " + option.slot); }
        return Component.translatable("gui.answer").append(" # ").append(Component.translatable("gui.several"));
    }

}
