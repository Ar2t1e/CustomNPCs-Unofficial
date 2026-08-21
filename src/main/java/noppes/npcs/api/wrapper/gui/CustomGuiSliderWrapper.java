package noppes.npcs.api.wrapper.gui;

import java.math.BigDecimal;
import java.math.RoundingMode;

import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.constants.GuiComponentType;
import noppes.npcs.api.functions.gui.GuiComponentUpdate;
import noppes.npcs.api.gui.ICustomGui;
import noppes.npcs.api.gui.ISlider;

public class CustomGuiSliderWrapper extends CustomGuiComponentWrapper implements ISlider {

    protected String format = "%s%%";
    protected float min = 0.0F;
    protected float max = 100.0F;
    protected int decimals = 0;
    protected float value = 100.0F;
    protected GuiComponentUpdate<ISlider> onChange = null;

    public CustomGuiSliderWrapper() { }

    public CustomGuiSliderWrapper(int id, String format, int x, int y, int width, int height) {
        setId(id);
        if (!format.isEmpty()) { setFormat(format); }
        setPos(x, y);
        setSize(width, height);
    }

    @Override
    public float getValue() { return value; }

    @Override
    public CustomGuiSliderWrapper setValue(float valueIn) {
        BigDecimal bd = new BigDecimal(valueIn);
        value = bd.setScale(decimals, RoundingMode.FLOOR).floatValue();
        return this;
    }

    @Override
    public String getFormat() { return format; }

    @Override
    public CustomGuiSliderWrapper setFormat(String formatIn) {
        format = formatIn;
        return this;
    }

    @Override
    public float getMin() { return min; }

    @Override
    public CustomGuiSliderWrapper setMin(float minIn) {
        min = minIn;
        return this;
    }

    @Override
    public float getMax() { return max; }

    @Override
    public CustomGuiSliderWrapper setMax(float maxIn) {
        max = maxIn;
        return this;
    }

    @Override
    public int getDecimals() { return decimals; }

    @Override
    public CustomGuiSliderWrapper setDecimals(int decimalsIn) {
        if (decimalsIn < 0) { throw new CustomNPCsException("Decimals cant be lower then 0"); }
        decimals = decimalsIn;
        return this;
    }

    @Override
    public int getType() { return GuiComponentType.SLIDER.get(); }

    @Override
    public NBTTagCompound toNBT(NBTTagCompound compound) {
        super.toNBT(compound);
        compound.setString("format", format);
        compound.setInteger("decimals", decimals);
        compound.setFloat("min", min);
        compound.setFloat("max", max);
        compound.setFloat("value", value);
        return compound;
    }

    @Override
    public CustomGuiComponentWrapper fromNBT(NBTTagCompound compound) {
        super.fromNBT(compound);
        setFormat(compound.getString("format"));
        setDecimals(compound.getInteger("decimals"));
        setMin(compound.getFloat("min"));
        setMax(compound.getFloat("max"));
        setValue(compound.getFloat("value"));
        return this;
    }

    @Override
    public CustomGuiSliderWrapper setOnChange(GuiComponentUpdate<ISlider> onChangeIn) {
        onChange = onChangeIn;
        return this;
    }

    public final void onChange(ICustomGui gui) {
        if (onChange != null) {
            onChange.onChange(gui, this);
        }
    }

}
