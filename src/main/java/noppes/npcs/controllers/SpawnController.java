package noppes.npcs.controllers;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.zip.GZIPInputStream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandom;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.data.SpawnData;
import noppes.npcs.shared.common.util.LogWriter;

public class SpawnController {

   public final TreeMap<ResourceLocation, List<SpawnData>> biomes = new TreeMap<>();
   public final ArrayList<SpawnData> data = new ArrayList<>();
   public RandomSource random = RandomSource.create();
   public static SpawnController instance;

   public SpawnController() {
      instance = this;
      loadData();
   }

   private void loadData() {
      File saveDir = CustomNpcs.getLevelSaveDirectory();
      if (saveDir != null) {
         try {
            File file = new File(saveDir, "spawns.dat");
            if (file.exists()) {
               loadDataFile(file);
            }
         }
         catch (Exception e) {
            try {
               File file = new File(saveDir, "spawns.dat_old");
               if (file.exists()) { loadDataFile(file); }
            }
            catch (Exception ignored) { }
         }
      }
   }

   private void loadDataFile(File file) throws IOException {
      DataInputStream var1 = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new FileInputStream(file))));
      loadData(var1);
      var1.close();
   }

   public void loadData(DataInputStream stream) throws IOException {
      data.clear();
      CompoundTag compound = NbtIo.read(stream);
      ListTag nbtList = compound.getList("NPCSpawnData", 10);
      for(int i = 0; i < nbtList.size(); ++i) {
         SpawnData spawn = new SpawnData();
         spawn.load(nbtList.getCompound(i));
         data.add(spawn);
      }
      fillBiomeData();
   }

   public CompoundTag getNBT() {
      ListTag list = new ListTag();
      for (SpawnData spawn : data) {
         CompoundTag nbtSpawn = new CompoundTag();
         spawn.save(nbtSpawn);
         list.add(nbtSpawn);
      }
      CompoundTag compound = new CompoundTag();
      compound.put("NPCSpawnData", list);
      return compound;
   }

   public void saveData() {
      try {
         File saveDir = CustomNpcs.getLevelSaveDirectory();
         File file = new File(saveDir, "spawns.dat_new");
         File file1 = new File(saveDir, "spawns.dat_old");
         File file2 = new File(saveDir, "spawns.dat");
         NbtIo.writeCompressed(getNBT(), new FileOutputStream(file));
         if (file1.exists() && !file1.delete()) { LogWriter.debug("Error delete \"" + file1.getName() + "\" file"); }
         if (!file2.renameTo(file1) || (file2.exists() && !file2.delete())) { LogWriter.debug("Error delete or rename \"" + file2.getName() + "\" file"); }
         if (!file.renameTo(file2) || (file.exists() && !file.delete())) { LogWriter.debug("Error delete or rename \"" + file.getName() + "\" file"); }
      }
      catch (Exception e) { LogWriter.except(e); }
   }

   public SpawnData getSpawnData(int id) {
      for (SpawnData spawn : data) {
         if (spawn.id == id) { return spawn; }
      }
      return null;
   }

   public void saveSpawnData(SpawnData spawn) {
      if (spawn.name != null && !spawn.name.isEmpty()) {
         if (spawn.id < 0) { spawn.id = getUnusedId(); }
         SpawnData spawnData = getSpawnData(spawn.id);
         if (spawnData == null) { data.add(spawn); }
         else { spawnData.load(spawn.save(new CompoundTag())); }
         fillBiomeData();
         saveData();
      }
   }

   private void fillBiomeData() {
      biomes.clear();
      for (SpawnData spawn : data) {
         List<SpawnData> list;
         for (Iterator<ResourceLocation> location = spawn.biomes.iterator(); location.hasNext(); list.add(spawn)) {
            ResourceLocation s = location.next();
            list = biomes.computeIfAbsent(s, k -> new ArrayList<>());
         }
      }
   }

   public int getUnusedId() {
      int id = 0;
      for (SpawnData spawn : data) {
         if (spawn.id == id) { id++; }
      }
      return id;
   }

   public void removeSpawnData(int id) {
      ArrayList<SpawnData> newData = new ArrayList<>();
      for (SpawnData spawn : data) {
         if (spawn.id != id && spawn.id > -1) { newData.add(spawn); }
      }
      data.clear();
      data.addAll(newData);
      fillBiomeData();
      saveData();
   }

   public List<SpawnData> getSpawnList(ResourceLocation biome) { return biomes.get(biome); }

   public SpawnData getRandomSpawnData(ResourceLocation biome) {
      List<SpawnData> list = getSpawnList(biome);
      return list != null && !list.isEmpty() ? WeightedRandom.getRandomItem(random, list).orElse(null) : null;
   }

   public boolean hasSpawnList(ResourceLocation biome) { return biomes.containsKey(biome) && !(biomes.get(biome)).isEmpty(); }

   public Map<String, Integer> getScroll() {
      Map<String, Integer> map = new TreeMap<>();
      for (SpawnData spawn : data) { map.put(spawn.name, spawn.id); }
      return map;
   }

}
