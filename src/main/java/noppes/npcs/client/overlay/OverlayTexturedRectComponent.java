package noppes.npcs.client.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.api.constants.AlignmentType;
import noppes.npcs.api.overlay.IOverlayTexturedRect;

import javax.annotation.Nonnull;

public class OverlayTexturedRectComponent extends Gui implements IOverlayRenderComponent {

    protected final int x;
    protected final int y;
    protected final int width;
    protected final int height;
    protected final int textureX;
    protected final int textureY;
    protected final int textureMaxX;
    protected final int textureMaxY;
    protected final int id;
    protected final float[] layerColor;
    protected final float scale;
    protected final String texture;
    protected AlignmentType alignment;
    protected final @Nonnull Minecraft minecraft;

    public OverlayTexturedRectComponent(IOverlayTexturedRect component) {
        x = component.getPosX();
        y = component.getPosY();
        id = component.getId();
        width = component.getWidth();
        height = component.getHeight();
        texture = component.getTexture();
        textureX = component.getTextureX();
        textureY = component.getTextureY();
        textureMaxX = component.getTextureMaxX();
        textureMaxY = component.getTextureMaxY();
        alignment = AlignmentType.get(component.getAlignment());
        scale = component.getScale();
        layerColor = component.getRGB();
        minecraft = Minecraft.getMinecraft();
    }

    @SuppressWarnings("ConstantConditions")
    public void render(int linkSide) {
        ScaledResolution sw = new ScaledResolution(Minecraft.getMinecraft());
        int widthWin = sw.getScaledWidth();
        int heightWin = sw.getScaledHeight();
        float xPos = x;
        float yPos = y;
        AlignmentType type = alignment != AlignmentType.NONE ? alignment : AlignmentType.get(linkSide);
        if (type != AlignmentType.NONE) {
            xPos += alignment.getOffsetX(widthWin / 2);
            yPos += alignment.getOffsetY(heightWin / 2);
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(xPos, yPos, (float) id * 0.01F);
        GlStateManager.scale(scale, scale, 1.0f);
        if (layerColor[3] != 0) { GlStateManager.color(layerColor[0], layerColor[1], layerColor[2], layerColor[3]); }
        ResourceLocation resLoc = new ResourceLocation(texture);

        if (texture.isEmpty() || minecraft.getTextureManager().getTexture(resLoc) == null) { drawGradientRect(0, 0, width, height, 0xC0101010, 0xD0101010); } // no texture
        else {
            minecraft.getTextureManager().bindTexture(resLoc);
            drawTexturedModalRect(0, 0, textureX, textureY, textureMaxX, textureMaxY);
        }
        GlStateManager.popMatrix();
    }

}
