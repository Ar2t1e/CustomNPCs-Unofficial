package noppes.npcs.controllers.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import noppes.npcs.CustomNpcs;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketSync;
import noppes.npcs.util.ValueUtil;

// New from Unofficial (BetaZavr)
@SuppressWarnings("all")
public class DialogGuiSettings {

    public boolean showNPC = true;
    public boolean npcInLeft = true;

    private int type = 0; // 0:right, 1:center, 2:left
    private int showVerticalLines = 2; // 0:none, 1:one, 2:two
    private int showHorizontalLines = 2; // 0:none, 1:one, 2:two
    private double width = 0.85d;
    private double optionH = 1.0d / 3.0d;
    private final float[] npcPos = new float[] { 0.0f, 0.05f };
    private float npcScale = 2.0f;
    private float blurringLine = 0.05f;
    public int backBorderColor = 0x80000000;
    public int backWindowColor = 0xCC000000;
    public int linesColor = 0x40FFFFFF;
    public int scrollLineColor = 0xFF404040;
    public int hoverLineColor = 0xFF80F080;
    public int pointerColor = 0xFFFFFFFF;
    public int sliderColor = 0x90FFFFFF;
    public int selectOptionLeftColor = 0xF0202020;
    public int selectOptionRightColor = 0xF0303030;

    // init on gui
    public int npcWidth;
    public int dialogWidth;
    public int guiLeft = 112;
    public int guiRight = 112;
    public int optionHeight;
    public int npcPosX;
    public int npcPosY;

    public ResourceLocation backTexture;
    public ResourceLocation windowTexture;

    public void load(CompoundTag compound) {
        showNPC = compound.getBoolean("ShowNPC");
        npcInLeft = compound.getBoolean("NPCInLeft");

        setType(compound.getInt("WindowType"));
        setShowVerticalLines(compound.getInt("ShowVerticalLines"));
        setShowHorizontalLines(compound.getInt("ShowHorizontalLines"));
        backBorderColor = compound.getInt("BackBorderColor");
        backWindowColor = compound.getInt("BackWindowColor");
        linesColor = compound.getInt("LinesColor");
        scrollLineColor = compound.getInt("ScrollLineColor");
        hoverLineColor = compound.getInt("HoverLineColor");
        pointerColor = compound.getInt("PointerColor");
        sliderColor = compound.getInt("SliderColor");
        selectOptionLeftColor = compound.getInt("SelectOptionLeftColor");
        selectOptionRightColor = compound.getInt("SelectOptionRightColor");

        setNpcPos(compound.getFloat("NPCPosX"), compound.getFloat("NPCPosY"));
        setNpcScale(compound.getFloat("NPCScale"));
        setBlurringLine(compound.getFloat("BlurringLine"));

        setWidth(compound.getDouble("WindowWidth"));
        setOptionHeight(compound.getDouble("OptionHeight"));

        backTexture = null;
        windowTexture = null;
        if (compound.contains("BackTexture", 8)) { backTexture = new ResourceLocation(compound.getString("BackTexture")); }
        if (compound.contains("WindowTexture", 8)) { windowTexture = new ResourceLocation(compound.getString("WindowTexture")); }
    }

