package noppes.npcs.controllers.data;

import java.io.File;
import java.nio.file.Files;
import java.util.*;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.containers.inventories.NpcMiscInventory;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.containers.ContainerNPCBank;
import noppes.npcs.controllers.BankController;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketBankReOpen;
import noppes.npcs.packets.client.PacketBankSave;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.ValueUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BankData {

   protected final List<ServerPlayer> listeners = new ArrayList<>();
   private final @Nonnull String uuid;
   private final Map<Integer, NpcMiscInventory> cells = new TreeMap<>();
   public @Nonnull Bank bank;

   public BankData(@Nullable Bank bankIn, @Nonnull String uuidIn) {
      //LogWriter.pathInfo("Create bank data: "+uuidIn+"; "+bankIn, 5);
      bank = bankIn != null ? bankIn : new Bank();
      uuid = uuidIn;
      initSettings();
   }

   public CompoundTag getNBT() {
      CompoundTag nbtBD = new CompoundTag();
      nbtBD.putInt("id", bank.id);
      ListTag list = new ListTag();
      for (int ceil : cells.keySet()) {
         CompoundTag nbtCeil = new CompoundTag();
         nbtCeil.putInt("ceil", ceil);
         nbtCeil.putInt("slots", cells.get(ceil).getContainerSize());
         CompoundTag invNbt = cells.get(ceil).save();
         nbtCeil.put("NpcMiscInv", Objects.requireNonNull(invNbt.get("NpcMiscInv")));
         list.add(nbtCeil);
      }
      nbtBD.put("cells", list);
      return nbtBD;
   }

   public UUID getUUID() { return UUID.fromString(uuid); }

   public void load(CompoundTag nbtBankData) {
      bank = BankController.getInstance().getBank(nbtBankData.getInt("id"));
      ListTag list = nbtBankData.getList("cells", 10);
      // old type
      if (list.isEmpty() && nbtBankData.contains("ceils", 9)) { list = nbtBankData.getList("ceils", 10); }
      for (int i = 0; i < list.size(); i++) {
         CompoundTag nbtCeil = list.getCompound(i);
         int size = nbtCeil.getInt("slots");
         int ceil = nbtCeil.getInt("ceil");
         if (cells.containsKey(ceil)) { cells.get(ceil).setNewSize(size); }
         else { cells.put(ceil, new NpcMiscInventory(size)); }
         cells.get(ceil).load(nbtCeil);
      }
      initSettings();
   }

   public void save() {
      LogWriter.debug("Start save bank data...");
      if (bank.id < 0 ||
              !Thread.currentThread().getName().toLowerCase().contains("server") ||
              BankController.getInstance().getBank(bank.id) == null) { return; }
      File file;
      if (bank.isPublic) { file = CustomNpcs.getLevelSaveDirectory("banks/" + bank.id + ".dat"); }
      else { file = CustomNpcs.getLevelSaveDirectory("playerdata/" + uuid + "/banks/" + bank.id + ".dat"); }
      try {
         if (file != null && (!file.exists() || file.delete())) {
            NbtIo.writeCompressed(getNBT(), Files.newOutputStream(file.toPath()));
            LogWriter.debug("Save bank data ID: "+bank.isPublic+"/"+bank.id+" to file: "+file);
         }
      }
      catch (Exception e) { LogWriter.error(e); }
   }

   public synchronized void addListener(ServerPlayer player) {
      if (player != null && !listeners.contains(player)) { listeners.add(player); }
   }

   public void removeListener(ServerPlayer player) { if (player != null) { listeners.remove(player); } }

   public boolean hasListeners() { return !listeners.isEmpty(); }

   public void openToPlayer(ServerPlayer player, int ceilId, int scrollY, int ceilPos, int ceilsUpdate) {
      if (!bank.ceilSettings.containsKey(ceilId)) { return; }
      String name = player.getName().getString();
      if (bank.isPublic && !player.isCreative() && !bank.access.isEmpty() && !bank.owner.equals(name) &&
              ((bank.isWhiteList && !bank.access.contains(name)) || (!bank.isWhiteList && bank.access.contains(name)))) {
         if (player.containerMenu instanceof ContainerNPCBank) { player.closeContainer(); }
         player.sendSystemMessage(Component.translatable("message.bank.not.access"));
         return;
      }
      initSettings();
      Packets.send(player, new PacketBankSave(bank.save()));
      CompoundTag nbtBD = getNBT();
      nbtBD.putInt("GuiCeil", ceilId);
      nbtBD.putInt("GuiScrollY", scrollY);
      nbtBD.putInt("GuiCeilPos", ceilPos);
      nbtBD.putInt("GuiCeilsUpdate", ceilsUpdate);
      NoppesUtilServer.openContainerGui(player, EnumGuiType.PlayerBank, (buffer) -> buffer.writeNbt(nbtBD));
   }

   public @Nullable NpcMiscInventory get(int ceil) { return cells.get(ceil); }

   public boolean openNew(int ceil) {
      if (!cells.containsKey(ceil) && bank.ceilSettings.containsKey(ceil)) {
         cells.put(ceil, new NpcMiscInventory(ValueUtil.correctInt(bank.ceilSettings.get(ceil).startCells, 0, 198)));
         setChanged();
         return true;
      }
      return false;
   }

   public synchronized void setChanged() {
      save();
      for (ServerPlayer player : new ArrayList<>(listeners)) {
         if (player.containerMenu instanceof ContainerNPCBank) {
            Packets.send(player, new PacketBankReOpen());
            player.containerMenu.broadcastChanges();
         }
         else { removeListener(player); }
      }
   }

   public synchronized void initSettings() {
      for (Bank.CeilSettings cs : bank.ceilSettings.values()) {
         if (cells.containsKey(cs.ceil)) {
            if (cells.get(cs.ceil).getContainerSize() < cs.startCells) { cells.get(cs.ceil).setNewSize(cs.startCells); }
            else if (cells.get(cs.ceil).getContainerSize() > cs.maxCells) { cells.get(cs.ceil).setNewSize(cs.maxCells); }
         }
         else { cells.put(cs.ceil, new NpcMiscInventory(0)); }
      }
      for (int ceil : new ArrayList<>(cells.keySet())) {
         if (!bank.ceilSettings.containsKey(ceil)) { cells.remove(ceil); }
      }
   }

   public boolean isPlayer(String uuidIn) { return uuid.equals(uuidIn); }

}
