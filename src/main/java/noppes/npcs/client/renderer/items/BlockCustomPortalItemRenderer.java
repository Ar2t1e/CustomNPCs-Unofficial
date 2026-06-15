package noppes.npcs.client.renderer.items;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.blocks.custom.CustomBlockPortal;
import noppes.npcs.client.renderer.blocks.BlockPortalRenderer;
import noppes.npcs.items.ItemNpcBlock;
import org.joml.Matrix4f;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class BlockCustomPortalItemRenderer extends BlockEntityWithoutLevelRenderer {

    private static final float[][] STATES = {
            {0.0f, 0.0f, 0.25f, 1.0f, 1.0f, 0.65f},
            {0.35f, 0.0f, 0.0f, 0.65f, 1.0f, 1.0f},
            {0.0f, 0.35f, 0.0f, 1.0f, 0.65f, 1.0f},
    };
    private static final long STATE_MS = 2000L;
    private static final long CYCLE_MS = 3L * STATE_MS;

    public BlockCustomPortalItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, @Nonnull ItemDisplayContext displayContext, @Nonnull PoseStack pose,
                             @Nonnull MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (!(stack.getItem() instanceof ItemNpcBlock itemBlock) ||
                !(itemBlock.getBlock() instanceof CustomBlockPortal portal)) {
            return;
        }

        ResourceLocation sky = new ResourceLocation(CustomNpcs.MODID, "textures/environment/" + portal.getCustomName() + "_sky.png");
        ResourceLocation texture = new ResourceLocation(CustomNpcs.MODID, "textures/entity/" + portal.getCustomName() + "_portal.png");
        VertexConsumer consumer = buffer.getBuffer(BlockPortalRenderer.getRenderType(portal.getCustomName(), sky, texture));
        Matrix4f matrix = pose.last().pose();

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

        renderFace(matrix, consumer, Direction.SOUTH,  minX, minY, minZ, maxX, maxY, maxZ, packedLight, packedOverlay);
        renderFace(matrix, consumer, Direction.NORTH,  minX, minY, minZ, maxX, maxY, maxZ, packedLight, packedOverlay);
        renderFace(matrix, consumer, Direction.EAST,   minX, minY, minZ, maxX, maxY, maxZ, packedLight, packedOverlay);
        renderFace(matrix, consumer, Direction.WEST,   minX, minY, minZ, maxX, maxY, maxZ, packedLight, packedOverlay);
        renderFace(matrix, consumer, Direction.UP,     minX, minY, minZ, maxX, maxY, maxZ, packedLight, packedOverlay);
        renderFace(matrix, consumer, Direction.DOWN,   minX, minY, minZ, maxX, maxY, maxZ, packedLight, packedOverlay);
    }

    private float lerp(float a, float b, float t) { return a + (b - a) * t; }

    private void renderFace(Matrix4f matrix, VertexConsumer consumer, Direction face,
                            float minX, float minY, float minZ, float maxX, float maxY, float maxZ,
                            int light, int overlay) {
        float r = 1.0F, g = 1.0F, b = 1.0F, a = 1.0F;
        switch (face) {
            case SOUTH -> {
                BlockPortalRenderer.vertex(consumer, matrix, minX, minY, maxZ, r, g, b, a, 0, 0, light, overlay);
                BlockPortalRenderer.vertex(consumer, matrix, maxX, minY, maxZ, r, g, b, a, 1, 0, light, overlay);
                BlockPortalRenderer.vertex(consumer, matrix, maxX, maxY, maxZ, r, g, b, a, 1, 1, light, overlay);
                BlockPortalRenderer.vertex(consumer, matrix, minX, maxY, maxZ, r, g, b, a, 0, 1, light, overlay);
            }
            case NORTH -> {
                BlockPortalRenderer.vertex(consumer, matrix, minX, maxY, minZ, r, g, b, a, 0, 1, light, overlay);
                BlockPortalRenderer.vertex(consumer, matrix, maxX, maxY, minZ, r, g, b, a, 1, 1, light, overlay);
                BlockPortalRenderer.vertex(consumer, matrix, maxX, minY, minZ, r, g, b, a, 1, 0, light, overlay);
                BlockPortalRenderer.vertex(consumer, matrix, minX, minY, minZ, r, g, b, a, 0, 0, light, overlay);
            }
            case EAST -> {
                BlockPortalRenderer.vertex(consumer, matrix, maxX, maxY, minZ, r, g, b, a, 0, 0, light, overlay);
                BlockPortalRenderer.vertex(consumer, matrix, maxX, maxY, maxZ, r, g, b, a, 1, 0, light, overlay);
                BlockPortalRenderer.vertex(consumer, matrix, maxX, minY, maxZ, r, g, b, a, 1, 1, light, overlay);
                BlockPortalRenderer.vertex(consumer, matrix, maxX, minY, minZ, r, g, b, a, 0, 1, light, overlay);
            }
            case WEST -> {
                BlockPortalRenderer.vertex(consumer, matrix, minX, minY, minZ, r, g, b, a, 0, 1, light, overlay);
                BlockPortalRenderer.vertex(consumer, matrix, minX, minY, maxZ, r, g, b, a, 1, 1, light, overlay);
                BlockPortalRenderer.vertex(consumer, matrix, minX, maxY, maxZ, r, g, b, a, 1, 0, light, overlay);
                BlockPortalRenderer.vertex(consumer, matrix, minX, maxY, minZ, r, g, b, a, 0, 0, light, overlay);
            }
            case UP -> {
                BlockPortalRenderer.vertex(consumer, matrix, minX, maxY, maxZ, r, g, b, a, 0, 1, light, overlay);
                BlockPortalRenderer.vertex(consumer, matrix, maxX, maxY, maxZ, r, g, b, a, 1, 1, light, overlay);
                BlockPortalRenderer.vertex(consumer, matrix, maxX, maxY, minZ, r, g, b, a, 1, 0, light, overlay);
                BlockPortalRenderer.vertex(consumer, matrix, minX, maxY, minZ, r, g, b, a, 0, 0, light, overlay);
            }
            case DOWN -> {
                BlockPortalRenderer.vertex(consumer, matrix, minX, minY, minZ, r, g, b, a, 0, 0, light, overlay);
                BlockPortalRenderer.vertex(consumer, matrix, maxX, minY, minZ, r, g, b, a, 1, 0, light, overlay);
                BlockPortalRenderer.vertex(consumer, matrix, maxX, minY, maxZ, r, g, b, a, 1, 1, light, overlay);
                BlockPortalRenderer.vertex(consumer, matrix, minX, minY, maxZ, r, g, b, a, 0, 1, light, overlay);
            }
        }
    }

}