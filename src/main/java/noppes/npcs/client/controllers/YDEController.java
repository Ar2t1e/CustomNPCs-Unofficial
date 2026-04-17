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
    public static int textColor = (new Color(0xF0F0F0).getRGB() & 0xFFFFFF) | 0xFF000000;
    public static int windowLineColor = (new Color(0x4C4C4C).getRGB() & 0xFFFFFF) | alpha;
    public static int gridColor = (new Color(0x8C8C74).getRGB() & 0xFFFFFF) | alpha;

    public static int gridColorEmpty = new Color(0xB66C6C).getRGB();
    public static int selectLineColor = new Color(0xFFFF80).getRGB();
    public static int componentLineColor = new Color(0xC0C0C0).getRGB();
    public static int hoverLineColor = new Color(0xFFFFFF).getRGB();

    public static int leftTabColor = new Color(0x6C00FF).getRGB();
    public static int rightTabColor = new Color(0x5A8A8C).getRGB();


    /**
     * <world map name; <node ID, node>>
     */
    protected final Map<String, YDEData> levels = new HashMap<>();
    public String lastCategory = "";
    public int lastNode = -1;

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
        if (compound.contains("BackColor", 3)) { backColor = (compound.getInt("BackColor") & 0xFFFFFF) | alpha; }
        if (compound.contains("BackHoverColor", 3)) { backHoverColor = (compound.getInt("BackHoverColor") & 0xFFFFFF) | alpha; }
        if (compound.contains("TextColor", 3)) { textColor = (compound.getInt("TextColor") & 0xFFFFFF) | 0xFF000000; }
        if (compound.contains("WindowLineColor", 3)) { windowLineColor = (compound.getInt("WindowLineColor") & 0xFFFFFF) | alpha; }
        if (compound.contains("GridColor", 3)) { gridColor = (compound.getInt("GridColor") & 0xFFFFFF) | alpha; }

        if (compound.contains("GridColorEmpty", 3)) { gridColorEmpty = compound.getInt("GridColorEmpty"); }
        if (compound.contains("SelectLineColor", 3)) { selectLineColor = compound.getInt("SelectLineColor"); }
        if (compound.contains("ComponentLineColor", 3)) { componentLineColor = compound.getInt("ComponentLineColor"); }
        if (compound.contains("HoverLineColor", 3)) { hoverLineColor = compound.getInt("HoverLineColor"); }

        if (compound.contains("LeftTabColor", 3)) { leftTabColor = compound.getInt("LeftTabColor"); }
        if (compound.contains("RightTabColor", 3)) { rightTabColor = compound.getInt("RightTabColor"); }

        lastCategory = compound.getString("LastCategory");
        if (compound.contains("LastNode", 3)) { lastNode = compound.getInt("LastNode"); }
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
        compound.putInt("LineColor", windowLineColor & 0xFFFFFF);
        compound.putInt("GridColor", gridColor & 0xFFFFFF);

        compound.putInt("GridColorEmpty", gridColorEmpty & 0xFFFFFF);
        compound.putInt("SelectLineColor", selectLineColor & 0xFFFFFF);
        compound.putInt("ComponentLineColor", componentLineColor & 0xFFFFFF);
        compound.putInt("HoverLineColor", hoverLineColor & 0xFFFFFF);

        compound.putInt("LeftTabColor", leftTabColor & 0xFFFFFF);
        compound.putInt("RightTabColor", rightTabColor & 0xFFFFFF);

        compound.putString("LastCategory", lastCategory);
        compound.putInt("LastNode", lastNode);

        try {
            NbtIo.writeCompressed(compound, new File(CustomNpcs.Dir, "yde_data.dat"));
            //if (CustomNpcs.VerboseDebug) { NBTJsonUtil.SaveFile(new File(CustomNpcs.Dir, "yde_data.json"), compound); }
        }
        catch (Exception e) { LogWriter.error(e); }
    }

}
