package noppes.npcs.client.gui.yellow_de.data.nodes;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import noppes.npcs.client.gui.yellow_de.data.EnumYDEType;
import noppes.npcs.client.gui.yellow_de.data.YDEData;
import noppes.npcs.client.gui.yellow_de.data.YDENode;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.DialogCategory;
import noppes.npcs.util.ValueUtil;

public class YDECategory extends YDENode {

    public int categoryId = -1;
    protected float scale = 1.0f;

    public YDECategory(YDEData parent, int idIn, String categoryIn) {
        super(parent);
        type = EnumYDEType.CATEGORY;
        id = idIn;
        category = categoryIn;
        width = 0;
        height = 0;
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        type = EnumYDEType.CATEGORY;
        scale = ValueUtil.correctFloat(compound.getFloat("Scale"), 0.1f, 1.0f);
        categoryId = compound.getInt("CategoryId");
    }

    @Override
    public CompoundTag save() {
        CompoundTag compound = super.save();
        compound.putInt("CategoryId", categoryId);
        compound.putFloat("Scale", scale);
        return compound;
    }

    @Override
    public Component getTitle() {
        DialogCategory cat = DialogController.instance.getCategory(category);
        if (cat == null) { return Component.empty(); }
        categoryId = cat.id;
        if (id < 0) {
            parent.nodes.remove(-1);
            id =  parent.getEmptyNodeId();
            parent.nodes.put(id, this);
        }
        return Component.empty()
                .append(Component.translatable("drop.category").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(" ID:" + cat.id + " ").withStyle(ChatFormatting.GRAY))
                .append(Component.translatable(cat.title).withStyle(ChatFormatting.RESET));
    }

    public float getScale() { return scale; }

    public void setScale(float scaleIn) { scale = ValueUtil.correctFloat(scaleIn, 0.1f, 1.0f); }

}
