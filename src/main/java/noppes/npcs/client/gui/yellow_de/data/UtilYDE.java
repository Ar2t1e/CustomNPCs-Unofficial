package noppes.npcs.client.gui.yellow_de.data;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.shared.client.gui.GuiBasic;

import java.util.ArrayList;
import java.util.List;

public class UtilYDE{

    public static final ClientProxy.FontContainer FONT = new ClientProxy.FontContainer("JetBrainsMono", 12);
    public static final ClientProxy.FontContainer FONT_HEADLINE = new ClientProxy.FontContainer("JetBrainsMono", 18);

    public static void renderSpline(float[] p0, float[] p1, boolean hovered, boolean turned, int color) {
        int r = color >> 16 & 255;
        int g = color >> 8 & 255;
        int b = color & 255;
        int nx = Math.abs(p0[0] - p1[0]) > Math.abs(p0[1] - p1[1]) ? 1 : 0;
        int ny = nx == 1 ? 0 : 1;
        int alpha = hovered ? 255 : 160;
        if (turned) {

        }
        // horizontal or vertical
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_NORMAL);
        if (p0[0] == p1[0] || p0[1] == p1[1]) {
            bufferbuilder.pos(p0[0], p0[1], 0.0f)
                    .color(r, g, b, alpha)
                    .normal(nx, ny, 0)
                    .endVertex();
            bufferbuilder.pos(p1[0], p1[1], 0.0f)
                    .color(r, g, b, alpha)
                    .normal(nx, ny, 0)
                    .endVertex();
            return;
        }
        // spline
        float x3 = p0[0] + (p1[0] - p0[0]) / 2.0f;
        float y3 = (p1[1] - p0[1]) / 40.0f;
        int steps = (int) (Math.min(Math.abs(p0[0] - p1[0]), Math.abs(p0[1] - p1[1])));
        List<float[]> points = cubicBezier(new float[] { p0[0], p0[1] },
                new float[] { x3, p0[1] + y3 },
                new float[] { x3, p1[1] - y3 },
                new float[] { p1[0], p1[1] }, steps);
        for (int i = 0; i < points.size() - 1; i++) {
            float[] v0 = points.get(i);
            float[] v1 = points.get(i + 1);
            bufferbuilder.pos(v0[0], v0[1], 0.0f)
                    .color(r, g, b, alpha)
                    .normal(nx, ny, 0)
                    .endVertex();
            bufferbuilder.pos(v1[0], v1[1], 0.0f)
                    .color(r, g, b, alpha)
                    .normal(nx, ny, 0)
                    .endVertex();
        }
        tessellator.draw();
    }

    public static List<float[]> cubicBezier(float[] p0, float[] p1, float[] p2, float[] p3, int segments) {
        List<float[]> points = new ArrayList<>();
        for (int i = 0; i <= segments; i++) {
            float t = i / (float)segments;
            float iT = 1 - t;
            // B(t) = (1-t)³P₀ + 3(1-t)²tP₁ + 3(1-t)t²P₂ + t³P₃
            float x = iT * iT * iT * p0[0] + 3 * iT * iT * t * p1[0] + 3 * iT * t * t * p2[0] + t * t * t * p3[0];
            float y = iT * iT * iT * p0[1] + 3 * iT * iT * t * p1[1] + 3 * iT * t * t * p2[1] + t * t * t * p3[1];
            points.add(new float[] { x, y });
        }
        return points;
    }

    public static void renderDot(float[] point, float scale, boolean hovered, int color) {
        float rC = (float)(color >> 16 & 255) / 255.0F;
        float gC = (float)(color >> 8 & 255) / 255.0F;
        float bC = (float)(color & 255) / 255.0F;

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.color(rC, gC, bC, hovered ? 1.0f : 0.75f);
        GlStateManager.translate(point[0] - 3.5f * scale, point[1] - 3.5f * scale, 0.0f);
        GlStateManager.scale(0.5f * scale, 0.5f * scale, 0.5f * scale);
        Minecraft.getMinecraft().getTextureManager().bindTexture(GuiBasic.INFO);

        float f = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(0.0d, 14.0d, 0.0d).tex(0.0f, 32.0f * f).endVertex();
        bufferbuilder.pos(14.0d, 14.0d, 0.0d).tex(14.0f * f, 32.0f * f).endVertex();
        bufferbuilder.pos(14.0d, 0.0d, 0.0d).tex(14.0f * f, 18.0f * f).endVertex();
        bufferbuilder.pos(0.0d, 0.0d, 0.0d).tex(0.0f, 18.0f * f).endVertex();
        tessellator.draw();

        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
    }

    public static void fill(float x0, float y0, float x1, float y1,
                            float r, float g, float b, float a) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(x0, y0, 0.0d).color(r, g, b, a).endVertex();
        bufferbuilder.pos(x0, y1, 0.0d).color(r, g, b, a).endVertex();
        bufferbuilder.pos(x1, y1, 0.0d).color(r, g, b, a).endVertex();
        bufferbuilder.pos(x1, y0, 0.0d).color(r, g, b, a).endVertex();
        tessellator.draw();
    }

}
