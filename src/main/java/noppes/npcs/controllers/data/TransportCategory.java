package noppes.npcs.controllers.data;

import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class TransportCategory {

	public Map<Integer, TransportLocation> locations = new TreeMap<>();
	public String title = "";
	public int id = -1;

	public Vector<TransportLocation> getDefaultLocations() {
		Vector<TransportLocation> list = new Vector<>();
		for (TransportLocation loc : locations.values()) {
			if (loc.isDefault()) {
				list.add(loc);
			}
		}
		return list;
	}

	public void load(NBTTagCompound compound) {
		id = compound.getInteger("CategoryId");
		title = compound.getString("CategoryTitle");
		if (title.isEmpty()) {
			title = "Default";
		}
		NBTTagList locs = compound.getTagList("CategoryLocations", 10);
		if (locs.tagCount() == 0) {
			return;
		}
		for (int ii = 0; ii < locs.tagCount(); ++ii) {
			TransportLocation location = new TransportLocation();
			location.load(locs.getCompoundTagAt(ii));
			location.category = this;
			locations.put(location.id, location);
		}
	}

	public void save(NBTTagCompound compound) {
		compound.setInteger("CategoryId", id);
		compound.setString("CategoryTitle", title);
		NBTTagList locs = new NBTTagList();
		for (TransportLocation location : locations.values()) {
			locs.appendTag(location.save());
		}
		compound.setTag("CategoryLocations", locs);
	}
}
