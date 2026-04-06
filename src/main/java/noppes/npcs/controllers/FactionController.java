package noppes.npcs.controllers;

import java.awt.*;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
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

   public FactionController() {
      factions.put(0, new Faction(0, "Friendly", new Color(0x00DD00).getRGB(), 2000));
      factions.put(1, new Faction(1, "Neutral", new Color(0xF2DD00).getRGB(), 1000));
      factions.put(2, new Faction(2, "Aggressive", new Color(0xD00D00).getRGB(), 0));
   }

   public void load() {
      factions.clear();
      lastUsedID = 0;
      try {
         File saveDir = CustomNpcs.getLevelSaveDirectory();
         if (saveDir != null) {
            try {
               File file = new File(saveDir, "factions.dat");
               if (file.exists()) {
                  loadFactionsFile(file);
               }
            } catch (Exception var9) {
               try {
                  File file = new File(saveDir, "factions.dat_old");
                  if (file.exists()) { loadFactionsFile(file); }
               }
               catch (Exception ignored) { }
            }
         }
      } finally {
         EventHooks.onGlobalFactionsLoaded(this);
         if (factions.isEmpty()) {
            factions.put(0, new Faction(0, "Friendly", 56576, 2000));
            factions.put(1, new Faction(1, "Neutral", 15916288, 1000));
            factions.put(2, new Faction(2, "Aggressive", 14483456, 0));
         }
      }
   }

   private void loadFactionsFile(File file) throws IOException {
      DataInputStream var1 = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new FileInputStream(file))));
      loadFactions(var1);
      var1.close();
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

   @SuppressWarnings("all")
   public void saveFactions() {
      try {
         File saveDir = CustomNpcs.getLevelSaveDirectory();
         File file = new File(saveDir, "factions.dat_new");
         File file1 = new File(saveDir, "factions.dat_old");
         File file2 = new File(saveDir, "factions.dat");
         NbtIo.writeCompressed(getNBT(), new FileOutputStream(file));
         if (file1.exists()) { file1.delete(); }
         file2.renameTo(file1);
         if (file2.exists()) { file2.delete(); }
         file.renameTo(file2);
         if (file.exists()) { file.delete(); }
      }
      catch (Exception e) { LogWriter.except(e); }
   }

   public Faction getFaction(int faction) {
      return factions.get(faction);
   }

   public void saveFaction(Faction faction) {
      if (faction.id < 0) {
         faction.id = getUnusedId();
         while (hasName(faction.name)) { faction.name = faction.name + "_"; }
      } else {
         Faction existing = factions.get(faction.id);
         if (existing != null && !existing.name.equals(faction.name)) {
            while(hasName(faction.name)) {
               faction.name = faction.name + "_";
            }
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
      ++lastUsedID;
      return lastUsedID;
   }

   public IFaction delete(int id) {
      if (id >= 0 && factions.size() > 1) {
         Faction faction = factions.remove(id);
         if (faction == null) {
            return null;
         } else {
            saveFactions();
            faction.id = -1;
            Packets.sendAll(new PacketSyncRemove(id, 1));
            return faction;
         }
      } else {
         return null;
      }
   }

   public int getFirstFactionId() {
      return factions.keySet().iterator().next();
   }

   @SuppressWarnings("all")
   public Faction getFirstFaction() {
      return factions.values().iterator().next();
   }

   public boolean hasName(String newName) {
       if (!newName.trim().isEmpty()) {
           Iterator<Faction> var2 = factions.values().iterator();
           Faction faction;
           do {
               if (!var2.hasNext()) {
                   return false;
               }

               faction = var2.next();
           } while (!faction.name.equals(newName));
       }
       return true;
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
      for(Iterator<Faction> var3 = factions.values().iterator(); var3.hasNext(); ++i) {
         Faction faction = var3.next();
         names[i] = faction.name.toLowerCase();
      }
      return names;
   }

   public List<IFaction> list() {
      return new ArrayList<>(factions.values());
   }

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
