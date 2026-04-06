package noppes.npcs.controllers.data;

import java.io.File;
import java.nio.file.Files;
import java.util.*;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import noppes.npcs.CustomNpcs;
import noppes.npcs.containers.NpcMiscInventory;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.controllers.PlayerDataController;

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

		public CeilSettings(NBTTagCompound nbtCeil) {
			this.load(nbtCeil);
		}

		public void load(NBTTagCompound nbtCeil) {
			if (nbtCeil.hasKey("CeilCurrency", 10)) { openStack = new ItemStack(nbtCeil.getCompoundTag("CeilCurrency")); }
			else { openStack = ItemStack.EMPTY; }
			if (nbtCeil.hasKey("CeilUpgrade", 10)) { upgradeStack = new ItemStack(nbtCeil.getCompoundTag("CeilUpgrade")); }
			else { upgradeStack = ItemStack.EMPTY; }
			startCells = nbtCeil.getInteger("StartCeil");
			maxCells = nbtCeil.getInteger("MaxCeil");
			ceil = nbtCeil.getInteger("CeilId");
			upgradeMoney = nbtCeil.getInteger("CeilUpgradeMoney");
			upgradeDonat = nbtCeil.getInteger("CeilUpgradeDonat");
			openMoney = nbtCeil.getInteger("CeilCurrencyMoney");
			openDonat = nbtCeil.getInteger("CeilCurrencyDonat");
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

		public void save(NBTTagCompound nbtCeil) {
			if (openStack != null && !openStack.isEmpty()) { nbtCeil.setTag("CeilCurrency", openStack.writeToNBT(new NBTTagCompound())); }
			if (upgradeStack != null && !upgradeStack.isEmpty()) { nbtCeil.setTag("CeilUpgrade", upgradeStack.writeToNBT(new NBTTagCompound())); }
			nbtCeil.setInteger("StartCeil", startCells);
			nbtCeil.setInteger("MaxCeil", maxCells);
			nbtCeil.setInteger("CeilId", ceil);
			nbtCeil.setInteger("CeilUpgradeMoney", upgradeMoney);
			nbtCeil.setInteger("CeilUpgradeDonat", upgradeDonat);
			nbtCeil.setInteger("CeilCurrencyMoney", openMoney);
			nbtCeil.setInteger("CeilCurrencyDonat", openDonat);
			nbtCeil.setBoolean("Free", isFree);
		}
	}

	protected final List<EntityPlayerMP> listeners = new ArrayList<>();
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
		File file = CustomNpcs.getWorldSaveDirectory("banks/" + id + ".dat");
		lastPublicBank = new BankData(this, "");
		try {
			// create new or new
			if (file != null) {
				if (file.exists() && file.isFile()) { lastPublicBank.load(CompressedStreamTools.readCompressed(Files.newInputStream(file.toPath()))); } // load
				else if (!file.exists() || file.delete()) { CompressedStreamTools.writeCompressed(lastPublicBank.getNBT(), Files.newOutputStream(file.toPath())); } // create
			}
		}
		catch (Exception e) { LogWriter.error("Error load bank data from file", e); }
		return lastPublicBank;
	}

	public boolean bankIsOpen() { return lastPublicBank != null; }

	public void freeUpMemory() {
		if (lastPublicBank != null && !lastPublicBank.hasListeners()) { lastPublicBank = null; }
	}

	public void load(NBTTagCompound nbtBank) {
		id = nbtBank.getInteger("BankID");
		name = nbtBank.getString("Username");
		ceilSettings.clear();
		access.clear();
		String pldOwner = owner;
		if (nbtBank.hasKey("StartSlots", 3)) {
			isPublic = false;
			isWhiteList = false;
			isChanging = true;
			int maxCells = nbtBank.getInteger("MaxSlots");
			NpcMiscInventory oldCInv = new NpcMiscInventory(maxCells);
			NpcMiscInventory oldUInv = new NpcMiscInventory(maxCells);
			oldCInv.load(nbtBank.getCompoundTag("BankCurrency"));
			oldUInv.load(nbtBank.getCompoundTag("BankUpgrade"));
			for (int ceil = 0; ceil < oldCInv.getSizeInventory(); ceil++) {
				CeilSettings cs = new CeilSettings();
				cs.ceil = ceil;
				cs.openStack = oldCInv.getStackInSlot(ceil);
				cs.upgradeStack = oldUInv.getStackInSlot(ceil);
				cs.upgradeStack.setCount(1);
				cs.startCells = 27;
				cs.maxCells = cs.upgradeStack.isEmpty() ? 27 : 54;
				ceilSettings.put(ceil, cs);
			}
		}
		else {
			NBTTagList list = nbtBank.getTagList("BankCells", 10);
			if (list.hasNoTags() && nbtBank.hasKey("BankCeils", 9)) { list = nbtBank.getTagList("BankCeils", 10); } // old type
			for (int ceil = 0; ceil < list.tagCount(); ceil++) { ceilSettings.put(ceil, new CeilSettings(list.getCompoundTagAt(ceil))); }
			isPublic = nbtBank.getBoolean("IsPublic");
			isWhiteList = nbtBank.getBoolean("IsWhiteList");
			if (nbtBank.hasKey("IsChanging", 1)) { isChanging = nbtBank.getBoolean("IsChanging"); }
			owner = nbtBank.getString("Owner");
			list = nbtBank.getTagList("BankNamesPlayersAccess", 8);
			for (int i = 0; i < list.tagCount(); i++) { access.add(list.getStringTagAt(i)); }
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

	public NBTTagCompound save() {
		NBTTagCompound nbtBank = new NBTTagCompound();
		nbtBank.setInteger("BankID", id);
		nbtBank.setString("Username", name);
		nbtBank.setBoolean("IsPublic", isPublic);
		nbtBank.setBoolean("IsWhiteList", isWhiteList);
		nbtBank.setBoolean("IsChanging", isChanging);
		nbtBank.setString("Owner", owner);
		if (name.isEmpty()) { name = "Default Bank"; }
		NBTTagList listCS = new NBTTagList();
		for (int ceil = 0; ceil < ceilSettings.size(); ++ceil) {
			NBTTagCompound nbtCeil = new NBTTagCompound();
			nbtCeil.setInteger("Ceil", ceil);
			ceilSettings.get(ceil).save(nbtCeil);
			listCS.appendTag(nbtCeil);
		}
		nbtBank.setTag("BankCells", listCS);
		NBTTagList listNPA = new NBTTagList();
		for (String n : access) { listNPA.appendTag(new NBTTagString(n)); }
		nbtBank.setTag("BankNamesPlayersAccess", listNPA);
		return nbtBank;
	}

}
