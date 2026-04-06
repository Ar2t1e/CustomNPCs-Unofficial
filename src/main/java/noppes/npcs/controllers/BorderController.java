package noppes.npcs.controllers;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.IPos;
import noppes.npcs.api.handler.IBorderHandler;
import noppes.npcs.controllers.data.Zone3D;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.SPacketBorderClear;
import noppes.npcs.packets.client.SPacketBorderData;
import noppes.npcs.packets.client.PacketGuiUpdate;
import noppes.npcs.shared.common.util.LogWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

public class BorderController implements IBorderHandler {

    protected static BorderController instance;
    public final TreeMap<Integer, Zone3D> regions = new TreeMap<>();

    public static BorderController getInstance() {
        if (instance == null) { instance = new BorderController(); }
        return instance;
    }

    public BorderController() { loadRegions(); }

    public Zone3D createNew(String dimensionID, BlockPos pos) {
        Zone3D reg = new Zone3D(getUnusedId(), dimensionID, pos.getX(), pos.getY(), pos.getZ());
        regions.put(reg.getId(), reg);
        return reg;
    }

    @Override
    public Zone3D createNew(String dimensionId, IPos pos) {
        return createNew(dimensionId, pos.getMCBlockPos());
    }

    @Override
    public Zone3D[] getAllRegions() {
        return regions.values().toArray(new Zone3D[0]);
    }

    public CompoundTag getNBT() {
        ListTag list = new ListTag();
        for (Zone3D region : regions.values()) {
            CompoundTag nbtRegion = new CompoundTag();
            region.save(nbtRegion);
            list.add(nbtRegion);
        }
        CompoundTag compound = new CompoundTag();
        compound.put("Data", list);
        return compound;
    }

    public int getRegionID(String dimensionID, Entity entity) {
        for (Zone3D reg : regions.values()) {
            if (reg.dimension.toString().equals(dimensionID) && reg.contains(entity.getX(), entity.getY(), entity.getZ(), entity.getBbHeight())) {
                return reg.getId();
            }
        }
        return -1;
    }

    public int getRegionID(String dimensionID, BlockPos pos) {
        for (Zone3D reg : regions.values()) {
            if (reg.dimension.toString().equals(dimensionID) && reg.contains(pos.getX(), pos.getY(), pos.getZ(), 1.0d)) {
                return reg.getId();
            }
        }
        return -1;
    }

    @Override
    public Zone3D getRegion(int regionId) {
        return regions.get(regionId);
    }

    @Override
    public Zone3D[] getRegions(String dimensionId) {
        List<Zone3D> regs = new ArrayList<>();
        for (Zone3D reg : regions.values()) {
            if (reg.dimension.toString().equals(dimensionId)) { regs.add(reg); }
        }
        return regs.toArray(new Zone3D[0]);
    }

    public List<Zone3D> getRegionsInWorld(ResourceLocation location) {
        List<Zone3D> regs = new ArrayList<>();
        for (Zone3D reg : regions.values()) {
            if (reg.dimension.equals(location)) { regs.add(reg); }
        }
        return regs;
    }

    @Override
    public List<Zone3D> getNearestRegions(String dimensionId, double x, double y, double z, double distance) {
        AABB searchBox = new AABB(x - distance, 0.0d, z - distance, x + distance + 1.0d, 255.0d, z + distance + 1.0d);
        List<Zone3D> regionsIn = new ArrayList<>();
        for (Zone3D reg : regions.values()) {
            if (!reg.dimension.toString().equals(dimensionId)) { continue; }
            if (searchBox.intersects(reg.getAxisAlignedBB())) { regionsIn.add(reg); }
        }
        return regionsIn;
    }

    public int getUnusedId() {
        int id = 0;
        while (regions.containsKey(id)) { id++; }
        return id;
    }

    public Zone3D loadRegion(CompoundTag nbtRegion) {
        if (nbtRegion == null || !nbtRegion.contains("ID", 3) || nbtRegion.getInt("ID") < 0) {
            return null;
        }
        int id = nbtRegion.getInt("ID");
        if (regions.containsKey(id)) {
            regions.get(id).load(nbtRegion);
            regions.get(id);
            return regions.get(id);
        }
        Zone3D region = new Zone3D();
        region.load(nbtRegion);
        regions.put(region.getId(), region);
        return regions.get(region.getId());
    }

