package noppes.npcs.client.renderer.blocks;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.blocks.custom.CustomBlockPortal;
import noppes.npcs.client.ClientRegisterEvents;
import noppes.npcs.blocks.custom.tiles.CustomTileEntityPortal;
import noppes.npcs.client.util.ShaderData;
import org.joml.Matrix4f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class BlockPortalRenderer<T extends CustomTileEntityPortal> extends BlockRendererInterface<T> {

    protected static final Map<String, RenderType> cash = new HashMap<>();

    public BlockPortalRenderer(BlockEntityRendererProvider.Context dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(@Nullable T te, float partialTicks, @Nonnull PoseStack pose, @Nonnull MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (te != null && te.getBlockState().getBlock() instanceof CustomBlockPortal portal) {
            RenderType renderType = getRenderType(portal.getCustomName(), te.getSkyTexture(), te.getPortalTexture());
            VertexConsumer consumer = buffer.getBuffer(renderType);
            Matrix4f matrix = pose.last().pose();
            renderFaces(te, matrix, consumer, Direction.SOUTH, packedLight, packedOverlay);
            renderFaces(te, matrix, consumer, Direction.NORTH, packedLight, packedOverlay);
            renderFaces(te, matrix, consumer, Direction.EAST, packedLight, packedOverlay);
            renderFaces(te, matrix, consumer, Direction.WEST, packedLight, packedOverlay);
            renderFaces(te, matrix, consumer, Direction.DOWN, packedLight, packedOverlay);
            renderFaces(te, matrix, consumer, Direction.UP, packedLight, packedOverlay);
        }
    }

    protected void renderFaces(T te, Matrix4f matrix, VertexConsumer consumer, Direction face, int light, int overlay) {
        if (te.shouldRenderFace(face)) {
            float r = 1.0F, g = 1.0F, b = 1.0F, a = te.getAlpha();
            switch (face) {
                case SOUTH -> {
                    vertex(consumer, matrix, 0.0f, 0.0f, 0.75F, r, g, b, a, 0, 0, light, overlay);
                    vertex(consumer, matrix, 1.0F, 0.0f, 0.75F, r, g, b, a, 1, 0, light, overlay);
                    vertex(consumer, matrix, 1.0F, 1.0F, 0.75F, r, g, b, a, 1, 1, light, overlay);
                    vertex(consumer, matrix, 0.0f, 1.0F, 0.75F, r, g, b, a, 0, 1, light, overlay);
                }
                case NORTH -> {
                    vertex(consumer, matrix, 0.0f, 1.0F, 0.375F, r, g, b, a, 0, 1, light, overlay);
                    vertex(consumer, matrix, 1.0F, 1.0F, 0.375F, r, g, b, a, 1, 1, light, overlay);
                    vertex(consumer, matrix, 1.0F, 0.0f, 0.375F, r, g, b, a, 1, 0, light, overlay);
                    vertex(consumer, matrix, 0.0f, 0.0f, 0.375F, r, g, b, a, 0, 0, light, overlay);
                }
                case EAST -> {
                    vertex(consumer, matrix, 0.75F, 1.0F, 0.0f, r, g, b, a, 0, 0, light, overlay);
                    vertex(consumer, matrix, 0.75F, 1.0F, 1.0F, r, g, b, a, 1, 0, light, overlay);
                    vertex(consumer, matrix, 0.75F, 0.0f, 1.0F, r, g, b, a, 1, 1, light, overlay);
                    vertex(consumer, matrix, 0.75F, 0.0f, 0.0f, r, g, b, a, 0, 1, light, overlay);
                }
                case WEST -> {
                    vertex(consumer, matrix, 0.375F, 0.0f, 0.0f, r, g, b, a, 0, 1, light, overlay);
                    vertex(consumer, matrix, 0.375F, 0.0f, 1.0F, r, g, b, a, 1, 1, light, overlay);
                    vertex(consumer, matrix, 0.375F, 1.0F, 1.0F, r, g, b, a, 1, 0, light, overlay);
                    vertex(consumer, matrix, 0.375F, 1.0F, 0.0f, r, g, b, a, 0, 0, light, overlay);
                }
                case UP -> {
                    vertex(consumer, matrix, 0.0f, 0.75F, 1.0F, r, g, b, a, 0, 1, light, overlay);
                    vertex(consumer, matrix, 1.0F, 0.75F, 1.0F, r, g, b, a, 1, 1, light, overlay);
                    vertex(consumer, matrix, 1.0F, 0.75F, 0.0f, r, g, b, a, 1, 0, light, overlay);
                    vertex(consumer, matrix, 0.0f, 0.75F, 0.0f, r, g, b, a, 0, 0, light, overlay);
                }
                case DOWN -> {
                    vertex(consumer, matrix, 0.0f, 0.375F, 0.0f, r, g, b, a, 0, 0, light, overlay);
                    vertex(consumer, matrix, 1.0F, 0.375F, 0.0f, r, g, b, a, 1, 0, light, overlay);
                    vertex(consumer, matrix, 1.0F, 0.375F, 1.0F, r, g, b, a, 1, 1, light, overlay);
                    vertex(consumer, matrix, 0.0f, 0.375F, 1.0F, r, g, b, a, 0, 1, light, overlay);
                }
            }
        }
    }

    public static void vertex(VertexConsumer consumer, Matrix4f matrix, float x, float y, float z,
                        float r, float g, float b, float a, int u, int v,
                              int light, int overlay) {
        consumer.vertex(matrix, x, y, z)
                .color(r, g, b, a)
                .uv(u, v)
                .uv2(light, overlay)
                .endVertex();
    }

    public static RenderType getRenderType(String name, ResourceLocation sky, ResourceLocation portal) {
        String key = name + "_" + sky + "_" + portal;
        return cash.computeIfAbsent(key, k -> CustomPortalRenderType.create(name, sky, portal));
    }

    private static class CustomPortalRenderType extends RenderType {

        private CustomPortalRenderType(String name, VertexFormat format, VertexFormat.Mode mode,
                                       int bufferSize, boolean affectsCrumbling, boolean sortOnUpload,
                                       Runnable setupState, Runnable clearState) {
            super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
        }

        static RenderType create(String name, ResourceLocation sky, ResourceLocation texture) {
            Supplier<ShaderInstance> shaderSupplier = GameRenderer::getRendertypeEndPortalShader;
            VertexFormat vertexFormat = DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP;
            ShaderData shaderData = ClientRegisterEvents.getShader(new ResourceLocation(CustomNpcs.MODID, name));
            if (shaderData != null) {
                shaderSupplier = () -> shaderData.shader;
                vertexFormat = shaderData.format;
            }
            return create(name + "_" + sky.getPath() + "_" + texture.getPath(),
                    vertexFormat,
                    VertexFormat.Mode.QUADS, 256, false, false,
                    RenderType.CompositeState.builder()
                            .setShaderState(new RenderStateShard.ShaderStateShard(shaderSupplier))
                            .setTextureState(RenderStateShard.MultiTextureStateShard.builder()
                                    .add(sky, false, false)
                                    .add(texture, false, false)
                                    .build())
                            .setTransparencyState(TransparencyStateShard.TRANSLUCENT_TRANSPARENCY)
                            .setLightmapState(new RenderStateShard.LightmapStateShard(true))
                            .createCompositeState(false));
        }
    }

}