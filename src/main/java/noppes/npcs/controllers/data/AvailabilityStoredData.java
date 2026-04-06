package noppes.npcs.controllers.data;

import net.minecraft.nbt.CompoundTag;
import noppes.npcs.constants.EnumAvailabilityStoredData;

public class AvailabilityStoredData {

    public String key;
    public String value;
    public EnumAvailabilityStoredData type;

    public AvailabilityStoredData(CompoundTag nbt) {
        key = nbt.getString("Key");
        value = nbt.getString("Value");
        if (nbt.contains("Has", 1)) {
            if (nbt.getBoolean("Has")) { type = EnumAvailabilityStoredData.ONLY; }
            else { type = EnumAvailabilityStoredData.EXCEPT; }
        } else {
            int t = nbt.getInt("Type");
            if (t < 0) { t *= -1; }
            type = EnumAvailabilityStoredData.values()[t % EnumAvailabilityStoredData.values().length];
        }
    }

    public AvailabilityStoredData(String k, String v, EnumAvailabilityStoredData t) {
        key = k;
        value = v;
        type = t;
    }

    public CompoundTag save() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("Key", key);
        nbt.putString("Value", value);
        nbt.putInt("Type", type.ordinal());
        return nbt;
    }

}
