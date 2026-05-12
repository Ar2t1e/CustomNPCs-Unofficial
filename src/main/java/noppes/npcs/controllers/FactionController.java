package noppes.npcs.controllers;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.zip.GZIPInputStream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import noppes.npcs.CustomNpcs;
import noppes.npcs.EventHooks;
import noppes.npcs.api.handler.IFactionHandler;
import noppes.npcs.api.handler.data.IFaction;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketSyncRemove;
import noppes.npcs.packets.client.PacketSyncUpdate;
import noppes.npcs.shared.common.util.LogWriter;

public class FactionController implements IFactionHandler {

   public final TreeMap<Integer, Faction> factionsSync = new TreeMap<>();
   public final TreeMap<Integer, Faction> factions = new TreeMap<>();
   public static final FactionController instance = new FactionController();
   private int lastUsedID = 0;

   public FactionController() { createDefaultFactions(); }

   public void load() {
      factions.clear();
      lastUsedID = 0;
      try {
         File saveDir = CustomNpcs.getLevelSaveDirectory();
         if (saveDir != null) {
            try {
               File file = new File(saveDir, "factions.dat");
               if (file.exists()) { loadFactionsFile(file); }
            }
            catch (Exception e) {
               try {
                  File file = new File(saveDir, "factions.dat_old");
                  if (file.exists()) { loadFactionsFile(file); }
               }
               catch (Exception ignored) { }
            }
         }
      }
      finally {
         EventHooks.onGlobalFactionsLoaded(this);
         if (factions.isEmpty()) { createDefaultFactions(); }
      }
   }

   private void createDefaultFactions() {
      if (!factions.containsKey(0)) {
         Faction friendly = new Faction(0, "faction.name.friendly", 0x00DD00, 2000);
         friendly.frendFactions.add(1);
         factions.put(0, friendly);
      }
      if (!factions.containsKey(1)) {
         Faction neutral = new Faction(1, "faction.name.neutral", 0xF2DD00, 1000);
         neutral.flag = new ResourceLocation(CustomNpcs.MODID + ":textures/cloak/baconcape.png");
         factions.put(1, neutral);
      }
      if (!factions.containsKey(2)) {
         Faction aggressive = new Faction(2, "faction.name.aggressive", 0xDD0000, 0);
         aggressive.attackFactions.add(0);
         aggressive.attackFactions.add(1);
         aggressive.flag = new ResourceLocation(CustomNpcs.MODID + ":textures/cloak/enderdragoncape.png");
         factions.put(2, aggressive);
      }
   }

   private void loadFactionsFile(File file) throws IOException {
      DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new FileInputStream(file))));
      loadFactions(inputStream);
      inputStream.close();
   }

   public void loadFactions(DataInputStream stream) throws IOException {
      CompoundTag compound = NbtIo.read(stream);
      lastUsedID = compound.getInt("lastID");
      ListTag list = compound.getList("NPCFactions", 10);
      factions.clear();
      for (int i = 0; i < list.size(); ++i) {
         CompoundTag nbtFaction = list.getCompound(i);
         Faction faction = new Faction();
         faction.load(nbtFaction);
         factions.put(faction.id, faction);
      }
   }

   public CompoundTag getNBT() {
      ListTag list = new ListTag();
      for (int slot : factions.keySet()) {
         Faction faction = factions.get(slot);
         CompoundTag nbtFaction = new CompoundTag();
         faction.save(nbtFaction);
         list.add(nbtFaction);
      }
      CompoundTag compound = new CompoundTag();
      compound.putInt("lastID", lastUsedID);
      compound.put("NPCFactions", list);
      return compound;
   }

   public void saveFactions() {
      try {
         File saveDir = CustomNpcs.getLevelSaveDirectory();
         File file = new File(saveDir, "factions.dat_new");
         File file1 = new File(saveDir, "factions.dat_old");
         File file2 = new File(saveDir, "factions.dat");
         NbtIo.writeCompressed(getNBT(), new FileOutputStream(file));
         if (file1.exists() && !file1.delete()) { LogWriter.debug("Error delete \"" + file1.getName() + "\" file"); }
         if (!file2.renameTo(file1) || (file2.exists() && !file2.delete())) { LogWriter.debug("Error delete or rename \"" + file2.getName() + "\" file"); }
         if (!file.renameTo(file2) || (file.exists() && !file.delete())) { LogWriter.debug("Error delete or rename \"" + file.getName() + "\" file"); }
      }
      catch (Exception e) { LogWriter.except(e); }
   }

   public Faction getFaction(int faction) { return factions.get(faction); }

   public void saveFaction(Faction faction) {
      if (faction.id < 0) {
         faction.id = getUnusedId();
         while (hasName(faction.name)) { faction.name = faction.name + "_"; }
      } else {
         Faction existing = factions.get(faction.id);
         if (existing != null && !existing.name.equals(faction.name)) {
            while(hasName(faction.name)) { faction.name = faction.name + "_"; }
         }
      }
      factions.remove(faction.id);
      factions.put(faction.id, faction);
      Packets.sendAll(new PacketSyncUpdate(faction.id, 1, faction.save(new CompoundTag())));
      saveFactions();
   }

   public int getUnusedId() {
      if (lastUsedID == 0) {
         for (int catId : factions.keySet()) {
            if (catId > lastUsedID) { lastUsedID = catId; }
         }
      }
      return ++lastUsedID;
   }

   @Override
   public IFaction delete(int id) {
      if (id >= 0 && factions.size() > 1) {
         Faction faction = factions.remove(id);
         if (faction != null) {
            saveFactions();
            faction.id = -1;
            Packets.sendAll(new PacketSyncRemove(id, 1));
            return faction;
         }
      }
      return null;
   }

   public int getFirstFactionId() { return factions.keySet().iterator().next(); }

   @SuppressWarnings("unused")
   public Faction getFirstFaction() { return factions.values().iterator().next(); }

   public boolean hasName(String newName) {
      if (newName.trim().isEmpty()) {
         return true;
      }
      for (Faction faction : new ArrayList<>(factions.values())) {
         if (faction.name.equals(newName)) { return true; }
      }
      return false;
   }

   public Faction getFactionFromName(String factionName) {
      for (Faction faction : factions.values()) {
         if (faction.name.equalsIgnoreCase(factionName)) { return faction; }
      }
      return null;
   }

   public String[] getNames() {
      String[] names = new String[factions.size()];
      int i = 0;
      for (Faction faction : new ArrayList<>(factions.values())) {
         names[i++] = faction.name.toLowerCase();
      }
      return names;
   }

   @Override
   public List<IFaction> list() { return new ArrayList<>(factions.values()); }

   @Override
   public IFaction create(String name, int color) {
      Faction faction = new Faction();
      while (hasName(name)) { name = name + "_"; }
      faction.name = name;
      faction.color = color;
      saveFaction(faction);
      return faction;
   }

   @Override
   public IFaction get(int id) {
      return factions.get(id);
   }

}
