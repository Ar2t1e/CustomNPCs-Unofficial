package noppes.npcs.controllers.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.level.ServerPlayer;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NBTTags;
import noppes.npcs.containers.inventories.NpcMiscInventory;
import noppes.npcs.api.handler.data.IPlayerData;
import noppes.npcs.containers.ContainerNPCBank;
import noppes.npcs.controllers.BankController;
import noppes.npcs.shared.common.util.LogWriter;

import javax.annotation.Nonnull;

public class PlayerBankData implements IPlayerData  {

   protected static final String dataName = "BankData";

   private final PlayerData main;
   public BankData lastBank;
   private int delay;

   public PlayerBankData(PlayerData playerData) { main = playerData; }

   public @Nonnull BankData get(int bankId) {
      Bank bank = BankController.getInstance().getBank(bankId);
      if (bank == null || main.uuid.isEmpty() || main.player == null || main.player.level().isClientSide()) {
         return new BankData(bank, main.uuid);
      }
      if (lastBank != null && lastBank.bank.id == bankId && lastBank.isPlayer(main.uuid)) { return lastBank; }
      if (bank.isPublic) { return bank.getPublicData(); }
      lastBank = new BankData(bank, main.uuid);
      File file = CustomNpcs.getLevelSaveDirectory("playerdata/" + main.uuid + "/banks/" + bank.id + ".dat");
      try {
         if (file != null) {
            if (file.exists() && file.isFile()) { lastBank.load(NbtIo.readCompressed(Files.newInputStream(file.toPath()))); } // load
            else if (!file.exists() || file.delete()) { NbtIo.writeCompressed(lastBank.getNBT(), Files.newOutputStream(file.toPath())); } // create
         }
      } catch (Exception e) { LogWriter.error(e); }
      return lastBank;
   }

   @Override
   public void load(CompoundTag compound) {
      if (compound == null || !compound.contains(dataName, 10)) { return; }
      // load old data
      if (compound.contains(dataName, 9)) {
         File dir = CustomNpcs.getLevelSaveDirectory("playerdata/" + main.uuid + "/banks");
         ListTag list = compound.getList("BankData", 10);
         for (int bankPos = 0; bankPos < list.size(); bankPos++) {
            CompoundTag nbt = list.getCompound(bankPos);
            Bank bank = BankController.getInstance().getBank(nbt.getInt("DataBankId"));
            if (bank == null) { continue; }
            BankData bd = new BankData(bank, main.uuid);
            int unlockedCeils = nbt.getInt("unlockedCeils");
            HashMap<Integer, Boolean> upgradedSlots = NBTTags.getBooleanList(nbt.getList("UpdatedSlots", 10));
            for (int ceil = 0; ceil < nbt.getList("BankInv", 10).size(); ceil++) {
               CompoundTag nbtCeils = nbt.getList("BankInv", 10).getCompound(ceil);
               int c = nbtCeils.getInt("Slot");
               if (c > unlockedCeils) { break; }
               if (bd.openNew(ceil)) {
                  NpcMiscInventory inv = bd.get(ceil);
                  if (inv != null) {
                     inv.setNewSize(upgradedSlots.get(c) ? 54 : 27);
                     inv.load(nbtCeils.getCompound("BankItems"));
                  }
               }
            }
            // save has new data
            File file = new File(dir, bank.id + ".dat");
            try {
               if (file.exists() || file.createNewFile()) {
                  NbtIo.writeCompressed(bd.getNBT(), Files.newOutputStream(file.toPath()));
               }
            }
            catch (IOException e) { LogWriter.error(e); }
         }
      }
   }

   @Override
   public CompoundTag save(CompoundTag compound) {
      CompoundTag gameNBT = new CompoundTag();
      compound.put(dataName, gameNBT);
      return compound;
   }

   public void remove(int bankId) {
      File dir = CustomNpcs.getLevelSaveDirectory("playerdata/" + main.uuid + "/banks");
      File file = new File(dir, bankId + ".dat");
      if (file.exists() && file.delete()) {
         LogWriter.debug("Delete player "+main.uuid+" bank ID: "+bankId);
      }
   }

   public void update(ServerPlayer player) {
      if (lastBank != null) {
         if (player.containerMenu instanceof ContainerNPCBank) { delay = 200; }
         else if (delay > 0) {
            delay--;
            if (delay == 0) { lastBank = null; }
         }
      }
   }

   public boolean hasBank(int bankId) {
      if (lastBank != null && lastBank.bank.id == bankId) { return true; }
      if (!main.uuid.isEmpty()) {
         File file = CustomNpcs.getLevelSaveDirectory("playerdata/" + main.uuid + "/banks/" + bankId + ".dat");
         return file != null && file.exists() && file.isFile();
      }
      return false;
   }

}