    public CompoundTag save() {
        CompoundTag compound = new CompoundTag();
        compound.putBoolean("ShowNPC", showNPC);
        compound.putBoolean("NPCInLeft", npcInLeft);

        compound.putInt("WindowType", type);
        compound.putInt("ShowVerticalLines", showVerticalLines);
        compound.putInt("ShowHorizontalLines", showHorizontalLines);
        compound.putInt("BackBorderColor", backBorderColor);
        compound.putInt("BackWindowColor", backWindowColor);
        compound.putInt("LinesColor", linesColor);
        compound.putInt("ScrollLineColor", scrollLineColor);
        compound.putInt("HoverLineColor", hoverLineColor);
        compound.putInt("PointerColor", pointerColor);
        compound.putInt("SliderColor", sliderColor);
        compound.putInt("SelectOptionLeftColor", selectOptionLeftColor);
        compound.putInt("SelectOptionRightColor", selectOptionRightColor);

        compound.putFloat("NPCPosX", npcPos[0]);
        compound.putFloat("NPCPosY", npcPos[1]);
        compound.putFloat("NPCScale", npcScale);
        compound.putFloat("BlurringLine", blurringLine);

        compound.putDouble("WindowWidth", width);
        compound.putDouble("OptionHeight", optionH);

        if (backTexture != null) { compound.putString("BackTexture", backTexture.toString()); }
        if (windowTexture != null) { compound.putString("WindowTexture", windowTexture.toString()); }

        return compound;
    }

    public void init(double widthIn, double heightIn) {
        dialogWidth = (int) (widthIn * width);
        npcWidth = (int) widthIn - dialogWidth;
        optionHeight = (int) (heightIn * optionH);
        npcPosY = (int) (heightIn - heightIn * npcPos[1]);
        switch (type) {
            case 0: {
                guiLeft = npcWidth;
                npcPosX = (int) ((float) npcWidth * npcPos[0]) - npcWidth / 2;
                break;
            }
            case 1: {
                npcWidth /= 2;
                guiLeft = npcWidth;
                if (npcInLeft) { npcPosX = (int) ((float) npcWidth * npcPos[0]) - npcWidth / 2; }
                else { npcPosX = dialogWidth + npcWidth / 2 + (int) ((float) npcWidth * npcPos[0]); }
                break;
            }
            case 2: {
                guiLeft = 0;
                npcPosX = dialogWidth + npcWidth / 2 + (int) ((float) npcWidth * npcPos[0]);
                break;
            }
        }
        guiRight = dialogWidth + guiLeft;
    }

    public int getType() { return type; }

    public void setType(int typeIn) {
        if (typeIn < 0) { typeIn *= -1; }
        type = typeIn % 3;
    }

    public double getWidth() { return width; }

    public void setWidth(double widthIn) {
        if (widthIn < 0) { widthIn *= -1; }
        width = Math.min(Math.max(widthIn, 0.4d), 0.9d);
    }

    public double getOptionHeight() { return optionH; }

    public void setOptionHeight(double lengthIn) {
        if (lengthIn < 0) { lengthIn *= -1; }
        optionH = Math.min(Math.max(lengthIn, 0.15d), 0.75d);
    }

    public float[] getNpcPos() { return npcPos; }

    public void setNpcPos(float x, float y) {
        npcPos[0] = ValueUtil.correctFloat(x, -0.5f, 0.5f);
        npcPos[1] = ValueUtil.correctFloat(y, -0.25f, 0.25f);
    }

    public float getNpcScale() { return npcScale; }

    public void setNpcScale(float scale) {
        npcScale = ValueUtil.correctFloat(scale, 0.15f, 5.0f);
    }

    public int getShowHorizontalLines() { return showHorizontalLines; }

    public void setShowHorizontalLines(int typeIn) {
        if (typeIn < 0) { typeIn *= -1; }
        showHorizontalLines = typeIn % 3;
    }

    public int getShowVerticalLines() { return showVerticalLines; }

    public void setShowVerticalLines(int typeIn) {
        if (typeIn < 0) { typeIn *= -1; }
        showVerticalLines = typeIn % 3;
    }

    public float getBlurringLine() { return blurringLine; }

    public void setBlurringLine(float border) {
        blurringLine = ValueUtil.correctFloat(border, 0.0f, 0.25f);
    }

    public void update() {
        if (CustomNpcs.Server != null) { Packets.sendAll(new PacketSync(10, save(), false)); }
    }

    public ResourceLocation getBackgroundTexture() { return backTexture; }

    public ResourceLocation getWindowTexture() { return windowTexture; }

}
