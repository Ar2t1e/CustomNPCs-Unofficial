package noppes.npcs.client.controllers;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.gui.yellow_de.data.YDEData;
import noppes.npcs.shared.common.util.LogWriter;

import javax.annotation.Nonnull;
import java.awt.*;
import java.io.DataInputStream;
import java.io.File;
import java.nio.file.Files;
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
    public Map<String, Integer> lastNode = new HashMap<>();

    public static YDEController getInstance() {
        if (instance == null) { instance = new YDEController(); }
        return instance;
    }

    // load all data:
    private YDEController() {
        NBTTagCompound compound = new NBTTagCompound();
        File file = new File(CustomNpcs.Dir, "yde_data.dat");
        if (file.exists()) {
            try { compound = CompressedStreamTools.readCompressed(new DataInputStream(Files.newInputStream(file.toPath()))); }
            catch (Exception e) { LogWriter.error(e); }
        }
        else { save(); }
        for (String worldName : compound.getCompoundTag("levels").getKeySet()) {
            if (worldName.contains("_") || worldName.contains(";")) {
                levels.put(worldName, new YDEData(compound.getCompoundTag(worldName)));
            }
        }
        if (compound.hasKey("BackColor", 3)) { backColor = (compound.getInteger("BackColor") & 0xFFFFFF) | alpha; }
        if (compound.hasKey("BackHoverColor", 3)) { backHoverColor = (compound.getInteger("BackHoverColor") & 0xFFFFFF) | alpha; }
        if (compound.hasKey("TextColor", 3)) { textColor = (compound.getInteger("TextColor") & 0xFFFFFF) | 0xFF000000; }
        if (compound.hasKey("WindowLineColor", 3)) { windowLineColor = (compound.getInteger("WindowLineColor") & 0xFFFFFF) | alpha; }
        if (compound.hasKey("GridColor", 3)) { gridColor = (compound.getInteger("GridColor") & 0xFFFFFF) | alpha; }

        if (compound.hasKey("GridColorEmpty", 3)) { gridColorEmpty = compound.getInteger("GridColorEmpty"); }
        if (compound.hasKey("SelectLineColor", 3)) { selectLineColor = compound.getInteger("SelectLineColor"); }
        if (compound.hasKey("ComponentLineColor", 3)) { componentLineColor = compound.getInteger("ComponentLineColor"); }
        if (compound.hasKey("HoverLineColor", 3)) { hoverLineColor = compound.getInteger("HoverLineColor"); }

        if (compound.hasKey("LeftTabColor", 3)) { leftTabColor = compound.getInteger("LeftTabColor"); }
        if (compound.hasKey("RightTabColor", 3)) { rightTabColor = compound.getInteger("RightTabColor"); }

        lastCategory = compound.getString("LastCategory");
        lastNode.clear();
        for (int i = 0; i < compound.getTagList("LastNode", 10).tagCount(); i++) {
            NBTTagCompound nbt = compound.getTagList("LastNode", 10).getCompoundTagAt(i);
            lastNode.put(nbt.getString("K"), nbt.getInteger("V"));
        }
    }

    public @Nonnull YDEData getLevelData(String levelKey) {
        if (!levels.containsKey(levelKey)) { levels.put(levelKey, new YDEData()); }
        return levels.get(levelKey).check();
    }

    public void save() {
        NBTTagCompound compound = new NBTTagCompound();
        NBTTagCompound data = new NBTTagCompound();
        for (Map.Entry<String, YDEData> entry : levels.entrySet()) {
            data.setTag(entry.getKey(), entry.getValue().save());
        }
        compound.setTag("levels", data);

        compound.setInteger("BackColor", backColor & 0xFFFFFF);
        compound.setInteger("BackHoverColor", backHoverColor & 0xFFFFFF);
        compound.setInteger("TextColor", textColor & 0xFFFFFF);
        compound.setInteger("LineColor", windowLineColor & 0xFFFFFF);
        compound.setInteger("GridColor", gridColor & 0xFFFFFF);

        compound.setInteger("GridColorEmpty", gridColorEmpty & 0xFFFFFF);
        compound.setInteger("SelectLineColor", selectLineColor & 0xFFFFFF);
        compound.setInteger("ComponentLineColor", componentLineColor & 0xFFFFFF);
        compound.setInteger("HoverLineColor", hoverLineColor & 0xFFFFFF);

        compound.setInteger("LeftTabColor", leftTabColor & 0xFFFFFF);
        compound.setInteger("RightTabColor", rightTabColor & 0xFFFFFF);

        compound.setString("LastCategory", lastCategory);
        NBTTagList list = new NBTTagList();
        for (Map.Entry<String, Integer> entry : lastNode.entrySet()) {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setString("K", entry.getKey());
            nbt.setInteger("V", entry.getValue());
            list.appendTag(nbt);
        }
        compound.setTag("LastNode", list);

        try {
            CompressedStreamTools.writeCompressed(compound, Files.newOutputStream(new File(CustomNpcs.Dir, "yde_data.dat").toPath()));
            //if (CustomNpcs.VerboseDebug) { NBTJsonUtil.SaveFile(new File(CustomNpcs.Dir, "yde_data.json"), compound); }
        }
        catch (Exception e) { LogWriter.error(e); }
    }

}
