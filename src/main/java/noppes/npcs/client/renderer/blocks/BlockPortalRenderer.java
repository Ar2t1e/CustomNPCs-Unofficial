package noppes.npcs.client.renderer.blocks;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.blocks.custom.tiles.CustomTileEntityPortal;
import noppes.npcs.shared.common.util.LogWriter;
import org.joml.Matrix4f;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

// TheEndPortalRenderer
@OnlyIn(Dist.CLIENT)
public class BlockPortalRenderer<T extends CustomTileEntityPortal> extends BlockRendererInterface<T> {

    protected static final Map<String, RenderType> cash = new HashMap<>();

    public BlockPortalRenderer(BlockEntityRendererProvider.Context dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(@Nonnull T te, float partialTicks, @Nonnull PoseStack pose, @Nonnull MultiBufferSource buffer, int packedLight, int packedOverlay) {
        Matrix4f matrix = pose.last().pose();
        VertexConsumer consumer = buffer.getBuffer(getRenderType(te));
        renderFaces(te, matrix, consumer, Direction.SOUTH, packedLight, packedOverlay);
        renderFaces(te, matrix, consumer, Direction.NORTH, packedLight, packedOverlay);
        renderFaces(te, matrix, consumer, Direction.EAST, packedLight, packedOverlay);
        renderFaces(te, matrix, consumer, Direction.WEST, packedLight, packedOverlay);
        renderFaces(te, matrix, consumer, Direction.DOWN, packedLight, packedOverlay);
        renderFaces(te, matrix, consumer, Direction.UP, packedLight, packedOverlay);
    }

    protected void renderFaces(T te, Matrix4f matrix, VertexConsumer consumer, Direction face, int light, int overlay) {
        if (te.shouldRenderFace(face)) {
            if (face == Direction.SOUTH) {
                consumer.vertex(matrix, 0.0f, 0.0f, 0.75F)
                        .color(1.0F, 1.0F, 1.0F, 0.75F).uv(0, 0).uv2(light, overlay)
                        .normal(0, 0, 1).endVertex();
                consumer.vertex(matrix, 1.0F, 0.0f, 0.75F)
                        .color(1.0F, 1.0F, 1.0F, 0.75F).uv(1, 0).uv2(light, overlay).normal(0, 0, 1).endVertex();
                consumer.vertex(matrix, 1.0F, 1.0F, 0.75F)
                        .color(1.0F, 1.0F, 1.0F, 0.75F).uv(1, 1).uv2(light, overlay).normal(0, 0, 1).endVertex();
                consumer.vertex(matrix, 0.0f, 1.0F, 0.75F)
                        .color(1.0F, 1.0F, 1.0F, 0.75F).uv(0, 1).uv2(light, overlay).normal(0, 0, 1).endVertex();
            }
            if (face == Direction.NORTH) {
                consumer.vertex(matrix, 0.0f, 1.0F, 0.375F)
                        .color(1.0F, 1.0F, 1.0F, 0.75F).uv(0, 1).uv2(light, overlay).normal(0, 0, -1).endVertex();
                consumer.vertex(matrix, 1.0F, 1.0F, 0.375F)
                        .color(1.0F, 1.0F, 1.0F, 0.75F).uv(1, 1).uv2(light, overlay).normal(0, 0, -1).endVertex();
                consumer.vertex(matrix, 1.0F, 0.0f, 0.375F)
                        .color(1.0F, 1.0F, 1.0F, 0.75F).uv(1, 0).uv2(light, overlay).normal(0, 0, -1).endVertex();
                consumer.vertex(matrix, 0.0f, 0.0f, 0.375F)
                        .color(1.0F, 1.0F, 1.0F, 0.75F).uv(0, 0).uv2(light, overlay).normal(0, 0, -1).endVertex();
            }
            if (face == Direction.EAST) {
                consumer.vertex(matrix, 0.75F, 1.0F, 0.0f)
                        .color(1.0F, 1.0F, 1.0F, 0.75F).uv(0, 0).uv2(light, overlay).normal(1, 0, 0).endVertex();
                consumer.vertex(matrix, 0.75F, 1.0F, 1.0F)
                        .color(1.0F, 1.0F, 1.0F, 0.75F).uv(1, 0).uv2(light, overlay).normal(1, 0, 0).endVertex();
                consumer.vertex(matrix, 0.75F, 0.0f, 1.0F)
                        .color(1.0F, 1.0F, 1.0F, 0.75F).uv(1, 1).uv2(light, overlay).normal(1, 0, 0).endVertex();
                consumer.vertex(matrix, 0.75F, 0.0f, 0.0f)
                        .color(1.0F, 1.0F, 1.0F, 0.75F).uv(0, 1).uv2(light, overlay).normal(1, 0, 0).endVertex();
            }
            if (face == Direction.WEST) {
                consumer.vertex(matrix, 0.375F, 0.0f, 0.0f)
                        .color(1.0F, 1.0F, 1.0F, 0.75F).uv(0, 1).uv2(light, overlay).normal(-1, 0, 0).endVertex();
                consumer.vertex(matrix, 0.375F, 0.0f, 1.0F)
                        .color(1.0F, 1.0F, 1.0F, 0.75F).uv(1, 1).uv2(light, overlay).normal(-1, 0, 0).endVertex();
                consumer.vertex(matrix, 0.375F, 1.0F, 1.0F)
                        .color(1.0F, 1.0F, 1.0F, 0.75F).uv(1, 0).uv2(light, overlay).normal(-1, 0, 0).endVertex();
                consumer.vertex(matrix, 0.375F, 1.0F, 0.0f)
                        .color(1.0F, 1.0F, 1.0F, 0.75F).uv(0, 0).uv2(light, overlay).normal(-1, 0, 0).endVertex();
            }
            if (face == Direction.DOWN) {
                consumer.vertex(matrix, 0.0f, 0.375F, 0.0f)
                        .color(1.0F, 1.0F, 1.0F, 0.75F).uv(0, 0).uv2(light, overlay).normal(0, -1, 0).endVertex();
                consumer.vertex(matrix, 1.0F, 0.375F, 0.0f)
                        .color(1.0F, 1.0F, 1.0F, 0.75F).uv(1, 0).uv2(light, overlay).normal(0, -1, 0).endVertex();
                consumer.vertex(matrix, 1.0F, 0.375F, 1.0F)
                        .color(1.0F, 1.0F, 1.0F, 0.75F).uv(1, 1).uv2(light, overlay).normal(0, -1, 0).endVertex();
                consumer.vertex(matrix, 0.0f, 0.375F, 1.0F)
                        .color(1.0F, 1.0F, 1.0F, 0.75F).uv(0, 1).uv2(light, overlay).normal(0, -1, 0).endVertex();
            }
            if (face == Direction.UP) {
                consumer.vertex(matrix, 0.0f, 0.75F, 1.0F)
                        .color(1.0F, 1.0F, 1.0F, 0.75F).uv(0, 1).uv2(light, overlay).normal(0, 1, 0).endVertex();
                consumer.vertex(matrix, 1.0F, 0.75F, 1.0F)
                        .color(1.0F, 1.0F, 1.0F, 0.75F).uv(1, 1).uv2(light, overlay).normal(0, 1, 0).endVertex();
                consumer.vertex(matrix, 1.0F, 0.75F, 0.0f)
                        .color(1.0F, 1.0F, 1.0F, 0.75F).uv(1, 0).uv2(light, overlay).normal(0, 1, 0).endVertex();
                consumer.vertex(matrix, 0.0f, 0.75F, 0.0f)
                        .color(1.0F, 1.0F, 1.0F, 0.75F).uv(0, 0).uv2(light, overlay).normal(0, 1, 0).endVertex();
            }
        }
    }

    protected RenderType getRenderType(T te) {
        ResourceLocation sky = te.getSkyTexture();
        ResourceLocation texture = te.getPortalTexture();
        String key = sky + "_" + texture;
        if (!cash.containsKey(key)) {
            cash.put(key, RenderType.create(key,
                    DefaultVertexFormat.POSITION,
                    VertexFormat.Mode.QUADS,
                    256, false, false,
                    RenderType.CompositeState.builder().setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeEndPortalShader))
                            .setTextureState(RenderStateShard.MultiTextureStateShard.builder()
                                    .add(sky, false, false)
                                    .add(texture, false, false).build())
                            .createCompositeState(false)));
        }
        return cash.getOrDefault(key, RenderType.endPortal());
    }

}
