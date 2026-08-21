package noppes.npcs.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.CustomNpcs;
import noppes.npcs.containers.ContainerManageBanks;
import noppes.npcs.containers.ContainerNPCBank;
import noppes.npcs.controllers.data.Bank;
import noppes.npcs.packets.server.SPacketBankGet;
import noppes.npcs.packets.server.SPacketBanksGet;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.Util;

public class BankController {

	protected final List<Bank> banks = new ArrayList<>();
	protected static BankController instance;

	public BankController() {
		loadBanksData();
		if (banks.isEmpty()) {
			Bank bank = new Bank();
			bank.id = 0;
			bank.name = "Default Bank";
			banks.add(bank);
		}
	}

	public static BankController getInstance() {
		if (instance == null) { instance = new BankController(); }
		return instance;
	}

	private void loadBanksData() {
		CustomNpcs.debugData.start(null);
		File saveDir = CustomNpcs.getWorldSaveDirectory();
		if (saveDir == null) {
			CustomNpcs.debugData.end(null);
			return;
		}
		try {
			File file = new File(saveDir, "bank.dat");
			if (file.exists()) { loadBanksData(file); }
			else { save(); }
		} catch (Exception e) {
			try {
				File file = new File(saveDir, "bank.dat_old");
				if (file.exists()) { loadBanksData(file); }
				else { save(); }
			} catch (Exception ex) { LogWriter.error(ex); }
		}
		CustomNpcs.debugData.end(null);
	}

	private void loadBanksData(File file) throws IOException {
		loadBanks(CompressedStreamTools.readCompressed(Files.newInputStream(file.toPath())));
	}

	public void loadBanks(NBTTagCompound compound) {
		List<Bank> banksIn = new ArrayList<>();
		NBTTagList list = compound.getTagList("Data", 10);
		for (int i = 0; i < list.tagCount(); ++i) {
			NBTTagCompound nbtBank = list.getCompoundTagAt(i);
			Bank bank = new Bank();
			bank.load(nbtBank);
			banksIn.add(bank);
		}
		banks.clear();
		banks.addAll(banksIn);
	}

	public void loadBank(NBTTagCompound nbtBank) {
		int bankId = nbtBank.getInteger("BankID");
		if (nbtBank.hasKey("BankID", 3) && bankId >= 0) {
			Bank bank = null;
			for (Bank b : banks) {
				if (b.id == bankId) {
					bank = b;
					break;
				}
			}
			if (bank == null) { bank = addNewBank(); }
			bank.load(nbtBank);
			// delete OLD
			if (bank.isPublic) {
				File datasDir = CustomNpcs.getWorldSaveDirectory("playerdata");
				if (datasDir != null) {
					File[] list = datasDir.listFiles();
					if (list != null) {
						for (File playerDir : list) {
							if (playerDir.isDirectory()) { Util.instance.removeFile(new File(playerDir, "banks/"+bank.id+".dat")); }
						}
					}
				}
			}
			else { Util.instance.removeFile(CustomNpcs.getWorldSaveDirectory("banks/"+bank.id+".dat")); }
			if (CustomNpcs.Server != null) {
				for (EntityPlayerMP player : CustomNpcs.Server.getPlayerList().getPlayers()) {
					if (player.openContainer instanceof ContainerManageBanks) {
						SPacketBanksGet.sendBankDataAll(player); // scroll data
						if (((ContainerManageBanks) player.openContainer).isBank(bankId)) {
							SPacketBankGet.sendBank(player, bank, ((ContainerManageBanks) player.openContainer).ceil);
						} // manage banks
					}
					else if (player.openContainer instanceof ContainerNPCBank && ((ContainerNPCBank) player.openContainer).data.bank.id == bankId) {
						player.closeContainer();
					} // bank
				}
			}
			save();
		}
	}

	public NBTTagCompound getNBT() {
		NBTTagList list = new NBTTagList();
		for (Bank bank : banks) { list.appendTag(bank.save()); }
		NBTTagCompound compound = new NBTTagCompound();
		compound.setTag("Data", list);
		return compound;
	}

	public Bank getBank(int bankId) {
		for (Bank bank : banks) {
			if (bank.id == bankId) { return bank; }
		}
		return null;
	}

	public void save() {
		if (CustomNpcs.Server == null) { return; }
		try {
			File saveDir = CustomNpcs.getWorldSaveDirectory();
			File file = new File(saveDir, "bank.dat_new");
			File file1 = new File(saveDir, "bank.dat_old");
			File file2 = new File(saveDir, "bank.dat");
			CompressedStreamTools.writeCompressed(getNBT(), Files.newOutputStream(file.toPath()));
			if (file1.exists() && !file1.delete()) { LogWriter.debug("Error delete \"" + file1.getName() + "\" file"); }
			if (!file2.renameTo(file1) || (file2.exists() && !file2.delete())) { LogWriter.debug("Error delete or rename \"" + file2.getName() + "\" file"); }
			if (!file.renameTo(file2) || (file.exists() && !file.delete())) { LogWriter.debug("Error delete or rename \"" + file.getName() + "\" file"); }
		} catch (Exception e) {
			LogWriter.error(e);
		}
	}

	public int getUnusedId() {
		int id = 0;
		while (getBank(id) != null) { id++; }
		return id;
	}

	public Bank addNewBank() {
		Bank bank = new Bank();
		bank.id = getUnusedId();
		while (true) {
			boolean isBreak = true;
			for (Bank b : banks) {
				if (b.name.equals(bank.name)) {
					isBreak = false;
					break;
				}
			}
			if (isBreak) { break; }
			else { bank.name += "_"; }
		}
		banks.add(bank);
		return bank;
	}

	public void removeBank(int bankId) {
		for (Bank bank : banks) {
			if (bank.id == bankId) {
				if (banks.remove(bank) && CustomNpcs.Server != null) {
					Util.instance.removeFile(CustomNpcs.getWorldSaveDirectory("banks/"+bank.id+".dat"));
					File datasDir = CustomNpcs.getWorldSaveDirectory("playerdata");
					if (datasDir != null) {
						File[] list = datasDir.listFiles();
						if (list != null) {
							for (File playerDir : list) {
								if (!playerDir.isDirectory()) { continue; }
								Util.instance.removeFile(new File(playerDir, "banks/"+bank.id+".dat"));
							}
						}
					}
				}
				break;
			}
		}
		if (CustomNpcs.Server != null) {
			for (EntityPlayerMP pl : CustomNpcs.Server.getPlayerList().getPlayers()) {
				if (pl.openContainer instanceof ContainerManageBanks) {
					SPacketBanksGet.sendBankDataAll(pl); // scroll data
					if (((ContainerManageBanks) pl.openContainer).isBank(bankId)) {
						SPacketBankGet.sendBank(pl, new Bank(), 0);
					} // manage banks
				}
				else if (pl.openContainer instanceof ContainerNPCBank && ((ContainerNPCBank) pl.openContainer).data.bank.id == bankId) {
					pl.closeContainer();
				} // bank
			}
		}
		save();
	}

	// New from Unofficial (BetaZavr)
	public void update() { // every 1 min --> ServerTickHandler.cnpcServerTick()
		if (CustomNpcs.Server != null) {
			for (Bank bank : banks) { bank.freeUpMemory(); }
		}
	}

	public List<Bank> getBanks() { return new ArrayList<>(banks); }

}
