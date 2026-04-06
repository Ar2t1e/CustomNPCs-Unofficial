package noppes.npcs.client.gui.yellow_de.data;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FastColor;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.shared.client.gui.GuiBasic;
import noppes.npcs.shared.common.util.LogWriter;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UtilYDE {

    public static final ClientProxy.FontContainer FONT = new ClientProxy.FontContainer("JetBrainsMono", 12);
    public static final ClientProxy.FontContainer FONT_HEADLINE = new ClientProxy.FontContainer("JetBrainsMono", 18);

    public static void renderSpline(GuiGraphics graphics, float[] p0, float[] p1, boolean hovered, boolean turned, int color) {
        int r = FastColor.ARGB32.red(color);
        int g = FastColor.ARGB32.green(color);
        int b = FastColor.ARGB32.blue(color);
        int nx = Math.abs(p0[0] - p1[0]) > Math.abs(p0[1] - p1[1]) ? 1 : 0;
        int ny = nx == 1 ? 0 : 1;
        int alpha = hovered ? 255 : 160;
        VertexConsumer consumer = graphics.bufferSource().getBuffer(RenderType.lines());
        Matrix4f matrix = graphics.pose().last().pose();
        if (turned) {

        }
        // horizontal or vertical
        if (p0[0] == p1[0] || p0[1] == p1[1]) {
            consumer.vertex(matrix, p0[0], p0[1], 0.0f)
                    .color(r, g, b, alpha)
                    .normal(nx, ny, 0)
                    .endVertex();
            consumer.vertex(matrix, p1[0], p1[1], 0.0f)
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
            consumer.vertex(matrix, v0[0], v0[1], 0.0f)
                    .color(r, g, b, alpha)
                    .normal(nx, ny, 0)
                    .endVertex();
            consumer.vertex(matrix, v1[0], v1[1], 0.0f)
                    .color(r, g, b, alpha)
                    .normal(nx, ny, 0)
                    .endVertex();
        }
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

    public static void renderDot(GuiGraphics graphics, float[] point, float scale, boolean hovered, int color) {
        float rC = FastColor.ARGB32.red(color) / 255.0f;
        float gC = FastColor.ARGB32.green(color) / 255.0f;
        float bC = FastColor.ARGB32.blue(color) / 255.0f;

        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(rC, gC, bC, hovered ? 1.0f : 0.75f);
        matrixStack.translate(point[0] - 3.5f * scale, point[1] - 3.5f * scale, 0.0f);
        matrixStack.scale(0.5f * scale, 0.5f * scale, 0.5f * scale);
        graphics.blit(GuiBasic.INFO, 0, 0, 0, 18, 14, 14);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        matrixStack.popPose();
    }

    public static void fill(GuiGraphics graphics,
                            float x0, float y0, float x1, float y1,
                            float r, float g, float b, float a) {
        VertexConsumer consumer = graphics.bufferSource().getBuffer(RenderType.gui());
        Matrix4f matrix = graphics.pose().last().pose();
        consumer.vertex(matrix, x0, y0, 0.0f).color(r, g, b, a).endVertex();
        consumer.vertex(matrix, x0, y1, 0.0f).color(r, g, b, a).endVertex();
        consumer.vertex(matrix, x1, y1, 0.0f).color(r, g, b, a).endVertex();
        consumer.vertex(matrix, x1, y0, 0.0f).color(r, g, b, a).endVertex();
        graphics.bufferSource().endBatch();
    }

}
