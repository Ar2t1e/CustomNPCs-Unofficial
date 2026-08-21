package noppes.npcs.client.overlay;

import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;
import noppes.npcs.api.gui.ITexturedRect;

public class OverlayTexturedRectComponent implements IOverlayRenderComponent {

    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final String texture;
    private final int textureX;
    private final int textureY;
    private final int textureMaxX;
    private final int textureMaxY;
    private final int id;

    public OverlayTexturedRectComponent(ITexturedRect component) {
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
    }

    public void render(int linkSide) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(2 * x, 2 * y, (double)id);
        ScaledResolution sw = new ScaledResolution(Minecraft.getMinecraft());
        double width = sw.getScaledWidth_double();
        double height = sw.getScaledHeight_double();
        int i = (int) (width / 2.0d);
        if (Objects.equals(texture, "")) {
            renderGradientRect(x, y, linkSide, width, height, width, height, i, -1072689136, -804253680);
        } else {
            ResourceLocation resLoc = new ResourceLocation(texture);
            if (textureX >= 0 && textureY >= 0) {
                if (textureMaxX >= 0 && textureMaxY >= 0) {
                    renderRectTextureCustomSize(resLoc, x, y, linkSide, width, height, width, height, textureX, textureY, textureMaxX, textureMaxY);
                } else {
                    renderRectTextureSize(resLoc, x, y, linkSide, width, height, width, height, textureX, textureY);
                }
            } else {
                renderRectTexture(resLoc, x, y, linkSide, width, height, width, height);
            }
        }

        GlStateManager.popMatrix();
    }

    public void renderGradientRect(int x, int y, int linkSide, double widthScaled, double heightScaled, double width, double height, int ignoredI, int startColor, int endColor) {
        int offsetX = (int) (widthScaled / 2.0d * ((linkSide - 1.0d) % 3.0d));
        int offsetY = (int) (heightScaled / 2.0d * ((linkSide - 1.0d) / 3.0d));
        GuiUtils.drawGradientRect(0, offsetX, offsetY, (int) (offsetX + width), (int) (offsetY + height), startColor, endColor);
    }

    public void renderRectTexture(ResourceLocation resLoc, int x, int y, int linkSide, double widthScaled, double heightScaled, double width, double height) {
        renderRectTextureCustomSize(resLoc, 0, 0, linkSide, widthScaled, heightScaled, width, height, 0, 0, 256, 256);
    }

    public void renderRectTextureSize(ResourceLocation resLoc, int x, int y, int linkSide, double widthScaled, double heightScaled, double width, double height, int textureX, int textureY) {
        renderRectTextureCustomSize(resLoc, 0, 0, linkSide, widthScaled, heightScaled, width, height, textureX, textureY, 256, 256);
    }

    public void renderRectTextureCustomSize(ResourceLocation resLoc, int x, int y, int linkSide, double widthScaled, double heightScaled, double width, double height, int textureX, int textureY, int textureMaxX, int textureMaxY) {
        int offsetX = (int) (widthScaled / 2.0d * ((linkSide - 1.0d) % 3.0d));
        int offsetY = (int) (heightScaled / 2.0d * ((linkSide - 1.0d) / 3.0d));
        Minecraft.getMinecraft().getTextureManager().bindTexture(resLoc);
        drawTexturedModalRect(offsetX, offsetY, textureX, textureY, textureMaxX, textureMaxY);
    }


    public void drawTexturedModalRect(double x, double y, int textureX, int textureY, double width, double height) {
        float f = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x + 0, y + height, 0.0d).tex((float)(textureX) * f, (float)(textureY + height) * f).endVertex();
        bufferbuilder.pos(x + width, y + height, 0.0d).tex((float)(textureX + width) * f, (float)(textureY + height) * f).endVertex();
        bufferbuilder.pos(x + width, y + 0, 0.0d).tex((float)(textureX + width) * f, (float)(textureY) * f).endVertex();
        bufferbuilder.pos(x + 0, y + 0, 0.0d).tex((float)(textureX) * f, (float)(textureY) * f).endVertex();
        tessellator.draw();
    }

}
