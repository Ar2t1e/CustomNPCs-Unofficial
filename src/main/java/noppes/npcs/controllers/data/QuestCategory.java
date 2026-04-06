package noppes.npcs.controllers.data;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import noppes.npcs.api.handler.data.IQuest;
import noppes.npcs.api.handler.data.IQuestCategory;

public class QuestCategory implements IQuestCategory {

   public final TreeMap<Integer, Quest> quests = new TreeMap<>();
   public int id = -1;
   public String title = "";

   public void load(CompoundTag compound) {
      id = compound.getInt("Slot");
      title = compound.getString("Title");
      ListTag questsList = compound.getList("Dialogs", 10);
      for (int ii = 0; ii < questsList.size(); ++ii) {
         CompoundTag nbtQuest = questsList.getCompound(ii);
         Quest quest = new Quest(this);
         quest.load(nbtQuest);
         quests.put(quest.id, quest);
      }
   }

   public CompoundTag save(CompoundTag compound) {
      compound.putInt("Slot", id);
      compound.putString("Title", title);
      ListTag dialogs = new ListTag();
      for (int dialogId : quests.keySet()) {
         Quest quest = quests.get(dialogId);
         dialogs.add(quest.save(new CompoundTag()));
      }
      compound.put("Dialogs", dialogs);
      return compound;
   }

   public List<IQuest> quests() {
      return new ArrayList<>(this.quests.values());
   }

   public String getName() {
      return this.title;
   }

   public IQuest create() {
      return new Quest(this);
   }

   public Component getTitle() { return Component.translatable(title); }

}
