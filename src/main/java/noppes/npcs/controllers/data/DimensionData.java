package noppes.npcs.controllers.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class DimensionData {

    public boolean isLoad = false;
    public BlockPos spawnPos = BlockPos.ZERO;
    public float spawnAngle = 0.0f;
    public ResourceKey<Level> dimensionId = Level.OVERWORLD;

    public DimensionData() { }

    public DimensionData(@Nonnull Level level) {
        dimensionId = level.dimension();
        spawnPos = level.getSharedSpawnPos();
        spawnAngle = level.getSharedSpawnAngle();
    }

    public DimensionData(CompoundTag compound) {
        isLoad = compound.getBoolean("loaded");
        spawnPos = BlockPos.of(compound.getLong("pos"));
        spawnAngle = compound.getFloat("angle");
        dimensionId = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(compound.getString("id")));
    }

    public CompoundTag save() {
        CompoundTag compound = new CompoundTag();
        compound.putBoolean("loaded", isLoad);
        compound.putLong("pos", spawnPos.asLong());
        compound.putFloat("angle", spawnAngle);
        if (dimensionId != null) { compound.putString("id", dimensionId.location().toString()); }
        return compound;
    }

}
