package noppes.npcs.controllers.data;

import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.constants.EnumAvailabilityScoreboard;

public class AvailabilityMoneyData {

    public int value;
    public EnumAvailabilityScoreboard type;

    public AvailabilityMoneyData(NBTTagCompound nbt) {
        value = nbt.getInteger("Value");
        int t = nbt.getInteger("EqualsType");
        if (t < 0) { t *= -1; }
        type = EnumAvailabilityScoreboard.values()[t % EnumAvailabilityScoreboard.values().length];
    }

    public AvailabilityMoneyData(int v, EnumAvailabilityScoreboard t) {
        value = v;
        type = t;
    }

    public NBTTagCompound save(NBTTagCompound nbtMoney) {
        nbtMoney.setInteger("Value", value);
        nbtMoney.setInteger("EqualsType", type.ordinal());
        return nbtMoney;
    }

}
