package noppes.npcs.mixin.world.storage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = WorldInfo.class, priority = 502)
public interface IWorldInfoMixin {

    @Accessor void setRandomSeed(long seed);

    @Accessor void setGeneratorOptions(String options);

    @Accessor("spawnX") void setCommonSpawnX(int x);

    @Accessor("spawnY") void setCommonSpawnY(int y);

    @Accessor("spawnZ") void setCommonSpawnZ(int z);

    @Accessor long getLastTimePlayed();
    @Accessor void setLastTimePlayed(long newLastTimePlayed);

    @Accessor long getSizeOnDisk();
    @Accessor void setSizeOnDisk(long newSizeOnDisk);

    @Accessor String getVersionName();
    @Accessor void setVersionName(String newVersionName);

    @Accessor int getVersionId();
    @Accessor void setVersionId(int newVersionId);

    @Accessor boolean isVersionSnapshot();
    @Accessor void setVersionSnapshot(boolean newVersionSnapshot);

    @Accessor void setPlayerTag(NBTTagCompound newPlayerTag);

    @Accessor Map<Integer, NBTTagCompound> getDimensionData();

    @Accessor int getDimension();
    @Accessor void setDimension(int newDimension);

}
