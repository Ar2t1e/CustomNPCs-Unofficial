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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import noppes.npcs.CustomNpcs;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiUpdate;
import noppes.npcs.packets.client.SPacketBorderClear;
import noppes.npcs.packets.client.SPacketBorderData;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.api.IPos;
import noppes.npcs.api.handler.IBorderHandler;
import noppes.npcs.controllers.data.Zone3D;

public class BorderController implements IBorderHandler {

	protected static BorderController instance;
	public final TreeMap<Integer, Zone3D> regions = new TreeMap<>();

	public static BorderController getInstance() {
		if (instance == null) { instance = new BorderController(); }
		return instance;
	}

	public BorderController() { loadRegions(); }

	public Zone3D createNew(int dimensionID, BlockPos pos) {
		Zone3D reg = new Zone3D(getUnusedId(), dimensionID, pos.getX(), pos.getY(), pos.getZ());
		regions.put(reg.getId(), reg);
		return reg;
	}

	@Override
	public Zone3D createNew(int dimensionId, IPos pos) { return createNew(dimensionId, pos.getMCBlockPos()); }

	@Override
	public Zone3D[] getAllRegions() { return regions.values().toArray(new Zone3D[0]); }

	public NBTTagCompound getNBT() {
		NBTTagList list = new NBTTagList();
		for (Zone3D region : regions.values()) {
			NBTTagCompound nbtRegion = new NBTTagCompound();
			region.save(nbtRegion);
			list.appendTag(nbtRegion);
		}
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		nbttagcompound.setTag("Data", list);
		return nbttagcompound;
	}

	public int getRegionID(int dimensionID, Entity entity) {
		for (Zone3D reg : regions.values()) {
			if (reg.dimension == dimensionID && reg.contains(entity.posX, entity.posY, entity.posZ, entity.height)) { return reg.getId(); }
		}
		return -1;
	}

	public int getRegionID(int dimensionID, BlockPos pos) {
		for (Zone3D reg : regions.values()) {
			if (reg.dimension == dimensionID && reg.contains(pos.getX(), pos.getY(), pos.getZ(), 1.0d)) {
				return reg.getId();
			}
		}
		return -1;
	}

	@Override
	public Zone3D getRegion(int regionId) { return regions.get(regionId); }

	@Override
	public Zone3D[] getRegions(int dimensionId) {
		List<Zone3D> regs = new ArrayList<>();
		for (Zone3D reg : regions.values()) {
			if (reg.dimension == dimensionId) { regs.add(reg); }
		}
		return regs.toArray(new Zone3D[0]);
	}

	public List<Zone3D> getRegionsInWorld(int dimensionID) {
		List<Zone3D> regs = new ArrayList<>();
		for (Zone3D reg : regions.values()) {
			if (reg.dimension == dimensionID) { regs.add(reg); }
		}
		return regs;
	}

	@Override
	public List<Zone3D> getNearestRegions(int dimensionName, double x, double y, double z, double distance) {
		AxisAlignedBB searchBox = new AxisAlignedBB(x - distance, 0.0d, z - distance, x + distance + 1.0d, 255.0d, z + distance + 1.0d);
		List<Zone3D> regionsIn = new ArrayList<>();
		for (Zone3D reg : regions.values()) {
			if (reg.dimension != dimensionName) { continue; }
			if (searchBox.intersects(reg.getAxisAlignedBB())) { regionsIn.add(reg); }
		}
		return regionsIn;
	}

	public int getUnusedId() {
		int id = 0;
		while (regions.containsKey(id)) { id++; }
		return id;
	}

	public Zone3D loadRegion(NBTTagCompound nbtRegion) {
		if (nbtRegion != null && nbtRegion.hasKey("ID", 3) && nbtRegion.getInteger("ID") >= 0) {
			int id = nbtRegion.getInteger("ID");
			if (regions.containsKey(id)) {
				regions.get(id).load(nbtRegion);
				return regions.get(id);
			}
			Zone3D region = new Zone3D();
			region.load(nbtRegion);
			regions.put(region.getId(), region);
			return regions.get(region.getId());
		}
		return null;
	}

