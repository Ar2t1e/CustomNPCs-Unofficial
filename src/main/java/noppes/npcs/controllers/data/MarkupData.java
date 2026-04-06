package noppes.npcs.controllers.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class MarkupData {

    public int id = -1;
    public int xp = 0;
    public int level = 0;
    public float buy = 0.0f;
    public float sell = 0.04f;

    public MarkupData(int levelIn, float buyIn, float sellIn, int xpIn) {
        level = levelIn;
        buy = buyIn;
        sell = sellIn;
        xp = xpIn;
    }

    public MarkupData(int idIn, int levelIn, int xpIn) {
        id = idIn;
        level = levelIn;
        xp = xpIn;
    }

    public MarkupData(CompoundTag data) { setNBT(data); }

    public void addXP(int xpIn) {
        xp += xpIn;
        if (xp < 0) { xp = 0; }
    }

    public CompoundTag getNBT() {
        CompoundTag data = new CompoundTag();
        data.putInt("level", level);
        data.putInt("xp", xp);
        data.putFloat("buy", buy);
        data.putFloat("sell", sell);
        return data;
    }

    public Tag getPlayerNBT() {
        CompoundTag data = new CompoundTag();
        data.putInt("id", id);
        data.putInt("xp", xp);
        data.putInt("level", level);
        return data;
    }

    public void setNBT(CompoundTag data) {
        level = data.getInt("level");
        xp = data.getInt("xp");
        buy = data.getFloat("buy");
        sell = data.getFloat("sell");
    }

}
