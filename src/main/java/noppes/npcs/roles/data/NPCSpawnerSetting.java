package noppes.npcs.roles.data;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.INbt;
import noppes.npcs.api.entity.data.role.IJobSpawner;
import noppes.npcs.api.wrapper.NBTWrapper;
import noppes.npcs.entity.EntityNPCInterface;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class NPCSpawnerSetting implements IJobSpawner.IJobSpawnerSettings {

    protected final @Nonnull EntityNPCInterface parent;
    public final List<Entity> spawned = new ArrayList<>();
    public final Map<Integer, IJobSpawner.IJobSpawnerData> dataEntitys = new TreeMap<>(); // max 7
    public final int[] offset = new int[] { 0, 0, 0 };
    public int spawnType = 0; // 0 =one, 1 = all, 2 = random
    public int number = 0; // current pos then spawned
    public int maximum = 10;
    public boolean despawnOnTargetLost = true;

    public NPCSpawnerSetting(@Nonnull EntityNPCInterface npc) { parent = npc; }

    @Nullable
    @Override
    public IJobSpawner.IJobSpawnerData add(boolean isClone) {
        if (dataEntitys.size() < 7) {
            int slot = dataEntitys.size();
            if (isClone) { dataEntitys.put(slot, new JobSpawnerCloneData(parent)); }
            else { dataEntitys.put(slot, new JobSpawnerNbtData(parent)); }
            return dataEntitys.get(slot);
        }
        return null;
    }

    @Nullable
    @Override
    public IJobSpawner.IJobSpawnerData get(int slotId) {
        if (slotId < 0 || slotId > 7) { throw new CustomNPCsException("Slot ID must be greater than 0 and less than 6"); }
        return dataEntitys.get(slotId);
    }

    @Override
    public void clear() {
        dataEntitys.clear();
        offset[0] = 0;
        offset[1] = 0;
        offset[2] = 0;
        spawnType = 0;
        despawnOnTargetLost = true;
    }

    @Override
    public boolean up(int slot) {
        if (dataEntitys.containsKey(slot) && slot > 0) {
            Map<Integer, IJobSpawner.IJobSpawnerData> entitys = new TreeMap<>();
            for (int i = 0; i < dataEntitys.size(); i++) {
                if (i == slot + 1) { entitys.put(i + 1, dataEntitys.get(i)); }
                else if (i == slot) { entitys.put(i - 1, dataEntitys.get(i)); }
                else { entitys.put(i, dataEntitys.get(i)); }
            }
            dataEntitys.clear();
            dataEntitys.putAll(entitys);
            return true;
        }
        return false;
    }

    @Override
    public boolean down(int slot) {
        if (dataEntitys.containsKey(slot) && slot < dataEntitys.size() - 1) {
            Map<Integer, IJobSpawner.IJobSpawnerData> entitys = new TreeMap<>();
            for (int i = 0; i < dataEntitys.size(); i++) {
                if (i == slot) { entitys.put(i + 1, dataEntitys.get(i)); }
                else if (i == slot - 1) { entitys.put(i - 1, dataEntitys.get(i)); }
                else { entitys.put(i, dataEntitys.get(i)); }
            }
            dataEntitys.clear();
            dataEntitys.putAll(entitys);
            return true;
        }
        return false;
    }

    @Override
    public INbt getNbt() { return new NBTWrapper(save()); }

    @Override
    public void setNbt(INbt nbt) {
        if (nbt != null) { load(nbt.getMCNBT()); }
    }

    public NBTTagCompound save() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("SpawnType", spawnType);
        compound.setIntArray("Offset", offset);
        compound.setBoolean("DespawnOnTargetLost", despawnOnTargetLost);
        NBTTagList list = new NBTTagList();
        for (IJobSpawner.IJobSpawnerData sd : dataEntitys.values()) { list.appendTag(sd.getNbt().getMCNBT()); }
        compound.setTag("DataEntitys", list);
        return compound;
    }

    public void load(NBTTagCompound compound) {
        clear();
        if (compound.hasKey("SpawnMaximum", 3)) { maximum = compound.getInteger("SpawnMaximum"); }
        else { maximum = 10; }
        spawnType = compound.getInteger("SpawnType");
        despawnOnTargetLost = compound.getBoolean("DespawnOnTargetLost");
        int[] array = compound.getIntArray("Offset");
        for (int k = 0; k < 3 && k < array.length; k++) { offset[k] = array[k]; }
        NBTTagList list = compound.getTagList("DataEntitys", 10);
        for (int slot = 0; slot < list.tagCount(); slot++) {
            NBTTagCompound nbt = list.getCompoundTagAt(slot);
            IJobSpawner.IJobSpawnerData sd;
            if (nbt.hasKey("tab", 3) && nbt.hasKey("name", 8)) {
                sd = new JobSpawnerCloneData(parent);
                ((JobSpawnerCloneData) sd).load(nbt);
            }
            else {
                sd = new JobSpawnerNbtData(parent);
                ((JobSpawnerNbtData) sd).load(nbt);
            }
            dataEntitys.put(slot, sd);
        }
    }

}
