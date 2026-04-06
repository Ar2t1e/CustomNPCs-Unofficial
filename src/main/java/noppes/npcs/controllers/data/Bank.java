package noppes.npcs.controllers.data;

import java.io.File;
import java.nio.file.Files;
import java.util.*;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import noppes.npcs.CustomNpcs;
import noppes.npcs.containers.inventories.NpcMiscInventory;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.shared.common.util.LogWriter;

import javax.annotation.Nonnull;

public class Bank {

   public static class CeilSettings {

      public ItemStack openStack = ItemStack.EMPTY;
      public ItemStack upgradeStack = ItemStack.EMPTY;
      public int openMoney = 0;
      public int openDonat = 0;
      public int upgradeMoney = 0;
      public int upgradeDonat = 0;
      public int ceil = 0;
      public int startCells = 1;
      public int maxCells = 27;
      public boolean isFree = false;

      public CeilSettings() {}

      public CeilSettings(CompoundTag nbtCeil) { load(nbtCeil); }

      public void load(CompoundTag nbtCeil) {
         if (nbtCeil.contains("CeilCurrency", 10)) { openStack = ItemStack.of(nbtCeil.getCompound("CeilCurrency")); }
         else { openStack = ItemStack.EMPTY; }
         if (nbtCeil.contains("CeilUpgrade", 10)) { upgradeStack = ItemStack.of(nbtCeil.getCompound("CeilUpgrade")); }
         else { upgradeStack = ItemStack.EMPTY; }
         startCells = nbtCeil.getInt("StartCeil");
         maxCells = nbtCeil.getInt("MaxCeil");
         ceil = nbtCeil.getInt("CeilId");
         upgradeMoney = nbtCeil.getInt("CeilUpgradeMoney");
         upgradeDonat = nbtCeil.getInt("CeilUpgradeDonat");
         openMoney = nbtCeil.getInt("CeilCurrencyMoney");
         openDonat = nbtCeil.getInt("CeilCurrencyDonat");
         isFree = nbtCeil.getBoolean("Free");
      }

      public void set(CeilSettings settings) {
         openStack = settings.openStack;
         upgradeStack = settings.upgradeStack;
         openMoney = settings.openMoney;
         openDonat = settings.openDonat;
         upgradeMoney = settings.upgradeMoney;
         upgradeDonat = settings.upgradeDonat;
         startCells = settings.startCells;
         maxCells = settings.maxCells;
         isFree = settings.isFree;
      }

      public void save(CompoundTag nbtCeil) {
         if (openStack != null && !openStack.isEmpty()) { nbtCeil.put("CeilCurrency", openStack.save(new CompoundTag())); }
         if (upgradeStack != null && !upgradeStack.isEmpty()) { nbtCeil.put("CeilUpgrade", upgradeStack.save(new CompoundTag())); }
         nbtCeil.putInt("StartCeil", startCells);
         nbtCeil.putInt("MaxCeil", maxCells);
         nbtCeil.putInt("CeilId", ceil);
         nbtCeil.putInt("CeilUpgradeMoney", upgradeMoney);
         nbtCeil.putInt("CeilUpgradeDonat", upgradeDonat);
         nbtCeil.putInt("CeilCurrencyMoney", openMoney);
         nbtCeil.putInt("CeilCurrencyDonat", openDonat);
         nbtCeil.putBoolean("Free", isFree);
      }
   }

   protected final List<ServerPlayer> listeners = new ArrayList<>();
   public final Map<Integer, CeilSettings> ceilSettings = new TreeMap<>();
   public final List<String> access = new ArrayList<>();
   public boolean isPublic = false;
   public boolean isWhiteList = false;
   public boolean isChanging = true;
   public int id = -1;
   public String name = "Default Bank";
   public String owner = "";
   private BankData lastPublicBank;

   public Bank() {
      for (int ceil = 0; ceil < 2; ceil++) {
         CeilSettings cs = new CeilSettings();
         cs.ceil = ceil;
         if (ceil == 1) {
            cs.startCells = 9;
            cs.maxCells = 27;
            cs.openStack = new ItemStack(Items.DIAMOND, 1);
            cs.upgradeStack = new ItemStack(Items.GOLD_INGOT, 2);
         } else {
            cs.startCells = 27;
            cs.maxCells = 54;
            cs.upgradeStack = new ItemStack(Items.GOLD_INGOT, 1);
         }
         ceilSettings.put(ceil, cs);
      }
   }

   public CeilSettings addCeil() {
      CeilSettings cs = new CeilSettings();
      cs.ceil = ceilSettings.size();
      ceilSettings.put(cs.ceil, cs);
      return cs;
   }

   public void removeCeil(int ceilId) {
      if (!ceilSettings.containsKey(ceilId)) { return; }
      Map<Integer, CeilSettings> newCS = new TreeMap<>();
      int i = 0;
      for (int c : ceilSettings.keySet()) {
         if (c == ceilId || ceilSettings.get(c).ceil == ceilId) {
            continue;
         }
         ceilSettings.get(c).ceil = i;
         newCS.put(i, ceilSettings.get(c));
         i++;
      }
      ceilSettings.clear();
      ceilSettings.putAll(newCS);
   }

