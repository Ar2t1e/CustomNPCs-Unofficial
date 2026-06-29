package noppes.npcs.controllers;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.gui.IDimensionGetter;
import noppes.npcs.controllers.data.DimensionData;
import noppes.npcs.shared.common.util.LogWriter;

import javax.annotation.Nullable;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DimensionController {

    private static boolean isLoad = false;
    private static final Map<Integer, DimensionData> data = new LinkedHashMap<>();

    public static void load() {
        if (isLoad) { return; }
        File dir = CustomNpcs.getWorldSaveDirectory();
        if (dir != null) {
            File file = new File(dir, "dimensions.dat");
            if (file.exists()) {
                try { load(CompressedStreamTools.readCompressed(Files.newInputStream(file.toPath()))); } catch (Exception e) { LogWriter.error(e); }
            }
            isLoad = true;
        }
    }

    public static void load(NBTTagCompound compound) {
        if (compound == null) { return; }
        data.clear();
        for (int i = 0; i < compound.getTagList("Data", 10).tagCount(); i++) {
            NBTTagCompound nbt = compound.getTagList("Data", 10).getCompoundTagAt(i);
            data.put(nbt.getInteger("id"), new DimensionData(nbt));
        }
        if (Minecraft.getMinecraft().currentScreen instanceof IDimensionGetter) {
            ((IDimensionGetter) Minecraft.getMinecraft().currentScreen).resetDimension();
        }
    }

    public static void save() {
        File dir = CustomNpcs.getWorldSaveDirectory();
        if (dir != null) {
            File file = new File(CustomNpcs.getWorldSaveDirectory(), "dimensions.dat");
            NBTTagCompound compound = new NBTTagCompound();
            NBTTagList list = new NBTTagList();
            for (int location : data.keySet()) {
                NBTTagCompound nbt = data.get(location).save();
                nbt.setInteger("name", location);
                list.appendTag(nbt);
            }
            compound.setTag("Data", list);
            try { CompressedStreamTools.writeCompressed(compound, Files.newOutputStream(file.toPath())); } catch (Exception e) { LogWriter.error(e); }
        }
    }

    @SuppressWarnings("unused")
    public static List<Integer> getLineKeys() { return new ArrayList<>(data.keySet()); }

    public static boolean has(int id) { return data.containsKey(id); }

    public static void setSpawn(WorldServer world, BlockPos pos, float angle) {
        if (world == null || pos == null) { return; }
        int key = world.provider.getDimension();
        if (!data.containsKey(key)) { data.put(key, new DimensionData(world)); }
        data.get(key).spawnPos = pos;
        data.get(key).spawnAngle = angle;
        save();
    }

    public static DimensionData get(WorldServer world) {
        if (world == null) { return new DimensionData(); }
        int key = world.provider.getDimension();
        if (data.containsKey(key)) { return data.get(key); }
        data.put(key, new DimensionData(world));

        return data.get(key);
    }

    public static @Nullable DimensionData get(Integer dimensionId) {
        if (data.containsKey(dimensionId)) { return data.get(dimensionId); }
        return null;
    }

}
