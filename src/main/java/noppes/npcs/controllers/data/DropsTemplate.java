package noppes.npcs.controllers.data;

import java.util.*;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.entity.data.DropSet;

public class DropsTemplate {

	public final Map<String, Map<Integer, DropSet>> groups = new TreeMap<>(); // <id, <pos, drop>>
	private boolean allDropsFromGroup = false; // or random
	private final Random rnd = new Random();

	public DropsTemplate() { groups.put("default", new TreeMap<>()); }

	public DropsTemplate(NBTTagCompound nbtTemplate) {
		this();
		load(nbtTemplate);
	}

	public DropSet addDropItem(String id, ItemStack item, double chance) {
		if (id == null ||id.isEmpty()) { id = "Group #" + groups.size(); }
		if (!groups.containsKey(id)) { groups.put(id, new TreeMap<>()); }
		DropSet ds = new DropSet(null);
		ds.item = item;
		ds.setChance(chance);
		ds.pos = groups.get(id).size();
		groups.get(id).put(ds.pos, ds);
		return ds;
	}

	public List<DropSet> getDrops() {
		List<DropSet> allDrops = new ArrayList<>();
		for (String groupId : groups.keySet()) {
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
		for (String id : groups.keySet()) {
			NBTTagList list = new NBTTagList();
			for (DropSet ds : groups.get(id).values()) { list.appendTag(ds.save()); }
			nbtTemplate.setTag(id, list);
		}
		return nbtTemplate;
	}

	public void load(NBTTagCompound nbtTemplate) {
		if (nbtTemplate.hasKey("DropType", 3)) { allDropsFromGroup = nbtTemplate.getInteger("DropType") == 3; }
		else if (nbtTemplate.hasKey("DropType", 1)) { allDropsFromGroup = nbtTemplate.getBoolean("DropType"); }
		nbtTemplate.removeTag("DropType");
		groups.clear();
		Set<String> keys = nbtTemplate.getKeySet();
		for (String id : keys) {
			if (!id.isEmpty()) {
				if (nbtTemplate.getTagList(id, 10).hasNoTags()) { groups.put(id, new TreeMap<>()); }
				else {
					for (int j = 0; j < nbtTemplate.getTagList(id, 10).tagCount(); j++) {
						DropSet ds = new DropSet(null);
						ds.load(nbtTemplate.getTagList(id, 10).getCompoundTagAt(j));
						ds.pos = j;
						if (!groups.containsKey(id)) { groups.put(id, new TreeMap<>()); }
						groups.get(id).put(ds.pos, ds);
					}
				}
			}
		}
	}

	public void removeDrop(String groupId, int slot) {
		if (groups.containsKey(groupId) && groups.get(groupId).containsKey(slot)) {
			groups.get(groupId).remove(slot);
			int j = 0;
			for (int s : groups.get(groupId).keySet()) {
				groups.get(groupId).get(s).pos = j++;
			}
		}
	}

	public void removeGroup(String groupId) { groups.remove(groupId); }
	
	public static DropsTemplate from(DropsTemplate dropTemplate) {
		DropsTemplate dt = new DropsTemplate();
		if (dropTemplate != null) { dt.load(dropTemplate.getNBT()); }
		return dt;
	}

}
