package noppes.npcs.api.wrapper.gui;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.constants.GuiComponentType;
import noppes.npcs.api.functions.gui.GuiComponentClicked;
import noppes.npcs.api.gui.IButton;
import noppes.npcs.api.gui.IButtonList;
import noppes.npcs.api.gui.ITexturedRect;

public class CustomGuiButtonListWrapper extends CustomGuiButtonWrapper implements IButtonList {

    CustomGuiTexturedRectWrapper left = new CustomGuiTexturedRectWrapper();
    CustomGuiTexturedRectWrapper right = new CustomGuiTexturedRectWrapper();
    private int selected = 0;
    private String[] values = new String[0];

    public CustomGuiButtonListWrapper() { }

    public CustomGuiButtonListWrapper(int id, int x, int y, int width, int height) {
        super(id, "", x, y, width, height);
        ITexturedRect rect = getTextureRect();
        rect.setTexture(CustomNpcs.MODID + ":textures/gui/components.png");
        rect.setRepeatingTexture(64, 22, 3).setTextureOffset(0, 64).setPos(7, 0);
        setTextureHoverOffset(22);
        left.setTexture(CustomNpcs.MODID + ":textures/gui/components.png").setTextureOffset(0, 130);
        left.setSize(10, 20).setPos(0, 0);
        right.setTexture(CustomNpcs.MODID + ":textures/gui/components.png").setTextureOffset(12, 130);
        right.setSize(10, 20).setPos(width - 10, 0);
    }

    @Override
    public CustomGuiButtonListWrapper setSize(int width, int height) {
        super.setSize(width, height);
        getTextureRect().setSize(width - 14, height);
        return this;
    }

    @Override
    public CustomGuiButtonListWrapper setValues(String... valuesIn) {
        if (valuesIn != null && valuesIn.length != 0) {
            values = valuesIn;
            selected %= valuesIn.length;
            setLabel(values[selected]);
        }
        else {
            values = new String[0];
            setLabel("");
        }
        return this;
    }

    @Override
    public String[] getValues() { return values; }

    @Override
    public CustomGuiButtonListWrapper setSelected(int selectedIn) {
        if (selectedIn < 0) { selectedIn += values.length; }
        if (selectedIn >= values.length) { selectedIn %= values.length; }
        selected = selectedIn;
        setLabel(values[selected]);
        return this;
    }

    @Override
    public int getSelected() { return selected; }

    @Override
    public CustomGuiTexturedRectWrapper getLeftTexture() { return left; }

    @Override
    public CustomGuiTexturedRectWrapper getRightTexture() { return right; }

    @Override
    public int getType() { return GuiComponentType.BUTTON_LIST.get(); }

    @Override
    public CustomGuiButtonListWrapper setOnPress(GuiComponentClicked<IButton> onPress) {
        super.setOnPress(onPress);
        return this;
    }

    @Override
    public NBTTagCompound toNBT(NBTTagCompound nbt) {
        super.toNBT(nbt);
        nbt.setInteger("selected", selected);
        NBTTagList list = new NBTTagList();
        for (String s : values) { list.appendTag(new NBTTagString(s)); }
        nbt.setTag("values", list);
        nbt.setTag("left", left.toNBT(new NBTTagCompound()));
        nbt.setTag("right", right.toNBT(new NBTTagCompound()));
        return nbt;
    }

    @Override
    public CustomGuiComponentWrapper fromNBT(NBTTagCompound nbt) {
        super.fromNBT(nbt);
        selected = nbt.getInteger("selected");
        NBTTagList list = nbt.getTagList("values", 8);
        values = new String[list.tagCount()];
        for (int i = 0; i < list.tagCount(); i++) { values[i] = list.getStringTagAt(i); }
        left.fromNBT(nbt.getCompoundTag("left"));
        right.fromNBT(nbt.getCompoundTag("right"));
        return this;
    }

}
