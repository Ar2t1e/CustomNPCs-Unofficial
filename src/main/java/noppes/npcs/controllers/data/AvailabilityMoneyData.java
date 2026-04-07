package noppes.npcs.controllers.data;

import net.minecraft.nbt.CompoundTag;
import noppes.npcs.constants.EnumAvailabilityScoreboard;

public class AvailabilityMoneyData {

    public int value;
    public EnumAvailabilityScoreboard type;

    public AvailabilityMoneyData(CompoundTag nbt) {
        value = nbt.getInt("Value");
        int t = nbt.getInt("EqualsType");
        if (t < 0) { t *= -1; }
        type = EnumAvailabilityScoreboard.values()[t % EnumAvailabilityScoreboard.values().length];
    }

    public AvailabilityMoneyData(int v, EnumAvailabilityScoreboard t) {
        value = v;
        type = t;
    }

    public CompoundTag save(CompoundTag nbtMoney) {
        nbtMoney.putInt("Value", value);
        nbtMoney.putInt("EqualsType", type.ordinal());
        return nbtMoney;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) { return true; }
        if (obj instanceof AvailabilityMoneyData md) {
            return value == md.value && type == md.type;
        }
        return false;
    }

}
