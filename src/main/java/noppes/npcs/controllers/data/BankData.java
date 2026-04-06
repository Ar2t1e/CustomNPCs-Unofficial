package noppes.npcs.controllers.data;

import java.io.File;
import java.nio.file.Files;
import java.util.*;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.Component;
import noppes.npcs.CustomNpcs;
import noppes.npcs.containers.ContainerNPCBank;
import noppes.npcs.containers.NpcMiscInventory;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketBankReOpen;
import noppes.npcs.packets.client.PacketBankSave;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.BankController;
import noppes.npcs.util.ValueUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BankData {

	protected final List<EntityPlayerMP> listeners = new ArrayList<>();
	private final @Nonnull String uuid;
	private final Map<Integer, NpcMiscInventory> cells = new TreeMap<>();
	public @Nonnull Bank bank;

	public BankData(@Nullable Bank bankIn, @Nonnull String uuidIn) {
		bank = bankIn != null ? bankIn : new Bank();
		uuid = uuidIn;
		initSettings();
	}

	public NBTTagCompound getNBT() {
		NBTTagCompound nbtBD = new NBTTagCompound();
		nbtBD.setInteger("id", bank.id);
		NBTTagList list = new NBTTagList();
		for (int ceil : cells.keySet()) {
			NBTTagCompound nbtCeil = new NBTTagCompound();
			nbtCeil.setInteger("ceil", ceil);
			nbtCeil.setInteger("slots", cells.get(ceil).getSizeInventory());
			NBTTagCompound invNbt = cells.get(ceil).save();
			nbtCeil.setTag("NpcMiscInv", invNbt.getTag("NpcMiscInv"));
			list.appendTag(nbtCeil);
		}
		nbtBD.setTag("cells", list);
		return nbtBD;
	}

	public UUID getUUID() { return UUID.fromString(uuid); }

	public void load(NBTTagCompound nbtBankData) {
		bank = BankController.getInstance().getBank(nbtBankData.getInteger("id"));
		NBTTagList list = nbtBankData.getTagList("cells", 10);
		// old type
		if (list.hasNoTags() && nbtBankData.hasKey("ceils", 9)) { list = nbtBankData.getTagList("ceils", 10); }
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound nbtCeil = list.getCompoundTagAt(i);
			int size = nbtCeil.getInteger("slots");
			int ceil = nbtCeil.getInteger("ceil");
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
		if (bank.isPublic) { file = CustomNpcs.getWorldSaveDirectory("banks/" + bank.id + ".dat"); }
		else { file = CustomNpcs.getWorldSaveDirectory("playerdata/" + uuid + "/banks/" + bank.id + ".dat"); }
		try {
			if (file != null && (!file.exists() || file.delete())) {
				CompressedStreamTools.writeCompressed(getNBT(), Files.newOutputStream(file.toPath()));
				LogWriter.debug("Save bank data ID: "+bank.isPublic+"/"+bank.id+" to file: "+file);
			}
		}
		catch (Exception e) { LogWriter.error(e); }
	}

	public synchronized void addListener(EntityPlayerMP player) {
		if (player != null && !listeners.contains(player)) { listeners.add(player); }
	}

	public void removeListener(EntityPlayerMP player) { if (player != null) { listeners.remove(player); } }

	public boolean hasListeners() { return !listeners.isEmpty(); }

	public void openToPlayer(EntityPlayerMP player, int ceilId, int scrollY, int ceilPos, int ceilsUpdate) {
		if (!bank.ceilSettings.containsKey(ceilId)) { return; }
		String name = player.getName();
		if (bank.isPublic && !player.isCreative() && !bank.access.isEmpty() && !bank.owner.equals(name) &&
				((bank.isWhiteList && !bank.access.contains(name)) || (!bank.isWhiteList && bank.access.contains(name)))) {
			if (player.openContainer instanceof ContainerNPCBank) { player.closeContainer(); }
			player.sendMessage(Component.translatable("message.bank.not.access"));
			return;
		}
		initSettings();
		Packets.send(player, new PacketBankSave(bank.save()));
		NBTTagCompound nbtBD = getNBT();
		nbtBD.setInteger("GuiCeil", ceilId);
		nbtBD.setInteger("GuiScrollY", scrollY);
		nbtBD.setInteger("GuiCeilPos", ceilPos);
		nbtBD.setInteger("GuiCeilsUpdate", ceilsUpdate);
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
		for (EntityPlayerMP player : new ArrayList<>(listeners)) {
			if (player.openContainer instanceof ContainerNPCBank) {
				Packets.send(player, new PacketBankReOpen());
				player.openContainer.detectAndSendChanges();
			}
			else { removeListener(player); }
		}
	}

	public synchronized void initSettings() {
		for (Bank.CeilSettings cs : bank.ceilSettings.values()) {
			if (cells.containsKey(cs.ceil)) {
				if (cells.get(cs.ceil).getSizeInventory() < cs.startCells) { cells.get(cs.ceil).setNewSize(cs.startCells); }
				else if (cells.get(cs.ceil).getSizeInventory() > cs.maxCells) { cells.get(cs.ceil).setNewSize(cs.maxCells); }
			}
			else { cells.put(cs.ceil, new NpcMiscInventory(0)); }
		}
		for (int ceil : new ArrayList<>(cells.keySet())) {
			if (!bank.ceilSettings.containsKey(ceil)) { cells.remove(ceil); }
		}
	}

	public boolean isPlayer(String uuidIn) { return uuid.equals(uuidIn); }

}
