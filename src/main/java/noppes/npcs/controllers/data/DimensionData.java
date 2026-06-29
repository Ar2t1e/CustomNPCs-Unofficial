package noppes.npcs.controllers.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;

public class DimensionData {

    public boolean isLoad = false;
    public BlockPos spawnPos = BlockPos.ORIGIN;
    public float spawnAngle = 0.0f;
    public int dimensionId = 0;

    public DimensionData() { }

    public DimensionData(WorldServer world) {
        dimensionId = world.provider.getDimension();
        spawnPos = world.getSpawnCoordinate();
        if (spawnPos == null) { spawnPos = world.getSpawnPoint(); }
        spawnAngle = 0;
    }

    public DimensionData(NBTTagCompound compound) {
        isLoad = compound.getBoolean("loaded");
        spawnPos = BlockPos.fromLong(compound.getLong("pos"));
        spawnAngle = compound.getFloat("angle");
        dimensionId = compound.getInteger("id");
    }

    public NBTTagCompound save() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setBoolean("loaded", isLoad);
        compound.setLong("pos", spawnPos.toLong());
        compound.setFloat("angle", spawnAngle);
        compound.setInteger("id", dimensionId);
        return compound;
    }

}
