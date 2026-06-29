package noppes.npcs.controllers;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.gui.IDimensionGetter;
import noppes.npcs.controllers.data.DimensionData;
import noppes.npcs.shared.common.util.LogWriter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DimensionController {

    private static boolean isLoad = false;
    private static final Map<String, DimensionData> data = new LinkedHashMap<>();

    public static void load() {
        if (isLoad) { return; }
        File dir = CustomNpcs.getLevelSaveDirectory();
        if (dir != null) {
            File file = new File(dir, "dimensions.dat");
            if (file.exists()) {
                try { load(NbtIo.readCompressed(file)); } catch (Exception e) { LogWriter.error(e); }
            }
            isLoad = true;
        }
    }

    public static void load(CompoundTag compound) {
        if (compound == null) { return; }
        data.clear();
        for (int i = 0; i < compound.getList("Data", 10).size(); i++) {
            CompoundTag nbt = compound.getList("Data", 10).getCompound(i);
            data.put(nbt.getString("id"), new DimensionData(nbt));
        }
        if (Minecraft.getInstance().screen instanceof IDimensionGetter gui) { gui.resetDimension(); }
    }

    public static void save() {
        File file = new File(CustomNpcs.getLevelSaveDirectory(), "dimensions.dat");
        CompoundTag compound = new CompoundTag();
        ListTag list = new ListTag();
        for (String location : data.keySet()) {
            CompoundTag nbt = data.get(location).save();
            nbt.putString("name", location);
            list.add(nbt);
        }
        compound.put("Data", list);
        try { NbtIo.writeCompressed(compound, file); } catch (Exception e) { LogWriter.error(e); }
    }

    public static List<String> getLineKeys() {
        return new ArrayList<>(data.keySet());
    }

    public static boolean has(ResourceLocation location) {
        if (location == null) { return false; }
        for (String line : data.keySet()) {
            if (line.equals(location.toString())) { return true; }
        }
        return false;
    }

    public static void setSpawn(Level level, BlockPos pos, float angle) {
        if (level == null || pos == null) { return; }
        String key = level.dimension().location().toString();
        if (!data.containsKey(key)) { data.put(key, new DimensionData(level)); }
        data.get(key).spawnPos = pos;
        data.get(key).spawnAngle = angle;
        save();
    }

    public static @Nonnull DimensionData get(ServerLevel level) {
        if (level == null) { return new DimensionData(); }
        String key = level.dimension().location().toString();
        if (data.containsKey(key)) { return data.get(key); }
        data.put(key, new DimensionData(level));
        return data.get(key);
    }

    public static @Nullable DimensionData get(String dimensionId) {
        if (data.containsKey(dimensionId)) { return data.get(dimensionId); }
        return null;
    }

}