	private void loadRegions() {
		CustomNpcs.debugData.start(null);
		File saveDir = CustomNpcs.getWorldSaveDirectory();
		if (saveDir == null) {
			CustomNpcs.debugData.end(null);
			return;
		}
		try {
			File file = new File(saveDir, "borders.dat");
			if (file.exists()) {
				loadRegions(file);
			}
		}
		catch (Exception e) {
			try {
				File file2 = new File(saveDir, "borders.dat_old");
				if (file2.exists()) {
					loadRegions(file2);
				}
			} catch (Exception ex) { LogWriter.error(ex); }
		}
		CustomNpcs.debugData.end(null);
	}

	private void loadRegions(File file) throws IOException {
		loadRegions(CompressedStreamTools.readCompressed(Files.newInputStream(file.toPath())));
	}

	public void loadRegions(NBTTagCompound compound) {
		regions.clear();
		if (compound.hasKey("Data", 9)) {
			for (int i = 0; i < compound.getTagList("Data", 10).tagCount(); ++i) {
				loadRegion(compound.getTagList("Data", 10).getCompoundTagAt(i));
			}
		}
	}

	@Override
	public boolean removeRegion(int region) {
		if (region < 0 || regions.isEmpty()) { return false; }
		regions.remove(region);
		save();
		return true;
	}

	public void save() {
		CustomNpcs.debugData.start(null);
		try {
			File saveDir = CustomNpcs.getWorldSaveDirectory();
			File file = new File(saveDir, "borders.dat_new");
			File file1 = new File(saveDir, "borders.dat_old");
			File file2 = new File(saveDir, "borders.dat");
			CompressedStreamTools.writeCompressed(getNBT(), Files.newOutputStream(file.toPath()));
			if (file1.exists() && !file1.delete()) { LogWriter.debug("Error delete \"" + file1.getName() + "\" file"); }
			if (!file2.renameTo(file1) || (file2.exists() && !file2.delete())) { LogWriter.debug("Error delete or rename \"" + file2.getName() + "\" file"); }
			if (!file.renameTo(file2) || (file.exists() && !file.delete())) { LogWriter.debug("Error delete or rename \"" + file.getName() + "\" file"); }
		}
		catch (Exception e) { LogWriter.error(e); }
		CustomNpcs.debugData.end(null);
	}

	public void sendTo(EntityPlayerMP player) {
		Packets.send(player, new SPacketBorderClear());
		for (int id : regions.keySet()) {
			if (id < 0 || regions.get(id).getId() < 0) {
				continue;
			}
			NBTTagCompound nbtRegion = new NBTTagCompound();
			regions.get(id).save(nbtRegion);
			Packets.send(player, new SPacketBorderData(nbtRegion));
		}
		Packets.send(player, new PacketGuiUpdate());
	}

	public void update() {
		if (CustomNpcs.Server == null || CustomNpcs.Server.getPlayerList().getOnlinePlayerNames().length == 0 || regions.isEmpty()) { return; }
		for (Zone3D reg : regions.values()) {
			for (WorldServer w : CustomNpcs.Server.worlds) { reg.update(w); }
		}
	}

	public void update(int id) {
		if (CustomNpcs.Server == null || CustomNpcs.Server.getPlayerList().getOnlinePlayerNames().length == 0) {
			return;
		}
		if (id < 0) {
			for (int i : regions.keySet()) {
				NBTTagCompound nbtRegion = new NBTTagCompound();
				regions.get(i).save(nbtRegion);
				for (EntityPlayerMP player : CustomNpcs.Server.getPlayerList().getPlayers()) {
					Packets.send(player, new SPacketBorderData(nbtRegion));
					Packets.send(player, new PacketGuiUpdate());
				}
			}
		}
		else if (regions.containsKey(id)) {
			NBTTagCompound nbtRegion = new NBTTagCompound();
			regions.get(id).save(nbtRegion);
			for (EntityPlayerMP player : CustomNpcs.Server.getPlayerList().getPlayers()) {
				Packets.send(player, new SPacketBorderData(nbtRegion));
				Packets.send(player, new PacketGuiUpdate());
			}
		}
	}

}
