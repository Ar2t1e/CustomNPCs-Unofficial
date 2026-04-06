package noppes.npcs.api.wrapper.gui;

import net.minecraft.nbt.CompoundTag;
import noppes.npcs.api.constants.GuiComponentType;
import noppes.npcs.api.gui.IGuiTimer;
import noppes.npcs.util.Util;

public class CustomGuiTimerWrapper extends CustomGuiComponentWrapper implements IGuiTimer {

    int color = 0xFFFFFF;
    public long start = 0;
    public long now = 0;
    public long end = 0;
    float scale = 1.0f;
    public boolean reverse;

    public CustomGuiTimerWrapper() { }

    public CustomGuiTimerWrapper(int id, long start, long end, int x, int y, int width, int height) {
        this();
        setId(id);
        setPos(x, y);
        setSize(width, height);
        setTime(start, end);
    }

    @SuppressWarnings("all")
    public CustomGuiTimerWrapper(int id, long start, long end, int x, int y, int width, int height, int color) {
        this(id, start, end, x, y, width, height);
        setColor(color);
    }

    @Override
    public int getColor() { return color; }

    @Override
    public float getScale() { return scale; }

    @Override
    public String getText() {
        long time = reverse ? now - System.currentTimeMillis() : (System.currentTimeMillis() - now);
        time /= 50L;
        return Util.instance.ticksToElapsedTime(time, false, false, false);
    }

    @Override
    public CustomGuiTimerWrapper setColor(int colorIn) {
        color = colorIn;
        return this;
    }

    @Override
    public CustomGuiTimerWrapper setScale(float scaleIn) {
        scale = scaleIn;
        return this;
    }

    @Override
    public CustomGuiTimerWrapper setTime(long startIn, long endIn) {
        start = startIn;
        end = endIn;
        now = System.currentTimeMillis();
        reverse = start > end;
        return this;
    }

    @Override
    public int getType() { return GuiComponentType.TIMER.get(); }

    @Override
    public CompoundTag toNBT(CompoundTag compound) {
        super.toNBT(compound);
        compound.putInt("color", color);
        compound.putLong("start", start);
        compound.putLong("now", now);
        compound.putLong("end", end);
        compound.putFloat("scale", scale);
        compound.putBoolean("reverse", reverse);
        return compound;
    }

    @Override
    public CustomGuiComponentWrapper fromNBT(CompoundTag compound) {
        super.fromNBT(compound);
        color = compound.getInt("color");
        start = compound.getLong("start");
        now = compound.getLong("now");
        end = compound.getLong("end");
        scale = compound.getFloat("scale");
        reverse = compound.getBoolean("reverse");
        return this;
    }

}