   public @Nonnull BankData getPublicData() {
      if (lastPublicBank != null) { return lastPublicBank; }
      // load
      File file = CustomNpcs.getLevelSaveDirectory("banks/" + id + ".dat");
      lastPublicBank = new BankData(this, "");
      try {
         // create new or new
         if (file != null) {
            if (file.exists() && file.isFile()) { lastPublicBank.load(NbtIo.readCompressed(Files.newInputStream(file.toPath()))); } // load
            else if (!file.exists() || file.delete()) { NbtIo.writeCompressed(lastPublicBank.getNBT(), Files.newOutputStream(file.toPath())); } // create
         }
      }
      catch (Exception e) { LogWriter.error("Error load bank data from file", e); }
      return lastPublicBank;
   }

   public boolean bankIsOpen() { return lastPublicBank != null; }

   public void freeUpMemory() {
      if (lastPublicBank != null && !lastPublicBank.hasListeners()) { lastPublicBank = null; }
   }

   public void load(CompoundTag nbtBank) {
      id = nbtBank.getInt("BankID");
      name = nbtBank.getString("Username");
      ceilSettings.clear();
      access.clear();
      String pldOwner = owner;
      if (nbtBank.contains("StartSlots", 3)) {
         isPublic = false;
         isWhiteList = false;
         isChanging = true;
         int maxCells = nbtBank.getInt("MaxSlots");
         NpcMiscInventory oldCInv = new NpcMiscInventory(maxCells);
         NpcMiscInventory oldUInv = new NpcMiscInventory(maxCells);
         oldCInv.load(nbtBank.getCompound("BankCurrency"));
         oldUInv.load(nbtBank.getCompound("BankUpgrade"));
         for (int ceil = 0; ceil < oldCInv.getContainerSize(); ceil++) {
            CeilSettings cs = new CeilSettings();
            cs.ceil = ceil;
            cs.openStack = oldCInv.getItem(ceil);
            cs.upgradeStack = oldUInv.getItem(ceil);
            cs.upgradeStack.setCount(1);
            cs.startCells = 27;
            cs.maxCells = cs.upgradeStack.isEmpty() ? 27 : 54;
            ceilSettings.put(ceil, cs);
         }
      }
      else {
         ListTag list = nbtBank.getList("BankCells", 10);
         if (list.isEmpty() && nbtBank.contains("BankCeils", 9)) { list = nbtBank.getList("BankCeils", 10); } // old type
         for (int ceil = 0; ceil < list.size(); ceil++) { ceilSettings.put(ceil, new CeilSettings(list.getCompound(ceil))); }
         isPublic = nbtBank.getBoolean("IsPublic");
         isWhiteList = nbtBank.getBoolean("IsWhiteList");
         if (nbtBank.contains("IsChanging", 1)) { isChanging = nbtBank.getBoolean("IsChanging"); }
         owner = nbtBank.getString("Owner");
         list = nbtBank.getList("BankNamesPlayersAccess", 8);
         for (int i = 0; i < list.size(); i++) { access.add(list.getString(i)); }
      }
      PlayerDataController pData = PlayerDataController.instance;
      if (pData != null) {
         List<String> names = PlayerDataController.instance.getPlayerNames();
         if (!owner.isEmpty()) {
            if (!names.contains(owner)) {
               boolean notFound = true;
               for (String name : names) {
                  if (name.equalsIgnoreCase(owner)) {
                     owner = name;
                     notFound = false;
                     break;
                  }
               }
               if (notFound) {
                  owner = pldOwner;
               }
            }
         }
         if (!access.isEmpty()) {
            List<String> newAccess = new ArrayList<>();
            boolean isChanged = false;
            for (String ac : access) {
               if (!names.contains(ac)) {
                  for (String name : names) {
                     if (name.equalsIgnoreCase(ac)) {
                        newAccess.add(name);
                        isChanged = true;
                        break;
                     }
                  }
                  continue;
               }
               newAccess.add(ac);
            }
            if (access.size() != newAccess.size() || isChanged) {
               access.clear();
               access.addAll(newAccess);
            }
         }
         if (!access.isEmpty()) {
            Collections.sort(access);
         }
      }
   }

   public CompoundTag save() {
      CompoundTag nbtBank = new CompoundTag();
      nbtBank.putInt("BankID", id);
      nbtBank.putString("Username", name);
      nbtBank.putBoolean("IsPublic", isPublic);
      nbtBank.putBoolean("IsWhiteList", isWhiteList);
      nbtBank.putBoolean("IsChanging", isChanging);
      nbtBank.putString("Owner", owner);
      if (name.isEmpty()) { name = "Default Bank"; }
      ListTag listCS = new ListTag();
      for (int ceil = 0; ceil < ceilSettings.size(); ++ceil) {
         CompoundTag nbtCeil = new CompoundTag();
         nbtCeil.putInt("Ceil", ceil);
         ceilSettings.get(ceil).save(nbtCeil);
         listCS.add(nbtCeil);
      }
      nbtBank.put("BankCells", listCS);
      ListTag listNPA = new ListTag();
      for (String n : access) { listNPA.add(StringTag.valueOf(n)); }
      nbtBank.put("BankNamesPlayersAccess", listNPA);
      return nbtBank;
   }

}
