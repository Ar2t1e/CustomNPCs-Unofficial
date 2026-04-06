package noppes.npcs.roles.data;

import net.minecraft.nbt.CompoundTag;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.entity.data.role.IHealerEffect;

public class HealerSettings implements IHealerEffect {

    public boolean onHimself = false;
    public boolean possibleOnMobs = true;
    public boolean isMassive = true;
    public int id;
    public int range;
    public int speed;
    public int time = 100;
    public int amplifier;
    public byte type; // 0-friendly, 1-unfriendly, 2-all

    public HealerSettings(int idIn, int rangeIn, int speedIn, int amplifierIn, byte typeIn) {
        id = idIn;
        range = rangeIn;
        type = typeIn;
        speed = speedIn;
        amplifier = amplifierIn;
    }

    public HealerSettings(CompoundTag nbtSet) {
        onHimself = nbtSet.getBoolean("OnHimself");
        possibleOnMobs = nbtSet.getBoolean("PossibleOnMobs");
        isMassive = nbtSet.getBoolean("IsMassive");
        type = nbtSet.getByte("Type");
        id = nbtSet.getInt("PotionID");
        range = nbtSet.getInt("Distance");
        speed = nbtSet.getInt("Speed");
        time = nbtSet.getInt("Time");
        amplifier = nbtSet.getInt("Amplifier");
    }

    public CompoundTag save() {
        CompoundTag nbtSet = new CompoundTag();
        nbtSet.putBoolean("OnHimself", onHimself);
        nbtSet.putBoolean("PossibleOnMobs", possibleOnMobs);
        nbtSet.putBoolean("IsMassive", isMassive);
        nbtSet.putByte("Type", type);
        nbtSet.putInt("PotionID", id);
        nbtSet.putInt("Distance", range);
        nbtSet.putInt("Speed", speed);
        nbtSet.putInt("Time", time);
        nbtSet.putInt("Amplifier", amplifier);
        return nbtSet;
    }

    // New from Unofficial (BetaZavr)
    @Override
    public int getEffect() { return id; }

    @Override
    public int getRange() { return range; }

    @Override
    public void setRange(int rangeIn) {
        if (rangeIn < 0 || rangeIn > 64) {
            throw new CustomNPCsException("Range must be between 0 and 64");
        }
        range = rangeIn;
    }

    @Override
    public int getSpeed() { return speed; }

    @Override
    public void setSpeed(int speedIn) {
        if (speedIn < 0 || speedIn > 72000) {
            throw new CustomNPCsException("Speed must be between 0 and 72000");
        }
        speed = speedIn;
    }

    @Override
    public int getTime() { return time; }

    @Override
    public void setTime(int timeIn) {
        if (timeIn < 0 || timeIn > 72000) {
            throw new CustomNPCsException("Time must be between 0 and 72000");
        }
        speed = timeIn;
    }

    @Override
    public int getAmplifier() { return amplifier; }

    @Override
    public void setAmplifier(int amplifierIn) {
        if (amplifierIn < 0) {
            throw new CustomNPCsException("Amplifier must be between 0");
        }
        amplifier = amplifierIn;
    }

    @Override
    public int getType() { return type; }

    @Override
    public void setType(int typeIn) {
        if (typeIn < 0 || typeIn > 2) {
            throw new CustomNPCsException("Type must be between 0 and 2");
        }
        type = (byte) typeIn;
    }

    @Override
    public boolean isOnHimSelf() { return onHimself; }

    @Override
    public void setOnHimSelf(boolean bo) { onHimself = bo; }

    @Override
    public boolean isPossibleOnMobs() { return possibleOnMobs; }

    @Override
    public void setPossibleOnMobs(boolean bo) { possibleOnMobs = bo; }

    @Override
    public boolean isMassive() { return possibleOnMobs; }

    @Override
    public void setIsMassive(boolean bo) { isMassive = bo; }

}
