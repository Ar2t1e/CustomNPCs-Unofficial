package noppes.npcs.controllers.data;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

public class DimensionData {

    public boolean isLoad = false;
    public BlockPos spawnPos = BlockPos.ZERO;
    public float spawnAngle = 0.0f;

    public DimensionData() { }

    public DimensionData(BlockPos pos, float angle) {
        spawnPos = pos;
        spawnAngle = angle;
    }

    public DimensionData(CompoundTag compound) {
        isLoad = compound.getBoolean("loaded");
        spawnPos = BlockPos.of(compound.getLong("pos"));
        spawnAngle = compound.getFloat("angle");
    }

    public CompoundTag save() {
        CompoundTag compound = new CompoundTag();
        compound.putBoolean("loaded", isLoad);
        compound.putLong("pos", spawnPos.asLong());
        compound.putFloat("angle", spawnAngle);
        return compound;
    }

}
