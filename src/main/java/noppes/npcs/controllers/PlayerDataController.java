package noppes.npcs.controllers;

import java.io.File;
import java.nio.file.Files;
import java.util.*;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import noppes.npcs.CustomNpcs;
import noppes.npcs.containers.inventories.NpcMiscInventory;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.NBTJsonUtil;
import noppes.npcs.util.Util;

import javax.annotation.Nullable;

public class PlayerDataController {

   public static PlayerDataController instance;

   public PlayerDataController() {
      CustomNpcs.debugData.start(null);
      instance = this;
      File dir = CustomNpcs.getLevelSaveDirectory("playerdata");
      if (dir == null) { return; }
      for (File playerDir : Objects.requireNonNull(dir.listFiles())) {
         // OLD
         if (!playerDir.isDirectory() && playerDir.getName().endsWith(".json")) {
            try {
               CompoundTag nbt = NBTJsonUtil.LoadFile(playerDir);
               String uuid = "nouuidplayer", name = "nonameplayer";
               if (nbt.contains("PlayerName", 8) && !nbt.getString("PlayerName").isEmpty()) {
                  name = nbt.getString("PlayerName");
               }
               if (nbt.contains("UUID", 8) && !nbt.getString("UUID").isEmpty()) {
                  uuid = nbt.getString("UUID");
               }
               // banks
               File banksDirTemp = CustomNpcs.getLevelSaveDirectory("playerdata/" + uuid + "/banks");
               if (banksDirTemp == null) {
                  CustomNpcs.debugData.end(null);
                  return;
               }
               if ((banksDirTemp.exists() || banksDirTemp.mkdirs()) && nbt.contains("BankData", 9)) {
                  for (int i = 0; i < nbt.getList("BankData", 10).size(); i++) {
                     CompoundTag nbtOldBank = nbt.getList("BankData", 10).getCompound(i);
                     CompoundTag nbtBD = new CompoundTag();
                     int bankID = nbtOldBank.getInt("DataBankId");
                     nbtBD.putInt("id", bankID);
                     int maxCells = nbtOldBank.getInt("UnlockedSlots");
                     ListTag list = new ListTag();
                     for (int c = 0; c < nbtOldBank.getList("BankInv", 10).size(); c++) {
                        CompoundTag nbtOldCeil = nbtOldBank.getList("BankInv", 10).getCompound(c);
                        int ceilID = nbtOldCeil.getInt("Slot");
                        if (ceilID >= maxCells) { continue; }
                        CompoundTag nbtCeil = new CompoundTag();
                        int slots = 27;
                        for (int u = 0; u < nbtOldBank.getList("UpdatedSlots", 10).size(); u++) {
                           if (nbtOldBank.getList("UpdatedSlots", 10).getCompound(u).getInt("Slot") == ceilID) {
                              if (nbtOldBank.getList("UpdatedSlots", 10).getCompound(u)
                                      .getBoolean("Boolean")) {
                                 slots = 54;
                              }
                              break;
                           }
                        }
                        NpcMiscInventory inv = new NpcMiscInventory(slots);
                        inv.load(nbtOldCeil.getCompound("BankItems"));
                        nbtCeil.putInt("ceil", ceilID);
                        nbtCeil.putInt("slots", slots);
                        CompoundTag invNbt = inv.save();
                        nbtCeil.put("NpcMiscInv", Objects.requireNonNull(invNbt.get("NpcMiscInv")));
                        list.add(nbtCeil);
                     }
                     nbtBD.put("ceils", list);
                     File bankFile = new File(banksDirTemp, bankID + ".dat");
                     if (!bankFile.exists() && !bankFile.createNewFile()) {
                        LogWriter.error("Not create player bank data ");
                     }
                     NbtIo.writeCompressed(nbtBD, Files.newOutputStream(bankFile.toPath()));
                  }
               }
               // main
               File playerDirTemp = new File(dir, uuid);
               if (playerDirTemp.exists() || playerDirTemp.mkdirs()) {
                  File tempFile = new File(playerDirTemp, name + ".json");
                  if (tempFile.exists() || tempFile.createNewFile()) {
                     nbt.remove("BankData");
                     Util.instance.saveFile(tempFile, nbt);
                  }
                  Util.instance.removeFile(playerDir);
               }
            }
            catch (Exception e) { LogWriter.error("Error loading Old file: " + playerDir.getAbsolutePath(), e); }
         }
      }
      CustomNpcs.debugData.end(null);
   }

   private File getPlayerDirectory(String user_name_or_uuid) {
      for (File playerDir : Objects.requireNonNull(Objects.requireNonNull(CustomNpcs.getLevelSaveDirectory("playerdata")).listFiles())) {
         if (!playerDir.isDirectory()) { continue; }
         if (playerDir.getName().equalsIgnoreCase(user_name_or_uuid)) { return playerDir; }
         File[] files = playerDir.listFiles();
         if (files != null) {
            for (File file : files) {
               if (file.isFile() && file.getName().endsWith(".json")
                       && file.getName().replace(".json", "").equalsIgnoreCase(user_name_or_uuid)) {
                  return playerDir;
               }
            }
         }
      }
      return null;
   }

