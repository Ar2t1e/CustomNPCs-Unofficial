package noppes.npcs.api.wrapper.gui;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.constants.GuiComponentType;
import noppes.npcs.api.gui.IItemRenderer;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.ItemStackWrapper;

import java.util.Objects;

public class CustomGuiItemRendererWrapper extends CustomGuiComponentWrapper implements IItemRenderer {

    protected IItemStack stack = ItemStackWrapper.AIR;
    public int width;
    public int height;
    public float scale = 1.0F;

    public CustomGuiItemRendererWrapper() { }

    public CustomGuiItemRendererWrapper(int id, int x, int y, int width, int height, IItemStack stackIn) {
        setId(id);
        setPos(x, y);
        setStack(stackIn);
        setHoverBox(width, height);
    }

    @Override
    public boolean hasStack() { return !stack.isEmpty(); }

    @Override
    public IItemStack getStack() { return stack; }

    @Override
    public IItemRenderer setStack(IItemStack stackIn) {
        stack = stackIn == null ? ItemStackWrapper.AIR : stackIn;
        return this;
    }

    @Override
    public int getWidth() { return width; }

    @Override
    public int getHeight() { return height; }

    @Override
    public IItemRenderer setHoverBox(int widthIn, int heightIn) {
        width = widthIn;
        height = heightIn;
        return this;
    }

    @Override
    public float getScale() { return scale; }

    @Override
    public IItemRenderer setScale(float scaleIn) {
        scale = scaleIn;
        return this;
    }

    @Override
    public int getType() { return GuiComponentType.ITEM_RENDERER.get(); }

    @Override
    public NBTTagCompound toNBT(NBTTagCompound nbt) {
        super.toNBT(nbt);
        nbt.setTag("stack", stack.getMCItemStack().serializeNBT());
        nbt.setFloat("scale", scale);
        nbt.setInteger("width", width);
        nbt.setInteger("height", height);
        return nbt;
    }

    @Override
    public CustomGuiComponentWrapper fromNBT(NBTTagCompound nbt) {
        super.fromNBT(nbt);
        setStack(Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(new ItemStack(nbt.getCompoundTag("stack"))));
        setScale(nbt.getFloat("scale"));
        setHoverBox(nbt.getInteger("width"), nbt.getInteger("height"));
        return this;
    }

}
