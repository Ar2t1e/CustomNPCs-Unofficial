package noppes.npcs.controllers.data;

import net.minecraft.nbt.CompoundTag;
import noppes.npcs.constants.EnumAvailabilityStackData;

public class AvailabilityStackData {

    public EnumAvailabilityStackData type = EnumAvailabilityStackData.Always;
    public boolean ignoreNBT = false;
    public boolean ignoreDamage = false;

    public AvailabilityStackData() {}

    public AvailabilityStackData(CompoundTag compound) {
        int t = compound.getInt("type");
        if (t < 0) { t *= -1; }
        type = EnumAvailabilityStackData.values()[t % EnumAvailabilityStackData.values().length];
        ignoreNBT = compound.getBoolean("ignoreNBT");
        ignoreDamage = compound.getBoolean("ignoreDamage");
    }

    public CompoundTag save() {
        CompoundTag compound = new CompoundTag();
        compound.putInt("type", type.ordinal());
        compound.putBoolean("ignoreNBT", ignoreNBT);
        compound.putBoolean("ignoreDamage", ignoreDamage);
        return compound;
    }

}
