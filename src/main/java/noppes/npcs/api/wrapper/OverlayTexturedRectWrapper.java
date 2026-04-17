package noppes.npcs.api.wrapper;

import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.api.INbt;
import noppes.npcs.api.overlay.IOverlayTexturedRect;
import noppes.npcs.util.ValueUtil;

public class OverlayTexturedRectWrapper extends OverlayComponentWrapper implements IOverlayTexturedRect {

    private final float[] uv = new float[] { 0.0f, 0.0f, 1.0f, 1.0f };
    private String texture;
    private int width;
    private int height;
    private int color = 0xFFFFFFFF;
    int textureX;
    int textureY = -1;
    int textureMaxX;
    int textureMaxY = -1;

    public OverlayTexturedRectWrapper(int id, int x, int y, String textureIn, int widthIn, int heightIn) {
        super(id, x, y);
        texture = textureIn;
        width = widthIn;
        height = heightIn;
    }

    public OverlayTexturedRectWrapper(int id, int x, int y, String textureIn, int widthIn, int heightIn, int textureX, int textureY) {
        super(id, x, y);
        texture = textureIn;
        width = widthIn;
        height = heightIn;
        setTextureOffset(textureX, textureY);
    }

    public OverlayTexturedRectWrapper(int id, int x, int y, String textureIn, int widthIn, int heightIn, int textureX, int textureY, int textureMaxX, int textureMaxY) {
        super(id, x, y);
        texture = textureIn;
        width = widthIn;
        height = heightIn;
        setTextureOffset(textureX, textureY);
        setTextureMaxSize(textureMaxX, textureMaxY);
    }

    @Override
    public int getTextureX() { return textureX; }

    @Override
    public int getTextureY() { return textureY; }

    @Override
    public int getTextureMaxX() { return textureMaxX; }

    @Override
    public int getTextureMaxY() { return textureMaxY; }

    @Override
    public IOverlayTexturedRect setTextureOffset(int offsetX, int offsetY) {
        textureX = offsetX;
        textureY = offsetY;
        return this;
    }

    @Override
    public IOverlayTexturedRect setTextureMaxSize(int textureMaxXIn, int textureMaxYIn) {
        textureMaxX = textureMaxXIn;
        textureMaxY = textureMaxYIn;
        return this;
    }

    @Override
    public String getTexture() { return texture; }

    @Override
    public IOverlayTexturedRect setTexture(String textureIn) {
        texture = textureIn;
        return this;
    }

    @Override
    public int getWidth() { return width; }

    @Override
    public IOverlayTexturedRect setWidth(int widthIn) {
        width = widthIn;
        return this;
    }

    @Override
    public int getHeight() { return height; }

    @Override
    public IOverlayTexturedRect setHeight(int heightIn) {
        height = heightIn;
        return this;
    }

    @Override
    public int getType() { return 1; }

    @Override
    public IOverlayTexturedRect setUV(float u0, float v0, float u1, float v1) {
        uv[0] = u0;
        uv[1] = v0;
        uv[2] = u1;
        uv[3] = v1;
        return this;
    }

    @Deprecated
    public IOverlayTexturedRect setRGB(float red, float green, float blue, float alpha) {
        setColor(red, green, blue, alpha);
        return this;
    }

    @Override
    public IOverlayTexturedRect setColor(float red, float green, float blue, float alpha) {
        setColor((int) (ValueUtil.correctFloat(alpha, 0.0f, 1.0f) * 255.0f) << 24,
                (int) (ValueUtil.correctFloat(red, 0.0f, 1.0f) * 255.0f) << 16,
                (int) (ValueUtil.correctFloat(green, 0.0f, 1.0f) * 255.0f) << 8,
                (int) (ValueUtil.correctFloat(blue, 0.0f, 1.0f) * 255.0f));
        return this;
    }

    @Override
    public IOverlayTexturedRect setColor(int red, int green, int blue, int alpha) {
        color = ValueUtil.correctInt(alpha, 0, 255) |
                ValueUtil.correctInt(red, 0, 255) |
                ValueUtil.correctInt(green, 0, 255) |
                ValueUtil.correctInt(blue, 0, 255);
        return this;
    }

    @Override
    public IOverlayTexturedRect setColor(int colorIn) {
        color = colorIn;
        return this;
    }

    @Override
    public float[] getRGB() {
        float[] rgba = new float[4];
        rgba[0] = ((color >> 16) & 255) / 255.0f;
        rgba[1] = ((color >> 8) & 255) / 255.0f;
        rgba[2] = (color & 255) / 255.0f;
        rgba[3] = ((color >> 24) & 255) / 255.0f;
        return rgba;
    }

    @Override
    public int getColor() { return color; }

    @Override
    public float[] getUV() { return uv; }

    @Override
    public void toNbt(INbt iNbt) {
        super.toNbt(iNbt);
        iNbt.setString("texture", texture);
        iNbt.setInteger("width", width);
        iNbt.setInteger("height", height);
        NBTTagList list = new NBTTagList();
        list.appendTag(new NBTTagFloat(uv[0]));
        list.appendTag(new NBTTagFloat(uv[1]));
        list.appendTag(new NBTTagFloat(uv[2]));
        list.appendTag(new NBTTagFloat(uv[3]));
        iNbt.mcSetTag("u", list);
        iNbt.setInteger("c", color);
        if (textureX >= 0 && textureY >= 0) { iNbt.setIntegerArray("texPos", new int[] { textureX, textureY }); }
        if (textureMaxX >= 0 && textureMaxY >= 0) { iNbt.setIntegerArray("texPosMax", new int[] { textureMaxX, textureMaxY }); }
    }

    @Override
    public void fromNbt(INbt iNbt) {
        super.fromNbt(iNbt);
        texture = iNbt.getString("texture");
        width = iNbt.getInteger("width");
        height = iNbt.getInteger("height");
        if (iNbt.has("c", 3)) { setColor(iNbt.getInteger("c")); } else { setColor(0xFFFFFFFF); }
        if (iNbt.has("u", 9) && iNbt.mcGetTag("u") instanceof NBTTagList) {
            NBTTagList list = (NBTTagList) iNbt.mcGetTag("u");
            if (list.getTagType() == 5) {
                for (int i = 0; i < 4; i++) { uv[i] = i < list.tagCount() ? list.getFloatAt(i) : 1.0f; }
            }
        }
        else if (iNbt.has("u", 3)) {
            int uvInt = iNbt.getInteger("u");
            setUV((float)(uvInt >> 24 & 255) / 255.0F, (float)(uvInt >> 16 & 255) / 255.0F, (float)(uvInt >> 8 & 255) / 255.0F, (float)(uvInt & 255) / 255.0F);
        } // OLD
        else { setUV(0.0F, 0.0F, 1.0F, 1.0F); }
        if (iNbt.has("texPos", 11)) { setTextureOffset(iNbt.getIntegerArray("texPos")[0], iNbt.getIntegerArray("texPos")[1]); }
        if (iNbt.has("texPosMax", 11)) { setTextureMaxSize(iNbt.getIntegerArray("texPosMax")[0], iNbt.getIntegerArray("texPosMax")[1]); }
    }

}
