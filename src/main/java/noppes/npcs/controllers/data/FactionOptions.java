package noppes.npcs.controllers.data;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.Component;
import noppes.npcs.controllers.FactionController;

public class FactionOptions {

	public final List<FactionOption> factionOptions = new ArrayList<>();

	public void load(NBTTagCompound compound) {
		factionOptions.clear();
		if (!compound.hasKey("FactionOptions", 9)) { // OLD
			if (compound.getInteger("OptionFactions1") > 0) {
				factionOptions.add(new FactionOption(compound.getInteger("OptionFactions1"),
						compound.getInteger("OptionFaction1Points"), compound.getBoolean("DecreaseFaction1Points")));
			}
			if (compound.getInteger("OptionFactions2") > 0) {
				factionOptions.add(new FactionOption(compound.getInteger("OptionFactions2"),
						compound.getInteger("OptionFaction2Points"), compound.getBoolean("DecreaseFaction2Points")));
			}
		} else {
			for (int i = 0; i < compound.getTagList("FactionOptions", 10).tagCount(); i++) {
				factionOptions.add(new FactionOption(compound.getTagList("FactionOptions", 10).getCompoundTagAt(i)));
			}
		}
	}

	public NBTTagCompound save(NBTTagCompound compound) {
		NBTTagList list = new NBTTagList();
		for (FactionOption fo : factionOptions) { list.appendTag(fo.save()); }
		compound.setTag("FactionOptions", list);
		return compound;
	}

	public boolean hasFaction(int id) {
		for (FactionOption fo : factionOptions) {
			if (fo.factionId == id) { return true; }
		}
		return false;
	}

	public void addPoints(EntityPlayer player) {
		PlayerFactionData data = PlayerData.get(player).factionData;
		for (FactionOption fo : factionOptions) {
			if (fo.factionId < 0 || fo.factionPoints == 0) { continue; }
			int value = fo.factionPoints;
			boolean take = fo.decreaseFactionPoints;
			if (value < 0) {
				value *= -1;
                take = !take;
			}
			addPoints(player, data, fo.factionId, take, value);
		}
    }

	private void addPoints(EntityPlayer player, PlayerFactionData data, int factionId, boolean decrease, int points) {
		Faction faction = FactionController.instance.getFaction(factionId);
		if (faction != null) {
			if (!faction.hideFaction) {
				String message = decrease ? "faction.decreasepoints" : "faction.increasepoints";
				player.sendMessage(Component.translatable(message, faction.name, points).getParent());
			}
			data.increasePoints(player, factionId, decrease ? (-points) : points);
			PlayerData.get(player).updateClient = true;
		}
	}

	// New from Unofficial (BetaZavr)
	public FactionOptions copy() {
		FactionOptions fp = new FactionOptions();
		fp.load(save(new NBTTagCompound()));
		return fp;
	}

	public FactionOption get(int factionID) {
		for (FactionOption fo : factionOptions) {
			if (fo.factionId == factionID) { return fo; }
		}
		return null;
	}

	public boolean hasOptions() {
		for (FactionOption fo : factionOptions) {
			if (fo.factionId > 0 && fo.factionPoints != 0) { return true; }
		}
		return false;
	}

	public boolean remove(int factionID) {
		for (FactionOption fo : factionOptions) {
			if (fo.factionId == factionID) {
				factionOptions.remove(fo);
				return true;
			}
		}
		return false;
	}

	public List<Integer> getIDs() {
		List<Integer> list = new ArrayList<>();
		for (FactionOption fo : factionOptions) { if (!list.contains(fo.factionId)) { list.add(fo.factionId); } }
		return list;
	}

}
