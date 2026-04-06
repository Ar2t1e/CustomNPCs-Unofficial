package noppes.npcs.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.Map.Entry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.handler.IQuestHandler;
import noppes.npcs.api.handler.data.IQuestCategory;
import noppes.npcs.client.gui.util.quests.QuestObjective;
import noppes.npcs.constants.EnumQuestRepeat;
import noppes.npcs.constants.EnumQuestTask;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.controllers.data.QuestCategory;
import noppes.npcs.entity.data.DropSet;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketSyncRemove;
import noppes.npcs.packets.client.PacketSyncUpdate;
import noppes.npcs.shared.client.gui.util.NoppesStringUtils;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.NBTJsonUtil;
import noppes.npcs.util.Util;

import javax.annotation.Nullable;

public class QuestController implements IQuestHandler {

   public static QuestController instance = new QuestController();
   public final TreeMap<Integer, QuestCategory> categoriesSync = new TreeMap<>();
   public final TreeMap<Integer, QuestCategory> categories = new TreeMap<>();
   public final TreeMap<Integer, Quest> quests = new TreeMap<>();
   private int lastUsedCatID = 1;
   private int lastUsedQuestID = 1;

   public QuestController() { instance = this; }

   public void load() {
      categories.clear();
      quests.clear();
      lastUsedCatID = 1;
      lastUsedQuestID = 1;
      File file;
      // OLD variant
      try {
         file = new File(CustomNpcs.getLevelSaveDirectory(), "quests.dat");
         if (file.exists()) {
            loadCategoriesOld(file);
            if (!file.delete()) { LogWriter.debug("Error delete \"" + file.getName() + "\" file"); }
            file = new File(CustomNpcs.getLevelSaveDirectory(), "quests.dat_old");
            if (file.exists() && !file.delete()) { LogWriter.debug("Error delete \"" + file.getName() + "\" file"); }
            return;
         }
      }
      catch (Exception e) { LogWriter.except(e); }

      File dir = getDir();
      if (!dir.exists()) {
         if (dir.mkdirs()) { loadDefaultQuests(); }
      }
      else {
         File[] files = dir.listFiles();
         if (files != null) {
            for(File questFile : files) {
               if (questFile.isDirectory()) {
                  QuestCategory category = loadCategoryDir(questFile);
                  for (Entry<Integer, Quest> entry : new ArrayList<>(category.quests.entrySet())) {
                     Integer id = entry.getKey();
                     lastUsedQuestID = Math.max(lastUsedQuestID, id);
                     Quest quest = category.quests.get(id);
                     if (quests.containsKey(id)) {
                        LogWriter.error("Duplicate quest ID:" + quest.id + " from category " + category.title);
                        category.quests.remove(id);
                     } else {
                        quests.put(id, quest);
                     }
                  }
                  ++lastUsedCatID;
                  category.id = lastUsedCatID;
                  categories.put(category.id, category);
               }
            }
         }
      }
   }