   public List<String> getPlayerNames() {
      List<String> list = new ArrayList<>();
      File dir = CustomNpcs.getLevelSaveDirectory("playerdata");
      if (dir != null && dir.exists()) {
         File[] dirs = dir.listFiles();
         if (dirs != null) {
            for (File playerDir : dirs) {
               if (!playerDir.isDirectory()) { continue; }
               for (File file : Objects.requireNonNull(playerDir.listFiles())) {
                  if (file.isFile() && file.getName().endsWith(".json")) {
                     list.add(file.getName().replace(".json", ""));
                     break;
                  }
               }
            }
         }
      }
      return list;
   }

   public @Nullable PlayerData getDataFromUsername(@Nullable MinecraftServer server, @Nullable String userPartNameOrUUID) {
      if (userPartNameOrUUID == null || userPartNameOrUUID.isEmpty()) { return null; }
      if (server == null) { server = CustomNpcs.Server; }
      if (server != null) {
         ServerPlayer player = server.getPlayerList().getPlayerByName(userPartNameOrUUID);
         if (player != null) { return PlayerData.get(player); }
         try {
            player = server.getPlayerList().getPlayer(UUID.fromString(userPartNameOrUUID));
            if (player != null) { return PlayerData.get(player); }
         }
         catch (Exception ignored) { }
      }
      File dir = CustomNpcs.getLevelSaveDirectory("playerdata");
      if (dir != null && dir.exists()) {
         File[] dirs = dir.listFiles();
         if (dirs != null) {
            for (File playerDir : dirs) {
               if (!playerDir.isDirectory()) { continue; }
               for (File file : Objects.requireNonNull(playerDir.listFiles())) {
                  if (file.isFile() && file.getName().endsWith(".json")) {
                     String uuid = playerDir.getName();
                     String name = file.getName().replace(".json", "");
                     if (name.toLowerCase().contains(userPartNameOrUUID.toLowerCase()) || uuid.equalsIgnoreCase(userPartNameOrUUID)) {
                        if (server != null) {
                           ServerPlayer player = server.getPlayerList().getPlayerByName(name);
                           if (player != null) { return PlayerData.get(player); }
                        }
                        PlayerData data = new PlayerData();
                        data.setNBT(PlayerData.loadPlayerData(uuid, name));
                        return data;
                     }
                     break;
                  }
               }
            }
         }
      }
      return null;
   }

   @SuppressWarnings("unused")
   public List<PlayerData> getAllPlayerDatas(@Nullable MinecraftServer server, @Nullable String userPartNameOrUUID) {
      if (server == null) { server = CustomNpcs.Server; }
      ArrayList<PlayerData> list = new ArrayList<>();
      if (server != null && userPartNameOrUUID != null) {
         for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player.getUUID().toString().equals(userPartNameOrUUID) || player.getName().getString().contains(userPartNameOrUUID)) {
               list.add(PlayerData.get(player));
            }
         }
      }
      File dir = CustomNpcs.getLevelSaveDirectory("playerdata");
      if (dir != null && dir.exists()) {
         File[] dirs = dir.listFiles();
         if (dirs != null) {
            for (File playerDir : dirs) {
               if (!playerDir.isDirectory()) { continue; }
               for (File file : Objects.requireNonNull(playerDir.listFiles())) {
                  if (file.isFile() && file.getName().endsWith(".json")) {
                     String uuid = playerDir.getName();
                     String name = file.getName().replace(".json", "");
                     if (userPartNameOrUUID == null || userPartNameOrUUID.isEmpty() ||
                             name.toLowerCase().contains(userPartNameOrUUID.toLowerCase()) ||
                             uuid.equalsIgnoreCase(userPartNameOrUUID)) {
                        if (server == null || server.getPlayerList().getPlayerByName(name) == null) {
                           PlayerData data = new PlayerData();
                           data.setNBT(PlayerData.loadPlayerData(uuid, name));
                           list.add(data);
                        }
                     }
                     break;
                  }
               }
            }
         }
      }
      return list;
   }

   public void addPlayerMessage(MinecraftServer server, String username, PlayerMail mail) {
      PlayerData data = getDataFromUsername(server, username);
      if (data != null) {
         data.mailData.addMail(mail);
         data.save(false);
      }
   }

   public String hasPlayer(String user_name_or_uuid) {
      File playerDir = getPlayerDirectory(user_name_or_uuid);
      String realName = "";
      if (playerDir != null) {
         File[] files = playerDir.listFiles();
         if (files != null) {
            for (File file : files) {
               if (file.isFile() && file.getName().endsWith(".json")
                       && file.getName().replace(".json", "").equalsIgnoreCase(user_name_or_uuid)) {
                  realName = file.getName().replace(".json", "");
                  break;
               }
            }
         }
      }
      return realName;
   }

}
