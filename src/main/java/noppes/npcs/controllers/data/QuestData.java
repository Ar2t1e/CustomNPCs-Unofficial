package noppes.npcs.controllers.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import noppes.npcs.constants.EnumQuestTask;
import noppes.npcs.client.gui.util.quests.QuestObjective;

import java.util.ArrayList;

public class QuestData {

   public Quest quest;
   public long startIn = System.currentTimeMillis();
   public boolean isCompleted;
   public final CompoundTag extraData = new CompoundTag();

   public QuestData(Quest questIn) {
      quest = questIn;
      int pos = 0;
      for (QuestObjective task : quest.questInterface.tasks) {
         if (task.getEnumType() == EnumQuestTask.KILL || task.getEnumType() == EnumQuestTask.AREAKILL || task.getEnumType() == EnumQuestTask.MANUAL) {
            if (!extraData.contains("Targets", 9)) { extraData.put("Targets", new ListTag()); }
            CompoundTag nbt = new CompoundTag();
            nbt.putString("Slot", task.getTargetName());
            nbt.putInt("Value", 0);
            nbt.putInt("ObjectPos", pos);
            extraData.getList("Targets", 10).add(nbt);
         }
         else if (task.getEnumType() == EnumQuestTask.CRAFT) {
            if (task.getItem().isEmpty()) { continue; }
            if (extraData.contains("Crafts", 9)) { extraData.put("Crafts", new ListTag()); }
            CompoundTag nbt = new CompoundTag();
            nbt.put("Item", task.getItemStack().save(new CompoundTag()));
            nbt.putInt("Value", 0);
            nbt.putInt("ObjectPos", pos);
            extraData.getList("Crafts", 10).add(nbt);
         }
         else if (task.getEnumType() == EnumQuestTask.LOCATION) {
            if (extraData.contains("Locations", 9)) { extraData.put("Locations", new ListTag()); }
            CompoundTag nbt = new CompoundTag();
            nbt.putString("Location", task.getTargetName());
            nbt.putBoolean("Found", false);
            nbt.putInt("ObjectPos", pos);
            extraData.getList("Locations", 10).add(nbt);
         }
         pos++;
      }
   }

   public void save(CompoundTag compound) {
      compound.putBoolean("QuestCompleted", isCompleted);
      compound.putLong("StartIn", startIn);
      compound.put("ExtraData", extraData);
   }

   public QuestData load(CompoundTag compound) {
      isCompleted = compound.getBoolean("QuestCompleted");
      startIn = compound.getLong("StartIn");
      for (String key : new ArrayList<>(extraData.getAllKeys())) { extraData.remove(key); }
      for (String key : new ArrayList<>(compound.getCompound("ExtraData").getAllKeys())) {
         Tag tag = compound.getCompound("ExtraData").get(key);
         if (tag != null) { extraData.put(key, tag); }
      }
      return this;
   }

}
