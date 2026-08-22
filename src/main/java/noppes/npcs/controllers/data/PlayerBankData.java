package noppes.npcs.controllers.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.handler.data.IPlayerData;
import noppes.npcs.containers.NpcMiscInventory;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.NBTTags;
import noppes.npcs.containers.ContainerNPCBank;
import noppes.npcs.controllers.BankController;

import javax.annotation.Nonnull;

public class PlayerBankData implements IPlayerData {

	protected static final String dataName = "BankData";

	private final PlayerData main;
	public BankData lastBank;
	private int delay;

	public PlayerBankData(PlayerData playerData) { main = playerData; }

	public @Nonnull BankData get(int bankId) {
		Bank bank = BankController.getInstance().getBank(bankId);
		if (bank == null || main.uuid.isEmpty() || main.player == null || main.player.world.isRemote) {
			return new BankData(bank, main.uuid);
		}
		if (lastBank != null && lastBank.bank.id == bankId && lastBank.isPlayer(main.uuid)) { return lastBank; }
		if (bank.isPublic) { return bank.getPublicData(); }
		lastBank = new BankData(bank, main.uuid);
		File file = CustomNpcs.getWorldSaveDirectory("playerdata/" + main.uuid + "/banks/" + bank.id + ".dat");
		try {
			if (file != null) {
				if (file.exists() && file.isFile()) { lastBank.load(CompressedStreamTools.readCompressed(Files.newInputStream(file.toPath()))); } // load
				else if (!file.exists() || file.delete()) { CompressedStreamTools.writeCompressed(lastBank.getNBT(), Files.newOutputStream(file.toPath())); } // create
			}
		} catch (Exception e) { LogWriter.error(e); }
		return lastBank;
	}

	@Override
	public void load(NBTTagCompound compound) {
		if (compound == null || !compound.hasKey(dataName, 10)) { return; }
		// load old data
		if (compound.hasKey(dataName, 9)) {
			File dir = CustomNpcs.getWorldSaveDirectory("playerdata/" + main.uuid + "/banks");
			NBTTagList list = compound.getTagList("BankData", 10);
			for (int bankPos = 0; bankPos < list.tagCount(); bankPos++) {
				NBTTagCompound nbt = list.getCompoundTagAt(bankPos);
				Bank bank = BankController.getInstance().getBank(nbt.getInteger("DataBankId"));
				if (bank == null) { continue; }
				BankData bd = new BankData(bank, main.uuid);
				int unlockedCeils = nbt.getInteger("unlockedCeils");
				HashMap<Integer, Boolean> upgradedSlots = NBTTags.getBooleanList(nbt.getTagList("UpdatedSlots", 10));
				for (int ceil = 0; ceil < nbt.getTagList("BankInv", 10).tagCount(); ceil++) {
					NBTTagCompound nbtCeils = nbt.getTagList("BankInv", 10).getCompoundTagAt(ceil);
					int c = nbtCeils.getInteger("Slot");
					if (c > unlockedCeils) { break; }
					if (bd.openNew(ceil)) {
						NpcMiscInventory inv = bd.get(ceil);
						if (inv != null) {
							inv.setNewSize(upgradedSlots.get(c) ? 54 : 27);
							inv.load(nbtCeils.getCompoundTag("BankItems"));
						}
					}
				}
				// save has new data
				File file = new File(dir, bank.id + ".dat");
				try {
					if (file.exists() || file.createNewFile()) {
						CompressedStreamTools.writeCompressed(bd.getNBT(), Files.newOutputStream(file.toPath()));
					}
				}
				catch (IOException e) { LogWriter.error(e); }
			}
		}
	}

	@Override
	public NBTTagCompound save(NBTTagCompound compound) {
		NBTTagCompound gameNBT = new NBTTagCompound();
		compound.setTag(dataName, gameNBT);
		return compound;
	}

	public void remove(int bankId) {
		File dir = CustomNpcs.getWorldSaveDirectory("playerdata/" + main.uuid + "/banks");
		File file = new File(dir, bankId + ".dat");
		if (file.exists() && file.delete()) {
			LogWriter.debug("Delete player "+main.uuid+" bank ID: "+bankId);
		}
	}

	public void update(EntityPlayerMP player) {
		if (lastBank != null) {
			if (player.openContainer instanceof ContainerNPCBank) { delay = 200; }
			else if (delay > 0) {
				delay--;
				if (delay == 0) { lastBank = null; }
			}
		}
	}

	public boolean hasBank(int bankId) {
		if (lastBank != null && lastBank.bank.id == bankId) { return true; }
		if (!main.uuid.isEmpty()) {
			File file = CustomNpcs.getWorldSaveDirectory("playerdata/" + main.uuid + "/banks/" + bankId + ".dat");
			return file != null && file.exists() && file.isFile();
		}
		return false;
	}

}
