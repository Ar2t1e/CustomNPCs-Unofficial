package noppes.npcs.controllers.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.controllers.ServerCloneController;

import java.util.HashMap;
import java.util.Map;

public class CloneSpawnData {

    public int tab;
    public String name;
    protected long lastLoaded;
    protected NBTTagCompound compound;

    public CloneSpawnData(int tabIn, String nameIn) {
        name = nameIn;
        tab = tabIn;
    }

    public NBTTagCompound getCompound() {
        if (lastLoaded < ServerCloneController.Instance.lastLoaded) {
            compound = ServerCloneController.Instance.getCloneData(null, name, tab);
            lastLoaded = ServerCloneController.Instance.lastLoaded;
        }
        return compound;
    }

    public static Map<Integer, CloneSpawnData> load(NBTTagList list) {
        Map<Integer, CloneSpawnData> data = new HashMap<>();
        for(int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound c = list.getCompoundTagAt(i);
            int tab = c.getInteger("tab");
            String name = c.getString("name");
            if (ServerCloneController.Instance == null || ServerCloneController.Instance.hasClone(tab, name)) {
                data.put(c.getInteger("slot"), new CloneSpawnData(tab, name));
            }
        }
        return data;
    }

    public static NBTTagList save(Map<Integer, CloneSpawnData> data) {
        NBTTagList list = new NBTTagList();
        for (Map.Entry<Integer, CloneSpawnData> entry : data.entrySet()) {
            if (ServerCloneController.Instance != null &&
                    !ServerCloneController.Instance.hasClone(entry.getValue().tab, entry.getValue().name)) {
                continue;
            }
            NBTTagCompound c = new NBTTagCompound();
            c.setInteger("slot", entry.getKey());
            c.setInteger("tab", entry.getValue().tab);
            c.setString("name", entry.getValue().name);
            list.appendTag(c);
        }
        return list;
    }
}
