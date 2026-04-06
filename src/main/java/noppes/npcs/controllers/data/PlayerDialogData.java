package noppes.npcs.controllers.data;

import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import noppes.npcs.api.handler.data.IPlayerData;
import noppes.npcs.client.TextBlockClient;

// Change from Unofficial (BetaZavr)
public class PlayerDialogData implements IPlayerData {

   protected static final String dataName = "DialogData";

   public final TreeMap<Integer, Set<Integer>> dialogsRead = new TreeMap<>();

   @Override
   public void load(CompoundTag compound) {
      dialogsRead.clear();
      if (compound != null && compound.contains(dataName, 9)) {
         ListTag dialogs = compound.getList(dataName, 10);
         for (int i = 0; i < dialogs.size(); ++i) {
            CompoundTag nbtDialog = dialogs.getCompound(i);
            Set<Integer> set = new TreeSet<>();
            for (int id : nbtDialog.getIntArray("OptionRead")) { set.add(id); }
            dialogsRead.put(nbtDialog.getInt("Dialog"), set);
         }
      }
   }

   @Override
   public CompoundTag save(CompoundTag compound) {
      ListTag dialogs = new ListTag();
      for (int dialogId : dialogsRead.keySet()) {
         CompoundTag nbtDialog = new CompoundTag();
         nbtDialog.putInt("Dialog", dialogId);
         int[] set = new int[dialogsRead.get(dialogId).size()];
         int i = 0;
         for (int id :dialogsRead.get(dialogId)) { set[i++] = id; }
         nbtDialog.putIntArray("OptionRead", set);
         dialogs.add(nbtDialog);
      }
      compound.put(dataName, dialogs);
      return compound;
   }

   // New from Unofficial (BetaZavr)
   public void clear() { dialogsRead.clear(); }

   public boolean has(int dialogId) { return dialogsRead.containsKey(dialogId); }

   public void read(int dialogId) {
      if (has(dialogId)) { return; }
      dialogsRead.put(dialogId, new TreeSet<>());
   }

   public void option(int dialogId, int optionId) {
      if (dialogsRead.containsKey(dialogId)) { dialogsRead.put(dialogId, new TreeSet<>()); }
      dialogsRead.get(dialogId).add(optionId);
   }

   public void addLogs(List<TextBlockClient> lines, String string) {

   }

}
