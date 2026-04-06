package noppes.npcs.controllers.data;

import java.util.HashMap;
import net.minecraft.nbt.CompoundTag;
import noppes.npcs.NBTTags;
import noppes.npcs.api.handler.data.IPlayerData;
import noppes.npcs.roles.JobItemGiver;

public class PlayerItemGiverData implements IPlayerData {

   protected final HashMap<Integer, Long> itemGivers = new HashMap<>();
   protected final HashMap<Integer, Integer> chained = new HashMap<>();

   @Override
   public void load(CompoundTag compound) {
      chained.clear();
      itemGivers.clear();
      chained.putAll(NBTTags.getIntegerIntegerMap(compound.getList("ItemGiverChained", 10)));
      itemGivers.putAll(NBTTags.getIntegerLongMap(compound.getList("ItemGiversList", 10)));
   }

   @Override
   public CompoundTag save(CompoundTag compound) {
      compound.put("ItemGiverChained", NBTTags.nbtIntegerIntegerMap(chained));
      compound.put("ItemGiversList", NBTTags.nbtIntegerLongMap(itemGivers));
      return compound;
   }

   public boolean notInteractedBefore(JobItemGiver jobItemGiver) { return !itemGivers.containsKey(jobItemGiver.itemGiverId); }

   public long getTime(JobItemGiver jobItemGiver) { return itemGivers.get(jobItemGiver.itemGiverId); }

   public void setTime(JobItemGiver jobItemGiver, long day) { itemGivers.put(jobItemGiver.itemGiverId, day); }

   public int getItemIndex(JobItemGiver jobItemGiver) { return chained.getOrDefault(jobItemGiver.itemGiverId, 0); }

   public void setItemIndex(JobItemGiver jobItemGiver, int i) { chained.put(jobItemGiver.itemGiverId, i); }

   public void clear() {
      itemGivers.clear();
      chained.clear();
   }

}
