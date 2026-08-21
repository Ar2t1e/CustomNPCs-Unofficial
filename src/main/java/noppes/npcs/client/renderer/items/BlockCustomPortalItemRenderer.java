package noppes.npcs.client.renderer.items;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.npcs.CustomNpcs;
import noppes.npcs.blocks.custom.CustomBlockPortal;
import noppes.npcs.client.renderer.ShaderProgram;
import noppes.npcs.client.renderer.blocks.BlockPortalRenderer;
import noppes.npcs.items.ItemNpcBlock;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class BlockCustomPortalItemRenderer extends TileEntityItemStackRenderer {

    private static final float[][] STATES = {
            {0.0f, 0.0f, 0.25f, 1.0f, 1.0f, 0.65f},
            {0.35f, 0.0f, 0.0f, 0.65f, 1.0f, 1.0f},
            {0.0f, 0.35f, 0.0f, 1.0f, 0.65f, 1.0f},
    };
    private static final long STATE_MS = 2000L;
    private static final long CYCLE_MS = 3L * STATE_MS;

    @Override
    public void renderByItem(@Nonnull ItemStack stack, float partialTicks) {
        if (!(stack.getItem() instanceof ItemNpcBlock) ||
                !(((ItemNpcBlock) stack.getItem()).getBlock() instanceof CustomBlockPortal)) {
            return;
        }

        CustomBlockPortal portal = (CustomBlockPortal) ((ItemNpcBlock) stack.getItem()).getBlock();
        ShaderProgram shader = BlockPortalRenderer.getShader(portal.getCustomName());
        if (shader == null) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        ResourceLocation skyTexture = new ResourceLocation(
                CustomNpcs.MODID, "textures/environment/" + portal.getCustomName() + "_sky.png"
        );
        ResourceLocation portalTexture = new ResourceLocation(
                CustomNpcs.MODID, "textures/entity/" + portal.getCustomName() + "_portal.png"
        );

        // === Анимация: интерполяция между STATES ===
        long time = System.currentTimeMillis() % CYCLE_MS;
        int stateIndex = (int) (time / STATE_MS);
        float progress = (time % STATE_MS) / (float) STATE_MS;
        int nextIndex = (stateIndex + 1) % STATES.length;

        float[] current = STATES[stateIndex];
        float[] next = STATES[nextIndex];

        float minX = lerp(current[0], next[0], progress);
        float minY = lerp(current[1], next[1], progress);
        float minZ = lerp(current[2], next[2], progress);
        float maxX = lerp(current[3], next[3], progress);
        float maxY = lerp(current[4], next[4], progress);
        float maxZ = lerp(current[5], next[5], progress);

        int prevProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
        int prevTexUnit = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        boolean prevBlend = GL11.glIsEnabled(GL11.GL_BLEND);
        int prevBlendSrc = GL11.glGetInteger(GL11.GL_BLEND_SRC);
        int prevBlendDst = GL11.glGetInteger(GL11.GL_BLEND_DST);
        boolean prevLight = GL11.glIsEnabled(GL11.GL_LIGHTING);
        boolean prevDepthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
        boolean prevCull = GL11.glIsEnabled(GL11.GL_CULL_FACE);

        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
        );
        GlStateManager.depthMask(false);
        GlStateManager.disableCull();

        shader.use();
        try {
            shader.set1f("Alpha", 0.85f);
            shader.set1f("GameTime", (Minecraft.getSystemTime() % 800000L) / 800000.0F);
            shader.set1i("PortalLayers", 15);

            ShaderProgram.texUnit(GL13.GL_TEXTURE0);
            mc.getTextureManager().bindTexture(skyTexture);
            shader.set1i("Sampler0", 0);

            ShaderProgram.texUnit(GL13.GL_TEXTURE1);
            mc.getTextureManager().bindTexture(portalTexture);
            shader.set1i("Sampler1", 1);

            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buf = tess.getBuffer();
            buf.begin(7, DefaultVertexFormats.POSITION_COLOR);

            for (EnumFacing facing : EnumFacing.values()) {
                renderFace(buf, facing, minX, minY, minZ, maxX, maxY, maxZ);
            }
            tess.draw();
        } finally {
            shader.stop();
            ShaderProgram.texUnit(GL13.GL_TEXTURE1);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
            ShaderProgram.texUnit(prevTexUnit);

            if (!prevBlend) {
                GlStateManager.disableBlend();
            } else {
                GlStateManager.enableBlend();
                GL11.glBlendFunc(prevBlendSrc, prevBlendDst);
            }

            GL20.glUseProgram(prevProgram);

            if (prevLight) GlStateManager.enableLighting();
            else GlStateManager.disableLighting();

            GlStateManager.depthMask(prevDepthMask);
            if (prevCull) GlStateManager.enableCull();
            else GlStateManager.disableCull();
        }
    }

    private float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    private void renderFace(BufferBuilder buffer, EnumFacing face,
                            float minX, float minY, float minZ,
                            float maxX, float maxY, float maxZ) {
        float r = 1.0f, g = 1.0f, b = 1.0f, a = 1.0f;
        switch (face) {
            case SOUTH:
                buffer.pos(minX, minY, maxZ).color(r, g, b, a).endVertex();
                buffer.pos(maxX, minY, maxZ).color(r, g, b, a).endVertex();
                buffer.pos(maxX, maxY, maxZ).color(r, g, b, a).endVertex();
                buffer.pos(minX, maxY, maxZ).color(r, g, b, a).endVertex();
                break;
            case NORTH:
                buffer.pos(minX, maxY, minZ).color(r, g, b, a).endVertex();
                buffer.pos(maxX, maxY, minZ).color(r, g, b, a).endVertex();
                buffer.pos(maxX, minY, minZ).color(r, g, b, a).endVertex();
                buffer.pos(minX, minY, minZ).color(r, g, b, a).endVertex();
                break;
            case EAST:
                buffer.pos(maxX, maxY, minZ).color(r, g, b, a).endVertex();
                buffer.pos(maxX, maxY, maxZ).color(r, g, b, a).endVertex();
                buffer.pos(maxX, minY, maxZ).color(r, g, b, a).endVertex();
                buffer.pos(maxX, minY, minZ).color(r, g, b, a).endVertex();
                break;
            case WEST:
                buffer.pos(minX, minY, minZ).color(r, g, b, a).endVertex();
                buffer.pos(minX, minY, maxZ).color(r, g, b, a).endVertex();
                buffer.pos(minX, maxY, maxZ).color(r, g, b, a).endVertex();
                buffer.pos(minX, maxY, minZ).color(r, g, b, a).endVertex();
                break;
            case DOWN:
                buffer.pos(minX, minY, minZ).color(r, g, b, a).endVertex();
                buffer.pos(maxX, minY, minZ).color(r, g, b, a).endVertex();
                buffer.pos(maxX, minY, maxZ).color(r, g, b, a).endVertex();
                buffer.pos(minX, minY, maxZ).color(r, g, b, a).endVertex();
                break;
            case UP:
                buffer.pos(minX, maxY, maxZ).color(r, g, b, a).endVertex();
                buffer.pos(maxX, maxY, maxZ).color(r, g, b, a).endVertex();
                buffer.pos(maxX, maxY, minZ).color(r, g, b, a).endVertex();
                buffer.pos(minX, maxY, minZ).color(r, g, b, a).endVertex();
                break;
        }
    }

}