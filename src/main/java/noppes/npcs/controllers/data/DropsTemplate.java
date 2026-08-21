package noppes.npcs.controllers.data;

import java.util.*;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.entity.data.DropSet;

public class DropsTemplate {

	public final Map<Integer, Map<Integer, DropSet>> groups = new TreeMap<>(); // <id, <pos, drop>>
	private boolean allDropsFromGroup = false; // or random
	private final Random rnd = new Random();

	public DropsTemplate() { groups.put(0, new TreeMap<>()); }

	public DropsTemplate(NBTTagCompound nbtTemplate) {
		this();
		load(nbtTemplate);
	}

	public DropSet addDropItem(int id, ItemStack item, double chance) {
		if (!groups.containsKey(id)) {
			id = groups.size();
			groups.put(id, new TreeMap<>());
		}
		DropSet ds = new DropSet(null);
		ds.item = item;
		ds.setChance(chance);
		ds.pos = groups.get(id).size();
		groups.get(id).put(ds.pos, ds);
		return ds;
	}

	public List<DropSet> getDrops() {
		List<DropSet> allDrops = new ArrayList<>();
		for (int groupId : groups.keySet()) {
			ArrayList<DropSet> preList = new ArrayList<>(groups.get(groupId).values());
			if (preList.isEmpty()) { continue; }
			if (allDropsFromGroup) { allDrops.addAll(preList); }
			else { allDrops.add(preList.get(rnd.nextInt(preList.size()))); }
		}
		return allDrops;
	}

	public NBTTagCompound getNBT() {
		NBTTagCompound nbtTemplate = new NBTTagCompound();
		nbtTemplate.setBoolean("DropType", allDropsFromGroup);
		for (int id : groups.keySet()) {
			NBTTagList list = new NBTTagList();
			for (DropSet ds : groups.get(id).values()) { list.appendTag(ds.save()); }
			nbtTemplate.setTag("Group_" + id, list);
		}
		return nbtTemplate;
	}

	public void load(NBTTagCompound nbtTemplate) {
		if (nbtTemplate.hasKey("DropType", 3)) { allDropsFromGroup = nbtTemplate.getInteger("DropType") == 3; }
		else if (nbtTemplate.hasKey("DropType", 1)) { allDropsFromGroup = nbtTemplate.getBoolean("DropType"); }
		groups.clear();
		Set<String> keys = nbtTemplate.getKeySet();
		for (String groupId : keys) {
			if (groupId.indexOf("Group_") != 0) { continue; }
			int id = -1;
			try { id = Integer.parseInt(groupId.replace("Group_", "")); }
			catch (Exception e) { LogWriter.error(e); }
			if (id < 0) { continue; }
			for (int j = 0; j < nbtTemplate.getTagList(groupId, 10).tagCount(); j++) {
				DropSet ds = new DropSet(null);
				ds.load(nbtTemplate.getTagList(groupId, 10).getCompoundTagAt(j));
				ds.pos = j;
				if (!groups.containsKey(id)) { groups.put(id, new TreeMap<>()); }
				groups.get(id).put(ds.pos, ds);
			}
		}
	}

	public void removeDrop(int groupId, int slot) {
		if (!groups.containsKey(groupId) || !groups.get(groupId).containsKey(slot)) { return; }
		if (groups.get(groupId).remove(slot) != null) {
			int j = 0;
			for (int s : groups.get(groupId).keySet()) {
				groups.get(groupId).get(s).pos = j++;
			}
		}
	}

	public void removeGroup(int groupId) {
		if (!groups.containsKey(groupId)) { return; }
		groups.remove(groupId);
		Map<Integer, Map<Integer, DropSet>> newGroups = new TreeMap<>();
		int j = 0;
		for (int gId : groups.keySet()) {
			if (gId == groupId) { continue; }
			newGroups.put(j, groups.get(gId));
			j++;
		}
		groups.clear();
		groups.putAll(newGroups);
	}
	
	public static DropsTemplate from(DropsTemplate dropTemplate) {
		DropsTemplate dt = new DropsTemplate();
		if (dropTemplate != null) { dt.load(dropTemplate.getNBT()); }
		return dt;
	}

}
