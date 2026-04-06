package noppes.npcs.controllers.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import noppes.npcs.controllers.ServerCloneController;

public class CloneSpawnData {

   public int tab;
   public String name;
   protected long lastLoaded;
   protected CompoundTag compound;

   public CloneSpawnData(int tabIn, String nameIn) {
      name = nameIn;
      tab = tabIn;
   }

   public CompoundTag getCompound() {
      if (lastLoaded < ServerCloneController.Instance.lastLoaded) {
         compound = ServerCloneController.Instance.getCloneData(null, name, tab);
         lastLoaded = ServerCloneController.Instance.lastLoaded;
      }
      return compound;
   }

   public static Map<Integer, CloneSpawnData> load(ListTag list) {
      Map<Integer, CloneSpawnData> data = new HashMap<>();
      for(int i = 0; i < list.size(); ++i) {
         CompoundTag c = list.getCompound(i);
         int tab = c.getInt("tab");
         String name = c.getString("name");
         if (ServerCloneController.Instance == null || ServerCloneController.Instance.hasClone(tab, name)) {
            data.put(c.getInt("slot"), new CloneSpawnData(tab, name));
         }
      }
      return data;
   }

   public static ListTag save(Map<Integer, CloneSpawnData> data) {
      ListTag list = new ListTag();
      for (Entry<Integer, CloneSpawnData> entry : data.entrySet()) {
         if (ServerCloneController.Instance != null &&
                 !ServerCloneController.Instance.hasClone(entry.getValue().tab, entry.getValue().name)) {
            continue;
         }
         CompoundTag c = new CompoundTag();
         c.putInt("slot", entry.getKey());
         c.putInt("tab", entry.getValue().tab);
         c.putString("name", entry.getValue().name);
         list.add(c);
      }
      return list;
   }

}
