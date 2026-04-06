package noppes.npcs.controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NBTTags;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.NBTJsonUtil;
import noppes.npcs.util.Util;

public class LinkedNpcController {

   public static LinkedNpcController Instance;
   public final List<LinkedNpcController.LinkedData> list = new ArrayList<>();

   public LinkedNpcController() {
      Instance = this;
      load();
   }

   private void load() {
      try {
         loadNpcs();
      } catch (Exception var2) {
         LogWriter.except(var2);
      }

   }

   public File getDir() {
      File dir = new File(CustomNpcs.getLevelSaveDirectory(), "linkednpcs");
      if (!dir.exists() && !dir.mkdir()) { LogWriter.debug("Error mkdir \"" + dir + "\""); }
      return dir;
   }

   private void loadNpcs() {
      LogWriter.info("Loading Linked Npcs");
      File dir = getDir();
      if (dir.exists()) {
         File[] files = dir.listFiles();
         list.clear();
         if (files != null) {
            for(File file : files) {
               if (file.getName().endsWith(".json")) {
                  try {
                     CompoundTag compound = NBTJsonUtil.LoadFile(file);
                     LinkedNpcController.LinkedData linked = new LinkedNpcController.LinkedData();
                     linked.setNBT(compound);
                     list.add(linked);
                  } catch (Exception var9) {
                     LogWriter.error("Error loading: " + file.getAbsolutePath(), var9);
                  }
               }
            }
         }
      }
      LogWriter.info("Done loading Linked Npcs");
   }

   public void save() {
      for (LinkedData npc : list) {
         try {
            saveNpc(npc);
         } catch (IOException var4) {
            LogWriter.except(var4);
         }
      }
   }

   private void saveNpc(LinkedNpcController.LinkedData npc) throws IOException {
      File file = new File(getDir(), npc.name + ".json_new");
      File file1 = new File(getDir(), npc.name + ".json");
      try {
         NBTJsonUtil.SaveFile(file, npc.getNBT());
         if (file1.exists() && !file1.delete()) { LogWriter.debug("Error delete \"" + file1.getName() + "\" file"); }
         if (!file.renameTo(file1)) { LogWriter.debug("Error rename \"" + file.getName() + "\" file"); }
      } catch (NBTJsonUtil.JsonException var5) {
         LogWriter.except(var5);
      }
   }

   public void loadNpcData(EntityNPCInterface npc) {
      if (!npc.linkedName.isEmpty()) {
         LinkedNpcController.LinkedData data = getData(npc.linkedName);
         if (data == null) {
            npc.linkedLast = 0L;
            npc.linkedName = "";
            npc.linkedData = null;
         }
         else {
            npc.linkedData = data;
            if (npc.getX() != 0.0D || npc.getY() != 0.0D || npc.getZ() != 0.0D) {
               npc.linkedLast = data.time;
               List<int[]> points = npc.ais.getMovingPath();
               CompoundTag compound = NBTTags.nbtMerge(readNpcData(npc), data.data);
               npc.display.load(compound);
               npc.stats.load(compound);
               npc.advanced.load(compound);
               npc.inventory.load(compound);
               npc.ais.load(compound);
               npc.transform.load(compound);
               npc.animation.load(compound);
               npc.ais.setMovingPath(points);
               if (compound.contains("ModelData")) {
                  ((EntityCustomNpc)npc).modelData.load(compound.getCompound("ModelData"));
               }
               npc.updateClient = true;
            }
         }
      }
   }

   private void cleanTags(CompoundTag compound) {
      compound.remove("MovingPathNew");
   }

   public LinkedNpcController.LinkedData getData(String name) {
      Iterator<LinkedData> var2 = list.iterator();
      LinkedNpcController.LinkedData data;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         data = var2.next();
      } while(!data.name.equalsIgnoreCase(name));
      return data;
   }

   private CompoundTag readNpcData(EntityNPCInterface npc) {
      CompoundTag compound = new CompoundTag();
      npc.display.save(compound);
      npc.inventory.save(compound);
      npc.stats.save(compound);
      npc.ais.save(compound);
      npc.advanced.save(compound);
      npc.transform.save(compound);
      compound.put("ModelData", ((EntityCustomNpc)npc).modelData.save());
      return compound;
   }

   public void saveNpcData(EntityNPCInterface npc) {
      CompoundTag compound = readNpcData(npc);
      cleanTags(compound);
      if (!npc.linkedData.data.equals(compound)) {
         npc.linkedData.data = compound;
         npc.linkedData.time = System.currentTimeMillis();
         save();
      }
   }

   public void removeData(String name) {
      list.removeIf(o -> o.name.equalsIgnoreCase(name));
      Util.instance.removeFile(new File(getDir(), name + ".json"));
      save();
   }

   public void addData(String name) {
      if (getData(name) == null && !name.isEmpty()) {
         LinkedNpcController.LinkedData data = new LinkedNpcController.LinkedData();
         data.name = name;
         list.add(data);
         save();
      }
   }

   public static class LinkedData {
      public String name = "LinkedNpc";
      public long time = System.currentTimeMillis();
      public CompoundTag data = new CompoundTag();

      public void setNBT(CompoundTag compound) {
         name = compound.getString("LinkedName");
         data = compound.getCompound("NPCData");
      }

      public CompoundTag getNBT() {
         CompoundTag compound = new CompoundTag();
         compound.putString("LinkedName", name);
         compound.put("NPCData", data);
         return compound;
      }
   }

}
