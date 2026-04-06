package noppes.npcs.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.WorldServer;
import noppes.npcs.CustomNpcs;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketSyncUpdate;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.controllers.data.TransportCategory;
import noppes.npcs.controllers.data.TransportLocation;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.RoleTransporter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TransportController {

	protected static TransportController instance;

	public static TransportController getInstance() {
		if (instance == null) { instance = new TransportController(); }
		return instance;
	}

	protected final Map<Integer, TransportLocation> locations = new TreeMap<>();
	protected final Map<Integer, TransportCategory> categories = new TreeMap<>();
	protected int lastUsedID = 0;


	public TransportController() {
		(instance = this).load();
        TransportCategory cat = new TransportCategory();
        cat.id = 1;
        cat.title = "Default";
        categories.put(cat.id, cat);
    }

	public boolean containsLocationName(String name) {
		name = name.toLowerCase();
		for (TransportLocation loc : locations.values()) {
			if (loc.name.equalsIgnoreCase(name)) { return true; }
		}
		return false;
	}

	public NBTTagCompound getNBT() {
		NBTTagList list = new NBTTagList();
		for (TransportCategory category : categories.values()) {
			NBTTagCompound compound = new NBTTagCompound();
			category.save(compound);
			list.appendTag(compound);
		}
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		nbttagcompound.setInteger("lastID", lastUsedID);
		nbttagcompound.setTag("NPCTransportCategories", list);

		return nbttagcompound;
	}

	public @Nullable TransportLocation getTransport(int transportId) { return locations.get(transportId); }

	public @Nullable TransportLocation getTransport(String name) {
		for (TransportLocation loc : new ArrayList<>(locations.values())) {
			if (loc.name.equals(name)) { return loc; }
		}
		return null;
	}

	private int getUniqueIdCategory() {
		int id = 0;
		for (int catId : categories.keySet()) {
			if (catId > id) {
				id = catId;
			}
		}
		return ++id;
	}

	private int getUniqueIdLocation() {
		if (lastUsedID == 0) {
			for (int catId : locations.keySet()) {
				if (catId > lastUsedID) {
					lastUsedID = catId;
				}
			}
		}
		return ++lastUsedID;
	}

	private void load() {
		File saveDir = CustomNpcs.getWorldSaveDirectory();
		if (saveDir == null) {
			CustomNpcs.debugData.end(null);
			return;
		}
		try {
			File file = new File(saveDir, "transport.dat");
			if (file.exists()) { load(CompressedStreamTools.readCompressed(Files.newInputStream(file.toPath()))); }
		} catch (IOException e) {
			try {
				File file = new File(saveDir, "transport.dat_old");
				if (file.exists()) { load(CompressedStreamTools.readCompressed(Files.newInputStream(file.toPath()))); }
			} catch (IOException ex) { LogWriter.error(e); }
		}
	}

	public void load(NBTTagCompound compound) {
		locations.clear();
		categories.clear();
		lastUsedID = compound.getInteger("lastID");
		NBTTagList list = compound.getTagList("NPCTransportCategories", 10);
		for (int i = 0; i < list.tagCount(); ++i) { loadCategory(list.getCompoundTagAt(i)); }
	}

	public void loadCategory(NBTTagCompound compound) {
		TransportCategory category = new TransportCategory();
		category.load(compound);
		for (TransportLocation location : category.locations.values()) { locations.put(location.id, location); }
		categories.put(category.id, category);
	}

	public void clear() {
		locations.clear();
		categories.clear();
	}

	public void removeCategory(int id) {
		if (categories.size() > 1) {
			TransportCategory cat = categories.get(id);
			if (cat != null) {
				for (int i : cat.locations.keySet()) { locations.remove(i); }
				categories.remove(id);
				saveCategories();
			}
		}
	}

	public TransportLocation removeLocation(int location) {
		TransportLocation loc = locations.get(location);
		if (loc != null) {
			loc.category.locations.remove(location);
			locations.remove(location);
			saveCategories();
		}
		return loc;
	}

	private void saveCategories() {
		CustomNpcs.debugData.start(null);
		try {
			File saveDir = CustomNpcs.getWorldSaveDirectory();
			File file = new File(saveDir, "transport.dat_new");
			File file1 = new File(saveDir, "transport.dat_old");
			File file2 = new File(saveDir, "transport.dat");
			CompressedStreamTools.writeCompressed(getNBT(), Files.newOutputStream(file.toPath()));
			if (file1.exists() && !file1.delete()) { LogWriter.debug("Error delete \"" + file1.getName() + "\" file"); }
			if (!file2.renameTo(file1) || (file2.exists() && !file2.delete())) { LogWriter.debug("Error delete or rename \"" + file2.getName() + "\" file"); }
			if (!file.renameTo(file2) || (file.exists() && !file.delete())) { LogWriter.debug("Error delete or rename \"" + file.getName() + "\" file"); }
		}
		catch (Exception e) { LogWriter.error(e); }
		CustomNpcs.debugData.end(null);
	}

	public @Nonnull TransportCategory getCategory(@Nullable TransportLocation forLocation, int categoryId) {
		if (categories.containsKey(categoryId)) { return categories.get(categoryId); }
		TransportCategory category = new TransportCategory();
		if (forLocation != null) { category.locations.put(forLocation.id, forLocation); }
		return category;
	}

	public void saveCategory(NBTTagCompound compound) {
		int id = compound.getInteger("CategoryId");
		if (id < 0) { id = getUniqueIdCategory(); }
		if (categories.containsKey(id)) {
			categories.get(id).load(compound);
			if (CustomNpcs.Server != null) {
				for (int locID : categories.get(id).locations.keySet()) {
					TransportLocation loc = categories.get(id).locations.get(locID);
					if (loc.npc != null) {
						WorldServer world = CustomNpcs.Server.getWorld(loc.dimension);
						if (world != null) {
							Entity entity = world.getEntityFromUuid(loc.npc);
							if (entity instanceof EntityNPCInterface
									&& ((EntityNPCInterface) entity).role instanceof RoleTransporter
									&& ((RoleTransporter) ((EntityNPCInterface) entity).role).transportId == locID
									&& !((RoleTransporter) ((EntityNPCInterface) entity).role).name
									.equals(loc.name)) {
								((RoleTransporter) ((EntityNPCInterface) entity).role).name = loc.name;
							}
						}
					}
				}
			}
		}
		else {
			TransportCategory category = new TransportCategory();
			category.load(compound);
			category.id = id;
			categories.put(id, category);
		}
		saveCategories();
	}

	public TransportLocation saveLocation(int categoryId, NBTTagCompound compound, EntityNPCInterface npc) {
		TransportCategory category = categories.get(categoryId);
		if (category == null || !(npc.role instanceof RoleTransporter)) {
			return null;
		}
		RoleTransporter role = (RoleTransporter) npc.role;
		TransportLocation location = new TransportLocation();
		location.load(compound);
		location.category = category;
		if (role.hasTransport()) {
			location.id = role.transportId;
		}
		if (location.id < 0 || !locations.get(location.id).name.equals(location.name)) {
			while (containsLocationName(location.name)) {
                location.name = location.name + "_";
			}
		}
		if (location.id < 0) {
			location.id = getUniqueIdLocation();
		}
		category.locations.put(location.id, location);
		locations.put(location.id, location);
		saveCategories();
		return location;
	}

	public void setLocation(TransportLocation location) {
		if (locations.containsKey(location.id)) {
			for (TransportCategory cat : categories.values()) {
				cat.locations.remove(location.id);
			}
		}
		locations.put(location.id, location);
		location.category.locations.put(location.id, location);
	}

	public void sendTo(@Nonnull EntityPlayerMP player) {
		if (categories.isEmpty()) {
			TransportCategory cat = new TransportCategory();
			cat.id = 1;
			cat.title = "Default";
			categories.put(cat.id, cat);
		}
		List<TransportCategory> list = getCategories();
		Packets.send(player, new PacketSyncUpdate(-1, 14, getNBT()));
		for (TransportCategory cat : list) {
			NBTTagCompound compound = new NBTTagCompound();
			cat.save(compound);
			Packets.send(player, new PacketSyncUpdate(cat.id, 14, compound));
		}
	}

	public List<TransportCategory> getCategories() { return new ArrayList<>(categories.values()); }

}
