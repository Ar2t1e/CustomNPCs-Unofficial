package noppes.npcs.client.gui.yellow_de.data;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FastColor;
import noppes.npcs.api.util.IRayTraceRotate;
import noppes.npcs.api.util.IRayTraceVec;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.shared.client.gui.GuiBasic;
import noppes.npcs.util.Util;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class UtilYDE {

    public static final ClientProxy.FontContainer FONT = new ClientProxy.FontContainer("JetBrainsMono", 12);
    public static final ClientProxy.FontContainer FONT_HEADLINE = new ClientProxy.FontContainer("JetBrainsMono", 18);

    /**
     * @param p0 - start point
     * @param p1 - end point
     * @param hovered - is hovered
     * @param turnedType 0: not turned; 1: horizontal 2: vertical
     */
    public static void renderSpline(GuiGraphics graphics, float[] p0, float[] p1, boolean hovered, int turnedType, int color, float zDepth) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        int r = FastColor.ARGB32.red(color);
        int g = FastColor.ARGB32.green(color);
        int b = FastColor.ARGB32.blue(color);
        int nx = Math.abs(p0[0] - p1[0]) > Math.abs(p0[1] - p1[1]) ? 1 : 0;
        int ny = nx == 1 ? 0 : 1;
        int alpha = hovered ? 192 : 148;
        VertexConsumer consumer = graphics.bufferSource().getBuffer(RenderType.lines());
        Matrix4f matrix = graphics.pose().last().pose();
        if (p0[0] == p1[0] || p0[1] == p1[1]) {
            consumer.vertex(matrix, p0[0], p0[1], zDepth)
                    .color(r, g, b, alpha)
                    .normal(nx, ny, 0)
                    .endVertex();
            consumer.vertex(matrix, p1[0], p1[1], zDepth)
                    .color(r, g, b, alpha)
                    .normal(nx, ny, 0)
                    .endVertex();
        } // horizontal or vertical
        else {
            int steps = (int) (Math.min(Math.abs(p0[0] - p1[0]), Math.abs(p0[1] - p1[1])));
            float x3 = p0[0] + (p1[0] - p0[0]) / 2.0f;
            float y3 = (p1[1] - p0[1]) / 40.0f;
            List<float[]> points;
            if (turnedType != 0) {
                float[] p2 = new float[]{ p0[0] + (p0[0] > p1[0] ? 5 : -5), p0[1] + (p0[1] > p1[1] ? -5 : 5)};
                float[] p5 = new float[]{ p1[0] + (p0[0] > p1[0] ? -5 : 5), p1[1] + (p0[1] > p1[1] ? 5 : -5)};
                IRayTraceRotate angles = Util.instance.getAngles3D(p0[0], 0, p0[1], p1[0], 0, p1[1]);
                IRayTraceVec pos0 = Util.instance.getPosition(p0[0], 0, p0[1], angles.getYaw() / 2.0d, 0, angles.getRadiusXZ() * 0.25);
                IRayTraceVec pos1 = Util.instance.getPosition(p1[0], 0, p1[1], 180.0d + angles.getYaw() / 2.0d, 0, angles.getRadiusXZ() * 0.25);
                float[] p3 = new float[] {(float) pos0.getX(), (float) pos0.getZ()};
                float[] p4 = new float[] {(float) pos1.getX(), (float) pos1.getZ()};
                points = quinticBezier(p0, p2, p3, p4, p5, p1, steps);
            }
            else {
                points = cubicBezier(p0,
                        new float[] { x3, p0[1] + y3 },
                        new float[] { x3, p1[1] - y3 },
                        p1, steps);
            }
            for (int i = 0; i < points.size() - 1; i++) {
                float[] v0 = points.get(i);
                float[] v1 = points.get(i + 1);
                consumer.vertex(matrix, v0[0], v0[1], zDepth)
                        .color(r, g, b, alpha)
                        .normal(nx, ny, 0)
                        .endVertex();
                consumer.vertex(matrix, v1[0], v1[1], zDepth)
                        .color(r, g, b, alpha)
                        .normal(nx, ny, 0)
                        .endVertex();
            }
        } // spline
    }

    public static List<float[]> quinticBezier(float[] p0, float[] p1, float[] p2,
                                              float[] p3, float[] p4, float[] p5,
                                              int segments) {
        List<float[]> points = new ArrayList<>();
        for (int i = 0; i <= segments; i++) {
            float t = i / (float) segments;
            float iT = 1 - t;
            // B(t) = Σ C(5,k) * (1-t)^(5-k) * t^k * Pk
            // C(5,k) = 5! / (k! * (5-k)!)
            // k=0: 1, k=1: 5, k=2: 10, k=3: 10, k=4: 5, k=5: 1
            float c0 = (float) Math.pow(iT, 5.0f);                                  // (1-t)^5 * 1
            float c1 = 5.2f * (float) Math.pow(iT, 4.0f) * t;                       // (1-t)^4 * t * 5
            float c2 = 10 * (float) Math.pow(iT, 3.0f) * (float) Math.pow(t, 2.0f); // (1-t)^3 * t^2 * 10
            float c3 = 10 * (float) Math.pow(iT, 2.0f) * (float) Math.pow(t, 3.0f); // (1-t)^2 * t^3 * 10
            float c4 = 4.8f * iT * (float) Math.pow(t, 4.0f);                       // (1-t) * t^4 * 5
            float c5 = (float) Math.pow(t, 5.0f);                                   // t^5 * 1

            float x = c0 * p0[0] + c1 * p1[0] + c2 * p2[0] + c3 * p3[0] + c4 * p4[0] + c5 * p5[0];
            float y = c0 * p0[1] + c1 * p1[1] + c2 * p2[1] + c3 * p3[1] + c4 * p4[1] + c5 * p5[1];

            points.add(new float[] { x, y });
        }
        return points;
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
