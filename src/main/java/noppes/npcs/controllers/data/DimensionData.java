package noppes.npcs.controllers.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class DimensionData {

    public String name = "";
    public String suffix = "";
    public String worldName = "";
    public boolean isLoad = false;
    public boolean isRemoved = false;
    public BlockPos spawnPos = BlockPos.ORIGIN;
    public float spawnAngle = 0.0f;
    public int dimensionId = 0;

    public DimensionData() { }

    public DimensionData(NBTTagCompound compound) {
        isLoad = compound.getBoolean("loaded");
        isRemoved = compound.getBoolean("deleted");
        spawnPos = BlockPos.fromLong(compound.getLong("pos"));
        spawnAngle = compound.getFloat("angle");
        dimensionId = compound.getInteger("id");
        name = compound.getString("name");
        suffix = compound.getString("suffix");
        worldName = compound.getString("world_name");
    }

    public NBTTagCompound save() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setBoolean("loaded", isLoad);
        compound.setBoolean("deleted", isRemoved);
        compound.setLong("pos", spawnPos.toLong());
        compound.setFloat("angle", spawnAngle);
        compound.setInteger("id", dimensionId);
        compound.setString("name", name);
        compound.setString("suffix", suffix);
        compound.setString("world_name", worldName);
        return compound;
    }

}
