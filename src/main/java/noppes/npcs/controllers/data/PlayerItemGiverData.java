package noppes.npcs.controllers.data;

import java.util.HashMap;

import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.NBTTags;
import noppes.npcs.api.handler.data.IPlayerData;
import noppes.npcs.roles.JobItemGiver;

public class PlayerItemGiverData implements IPlayerData {

	protected final HashMap<Integer, Integer> chained;
	protected final HashMap<Integer, Long> itemGivers;

	public PlayerItemGiverData() {
		itemGivers = new HashMap<>();
		chained = new HashMap<>();
	}

	@Override
	public void load(NBTTagCompound compound) {
		chained.clear();
		itemGivers.clear();
		chained.putAll(NBTTags.getIntegerIntegerMap(compound.getTagList("ItemGiverChained", 10)));
		itemGivers.putAll(NBTTags.getIntegerLongMap(compound.getTagList("ItemGiversList", 10)));
	}

	@Override
	public NBTTagCompound save(NBTTagCompound compound) {
		compound.setTag("ItemGiverChained", NBTTags.nbtIntegerIntegerMap(chained));
		compound.setTag("ItemGiversList", NBTTags.nbtIntegerLongMap(itemGivers));
		return compound;
	}

	public boolean notInteractedBefore(JobItemGiver jobItemGiver) { return !itemGivers.containsKey(jobItemGiver.itemGiverId); }

	public long getTime(JobItemGiver jobItemGiver) { return itemGivers.get(jobItemGiver.itemGiverId); }

	public void setTime(JobItemGiver jobItemGiver, long day) { itemGivers.put(jobItemGiver.itemGiverId, day); }

	public int getItemIndex(JobItemGiver jobItemGiver) { return chained.getOrDefault(jobItemGiver.itemGiverId, 0); }

	public void setItemIndex(JobItemGiver jobItemGiver, int i) { chained.put(jobItemGiver.itemGiverId, i); }

	public void clear() {
		itemGivers.clear();
		chained.clear();
	}

}