    private void loadRegions() {
        CustomNpcs.debugData.start("Mod");
        File saveDir = CustomNpcs.getLevelSaveDirectory();
        if (saveDir == null) {
            return;
        }
        try {
            File file = new File(saveDir, "borders.dat");
            if (file.exists()) {
                loadRegions(file);
            }
        } catch (Exception e) {
            try {
                File file2 = new File(saveDir, "borders.dat_old");
                if (file2.exists()) { loadRegions(file2); }
            } catch (Exception ex) { LogWriter.error("Error:", ex); }
        }
        CustomNpcs.debugData.end("Mod");
    }

    private void loadRegions(File file) throws IOException {
        DataInputStream stream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new FileInputStream(file))));
        loadRegions(NbtIo.read(stream));
        stream.close();
    }

    public void loadRegions(CompoundTag compound) {
        regions.clear();
        if (compound.contains("Data", 9)) {
            for (int i = 0; i < compound.getList("Data", 10).size(); ++i) {
                loadRegion(compound.getList("Data", 10).getCompound(i));
            }
        }
    }

    @Override
    public boolean removeRegion(int region) {
        if (region < 0 || regions.isEmpty()) {
            return false;
        }
        regions.remove(region);
        save();
        return true;
    }

    public void save() {
        try {
            File saveDir = CustomNpcs.getLevelSaveDirectory();
            File file = new File(saveDir, "borders.dat_new");
            File file1 = new File(saveDir, "borders.dat_old");
            File file2 = new File(saveDir, "borders.dat");
            NbtIo.writeCompressed(getNBT(), new FileOutputStream(file));
            if (file1.exists() && !file1.delete()) { LogWriter.debug("Error delete \"" + file1.getName() + "\" file"); }
            if (!file2.renameTo(file1) || (file2.exists() && !file2.delete())) { LogWriter.debug("Error delete or rename \"" + file2.getName() + "\" file"); }
            if (!file.renameTo(file2) || (file.exists() && !file.delete())) { LogWriter.debug("Error delete or rename \"" + file.getName() + "\" file"); }
        } catch (Exception e) { LogWriter.error("Error:", e); }
    }

    public void sendTo(ServerPlayer player) {
        Packets.send(player, new SPacketBorderClear());
        for (int id : regions.keySet()) {
            if (id < 0 || regions.get(id).getId() < 0) { continue; }
            CompoundTag nbtRegion = new CompoundTag();
            regions.get(id).save(nbtRegion);
            Packets.send(player, new SPacketBorderData(nbtRegion));
        }
        Packets.send(player, new PacketGuiUpdate());
    }

    public void update() {
        if (CustomNpcs.Server == null || CustomNpcs.Server.getPlayerList().getPlayerCount() == 0 || regions.isEmpty()) { return; }
        for (Zone3D reg : regions.values()) {
            for (ServerLevel level : CustomNpcs.Server.getAllLevels()) { reg.update(level); }
        }
    }

    public void update(int id) {
        if (CustomNpcs.Server == null || CustomNpcs.Server.getPlayerList().getPlayers().isEmpty()) { return; }
        if (id < 0) {
            for (int i : regions.keySet()) {
                CompoundTag nbtRegion = new CompoundTag();
                regions.get(i).save(nbtRegion);
                for (ServerPlayer player : CustomNpcs.Server.getPlayerList().getPlayers()) {
                    Packets.send(player, new SPacketBorderData(nbtRegion));
                    Packets.send(player, new PacketGuiUpdate());
                }
            }
        } else if (regions.containsKey(id)) {
            CompoundTag nbtRegion = new CompoundTag();
            regions.get(id).save(nbtRegion);
            for (ServerPlayer player : CustomNpcs.Server.getPlayerList().getPlayers()) {
                Packets.send(player, new SPacketBorderData(nbtRegion));
                Packets.send(player, new PacketGuiUpdate());
            }
        }
    }

}