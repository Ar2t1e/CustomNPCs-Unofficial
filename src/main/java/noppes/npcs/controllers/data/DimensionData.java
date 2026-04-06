package noppes.npcs.controllers.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class DimensionData {

    public boolean isLoad = false;
    public BlockPos spawnPos = BlockPos.ORIGIN;
    public float spawnAngle = 0.0f;

    public DimensionData() { }

    public DimensionData(BlockPos pos, float angle) {
        spawnPos = pos;
        spawnAngle = angle;
    }

    public DimensionData(NBTTagCompound compound) {
        isLoad = compound.getBoolean("loaded");
        spawnPos = BlockPos.fromLong(compound.getLong("pos"));
        spawnAngle = compound.getFloat("angle");
    }

    public NBTTagCompound save() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setBoolean("loaded", isLoad);
        compound.setLong("pos", spawnPos.toLong());
        compound.setFloat("angle", spawnAngle);
        return compound;
    }

}
