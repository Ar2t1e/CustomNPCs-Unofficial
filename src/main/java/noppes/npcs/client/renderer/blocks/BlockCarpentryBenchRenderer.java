package noppes.npcs.client.renderer.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import noppes.npcs.blocks.BlockCarpentryBench;
import noppes.npcs.blocks.tiles.TileBlockAnvil;
import noppes.npcs.client.model.blocks.ModelCarpentryBench;

public class BlockCarpentryBenchRenderer implements BlockEntityRenderer<TileBlockAnvil> {

	private final ModelCarpentryBench model = new ModelCarpentryBench();
	private static final ResourceLocation TEXTURE = new ResourceLocation("customnpcs", "textures/models/carpentrybench.png");
	private static final RenderType type = RenderType.entityCutout(TEXTURE);

	public BlockCarpentryBenchRenderer(BlockEntityRendererProvider.Context dispatcher) {
		
	}

	@Override
	public void render(TileBlockAnvil te, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int light, int overlay) {
		int rotation = 0;
		if(te.getBlockPos() != BlockPos.ZERO){
			rotation = te.getBlockState().getValue(BlockCarpentryBench.ROTATION);
		}
		matrixStack.pushPose();
		RenderSystem.disableBlend();
		//RenderSystem.enableLighting();
		matrixStack.translate((float) 0.5f, (float) 1.4f, (float) 0.5f);
		matrixStack.scale(0.95f, 0.95f, 0.95f);
		matrixStack.mulPose(Axis.ZP.rotationDegrees(180));
		matrixStack.mulPose(Axis.YP.rotationDegrees(90 * rotation));
		model.renderToBuffer(matrixStack, buffer.getBuffer(type), light, overlay, 1, 1, 1, 1);
		matrixStack.popPose();
	}
}
