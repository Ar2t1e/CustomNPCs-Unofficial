package noppes.npcs.controllers.data;

import net.minecraft.nbt.CompoundTag;

public class FactionOption {

    public boolean decreaseFactionPoints = false;
    public int factionId = -1;
    public int factionPoints = 100;

    public FactionOption(int factionIdIn, int factionPointsIn, boolean take) {
        factionId = factionIdIn;
        factionPoints = factionPointsIn;
        decreaseFactionPoints = take;
    }

    public FactionOption(CompoundTag compound) { load(compound); }

    public void check() {
        if (factionPoints < 0) {
            factionPoints *= -1;
            decreaseFactionPoints = !decreaseFactionPoints;
        }
    }

    public void load(CompoundTag compound) {
        factionId = compound.getInt("FactionID");
        decreaseFactionPoints = compound.getBoolean("IsDecrease");
        factionPoints = compound.getInt("Points");
    }

    public CompoundTag save() {
        CompoundTag compound = new CompoundTag();
        compound.putInt("FactionID", factionId);
        compound.putBoolean("IsDecrease", decreaseFactionPoints);
        compound.putInt("Points", factionPoints);
        return compound;
    }

}