   private QuestCategory loadCategoryDir(File dir) {
      QuestCategory category = new QuestCategory();
      category.title = dir.getName();
      category.id = lastUsedCatID++;
      File[] files = dir.listFiles();
      if (files != null) {
         for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".json")) {
               try {
                  Quest quest = new Quest(category);
                  quest.id = Integer.parseInt(file.getName().substring(0, file.getName().length() - 5));
                  quest.loadPartial(NBTJsonUtil.LoadFile(file));
                  category.quests.put(quest.id, quest);
               } catch (Exception var8) {
                  LogWriter.error("Error loading: " + file.getAbsolutePath(), var8);
               }
            }
         }
      }
      return category;
   }

   private void loadCategoriesOld(File file) throws Exception {
      CompoundTag compound = NbtIo.readCompressed(new FileInputStream(file));
      lastUsedCatID = compound.getInt("lastID");
      lastUsedQuestID = compound.getInt("lastQuestID");
      ListTag list = compound.getList("Data", 10);
      for (int i = 0; i < list.size(); ++i) {
         QuestCategory category = new QuestCategory();
         category.load(list.getCompound(i));
         categories.put(category.id, category);
         lastUsedCatID = Math.max(lastUsedCatID, category.id);
         saveCategory(category);
         Iterator<Entry<Integer, Quest>> ita = category.quests.entrySet().iterator();
         while (ita.hasNext()) {
            Entry<Integer, Quest> entry = ita.next();
            Quest quest = entry.getValue();
            quest.id = entry.getKey();
            lastUsedQuestID = Math.max(lastUsedQuestID, quest.id);
            if (quests.containsKey(quest.id)) {
               ita.remove();
            } else {
               saveQuest(category, quest);
            }
         }
      }
   }

   private void loadDefaultQuests() {
      LogWriter.info("TEST: "+lastUsedCatID);
      QuestCategory cat = new QuestCategory();
      cat.id = lastUsedCatID++;
      cat.title = "Village";

      Quest qst1 = new Quest(cat);
      qst1.id = lastUsedQuestID++;
      qst1.level = 1;
      qst1.rewardMoney = 2;
      qst1.repeat = EnumQuestRepeat.MCWEEKLY;
      qst1.title = "quest.base.0";
      qst1.logText = "quest.base.log.text.0";
      qst1.completeText = "quest.base.complete.text.0";

      QuestObjective task = qst1.questInterface.addTask(EnumQuestTask.ITEM);
      task.setItem(new ItemStack(Items.OAK_LOG, 5));
      task.setMaxProgress(5);

      DropSet ds = new DropSet(qst1);
      ds.pos = 0;
      ds.setItem(0, new ItemStack(Items.MUSHROOM_STEW));

      qst1.rewardItems.put(ds.pos, ds);

      saveCategory(cat);
      saveQuest(cat, qst1);
   }

   public void removeCategory(int category) {
      QuestCategory cat = categories.get(category);
      if (cat == null) { return; }
      File dir = new File(getDir(), cat.title);
      if (!Util.instance.removeFile(dir)) {
         LogWriter.error("Error delete " + dir + "; no access or file not uploaded!");
         return;
      }
      for (Integer qId : cat.quests.keySet()) {
         quests.remove(qId);
      }
      categories.remove(category);
      Packets.sendAll(new PacketSyncRemove(category, 3));
   }

   public void saveCategory(QuestCategory category) {
      category.title = NoppesStringUtils.cleanFileName(category.title);
      if (categories.containsKey(category.id)) {
         QuestCategory currentCategory = categories.get(category.id);
         if (!currentCategory.title.equals(category.title)) {
            while(containsCategoryName(category)) { category.title += "_"; }
            File newDir = new File(getDir(), category.title);
            File oldDir = new File(getDir(), currentCategory.title);
            if (newDir.exists()) {
               if (oldDir.exists()) { Util.instance.removeFile(oldDir); }
               return;
            }
            else if (!oldDir.renameTo(newDir)) { return; }
         }
         category.quests.clear();
         category.quests.putAll(currentCategory.quests);
      }
      else {
         if (category.id < 0) {
            ++lastUsedCatID;
            category.id = lastUsedCatID;
         }
         while(containsCategoryName(category)) { category.title += "_"; }
         File dir = new File(getDir(), category.title);
         if (!dir.exists()) {
            dir.mkdirs();
         }
      }
      categories.put(category.id, category);
      Packets.sendAll(new PacketSyncUpdate(category.id, 3, category.save(new CompoundTag())));
   }

   public boolean containsCategoryName(QuestCategory categoryIn) {
      for (QuestCategory category : categories.values()) {
         if (!category.equals(categoryIn) &&
                 category.id != categoryIn.id &&
                 category.title.equalsIgnoreCase(categoryIn.title)) { return true; }
      }
      return false;
   }

   public boolean containsQuestName(QuestCategory category, Quest questIn) {
      for (Quest quest : category.quests.values()) {
         if (!quest.equals(questIn) &&
                 quest.id != questIn.id &&
                 quest.getName().equalsIgnoreCase(questIn.getName())) {
            return true;
         }
      }
      return false;
   }

   public void saveQuest(QuestCategory category, Quest quest) {
      if (category != null) {
         while(containsQuestName(quest.category, quest)) { quest.title = quest.title + "_"; }
         if (quest.id < 0) {
            ++lastUsedQuestID;
            quest.id = lastUsedQuestID;
         }
         quests.put(quest.id, quest);
         category.quests.put(quest.id, quest);
         File dir = new File(getDir(), category.title);
         if (dir.exists() || dir.mkdirs()) {
            File file = new File(dir, quest.id + ".json_new");
            File file1 = new File(dir, quest.id + ".json");
            try {
               NBTJsonUtil.SaveFile(file, quest.savePartial(new CompoundTag()));
               if (file1.exists() && !file1.delete()) { LogWriter.error("Error delete " + file1 + "; no access or file not uploaded!"); }
               if (file.renameTo(file1)) { LogWriter.error("Error rename " + file + "; no access or file not uploaded!"); }
               Packets.sendAll(new PacketSyncUpdate(category.id, 2, quest.save(new CompoundTag())));
            } catch (Exception e) {
               LogWriter.error(e);
            }
         }
      }
   }

   public void removeQuest(Quest quest) {
      File file = new File(new File(getDir(), quest.category.title), quest.id + ".json");
      if (file.delete()) {
         quests.remove(quest.id);
         quest.category.quests.remove(quest.id);
         Packets.sendAll(new PacketSyncRemove(quest.id, 2));
      }
   }

   public File getDir() {
      return new File(CustomNpcs.getLevelSaveDirectory(), "quests");
   }

   @Override
   public List<IQuestCategory> categories() { return new ArrayList<>(categories.values()); }

   @Override
   public @Nullable Quest get(int id) { return quests.get(id); }

   public @Nullable Quest getQuestFromName(String questName) {
      for (Quest quest : quests.values()) {
         if (quest.getName().equalsIgnoreCase(questName)) { return quest; }
      }
      return null;
   }

}
