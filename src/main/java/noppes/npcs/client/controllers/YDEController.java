package noppes.npcs.client.controllers;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.gui.yellow_de.data.YDEData;
import noppes.npcs.shared.common.util.LogWriter;

import javax.annotation.Nonnull;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class YDEController {

    protected static YDEController instance;
    protected static int alpha = 0xC0000000;
    public static int backColor = (new Color(0x222222).getRGB() & 0xFFFFFF) | alpha;
    public static int backHoverColor = (new Color(0x333333).getRGB() & 0xFFFFFF) | alpha;
    public static int textColor = (new Color(0xC0C0C0).getRGB() & 0xFFFFFF) | alpha;
    public static int lineColor = (new Color(0x4C4C4C).getRGB() & 0xFFFFFF) | alpha;
    public static int gridColor = (new Color(0x8C8C74).getRGB() & 0xFFFFFF) | alpha;
    public static int gridColorEmpty = new Color(0xB66C6C).getRGB();

    /**
     * <world map name; <node ID, node>>
     */
    protected final Map<String, YDEData> levels = new HashMap<>();

    public static YDEController getInstance() {
        if (instance == null) { instance = new YDEController(); }
        return instance;
    }

    // load all data:
    private YDEController() {
        CompoundTag compound = new CompoundTag();
        File file = new File(CustomNpcs.Dir, "yde_data.dat");
        if (file.exists()) {
            try { compound = NbtIo.readCompressed(file); }
            catch (Exception e) { LogWriter.error(e); }
        }
        else { save(); }
        for (String worldName : compound.getAllKeys()) {
            if (worldName.contains("_") || worldName.contains(";")) {
                levels.put(worldName, new YDEData(compound.getList(worldName, 10)));
            }
        }
        if (compound.contains("BackColor", 3)) {
            backColor = (compound.getInt("BackColor") & 0xFFFFFF) | alpha;
        }
        if (compound.contains("BackHoverColor", 3)) {
            backHoverColor = (compound.getInt("BackHoverColor") & 0xFFFFFF) | alpha;
        }
        if (compound.contains("TextColor", 3)) {
            textColor = (compound.getInt("TextColor") & 0xFFFFFF) | alpha;
        }
        if (compound.contains("LineColor", 3)) {
            lineColor = (compound.getInt("LineColor") & 0xFFFFFF) | alpha;
        }
        if (compound.contains("GridColor", 3)) {
            gridColor = (compound.getInt("GridColor") & 0xFFFFFF) | alpha;
        }
        if (compound.contains("GridColorEmpty", 3)) {
            gridColorEmpty = compound.getInt("GridColorEmpty");
        }
    }

    public @Nonnull YDEData getLevelData(String levelKey) {
        //levels.clear();
        if (!levels.containsKey(levelKey)) { levels.put(levelKey, new YDEData()); }
        return levels.get(levelKey).check();
    }

    public void save() {
        CompoundTag compound = new CompoundTag();
        for (Map.Entry<String, YDEData> entry : levels.entrySet()) {
            compound.put(entry.getKey(), entry.getValue().save());
        }
        compound.putInt("BackColor", backColor & 0xFFFFFF);
        compound.putInt("BackHoverColor", backHoverColor & 0xFFFFFF);
        compound.putInt("TextColor", textColor & 0xFFFFFF);
        compound.putInt("LineColor", lineColor & 0xFFFFFF);
        compound.putInt("GridColor", gridColor & 0xFFFFFF);
        compound.putInt("GridColorEmpty", gridColorEmpty & 0xFFFFFF);
        try {
            NbtIo.writeCompressed(compound, new File(CustomNpcs.Dir, "yde_data.dat"));
            //if (CustomNpcs.VerboseDebug) { NBTJsonUtil.SaveFile(new File(CustomNpcs.Dir, "yde_data.json"), compound); }
        }
        catch (Exception e) { LogWriter.error(e); }
    }

}
