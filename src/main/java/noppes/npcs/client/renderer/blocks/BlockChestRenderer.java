package noppes.npcs.client.renderer.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BrightnessCombiner;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import noppes.npcs.blocks.custom.CustomChest;
import noppes.npcs.blocks.custom.tiles.CustomTileEntityChest;

import javax.annotation.Nonnull;

// ChestRenderer
public class BlockChestRenderer<T extends CustomTileEntityChest> extends BlockRendererInterface<T> {

    private final ModelPart lid;
    private final ModelPart bottom;
    private final ModelPart lock;

    public BlockChestRenderer(BlockEntityRendererProvider.Context dispatcher) {
        super(dispatcher);

        ModelPart modelpart = dispatcher.bakeLayer(ModelLayers.CHEST);
        bottom = modelpart.getChild("bottom");
        lid = modelpart.getChild("lid");
        lock = modelpart.getChild("lock");
    }

    @Override
    public void render(@Nonnull T te, float partialTicks, @Nonnull PoseStack pose, @Nonnull MultiBufferSource buffer, int packedLight, int packedOverlay) {
        Level level = te.getLevel();
        boolean hasLevel = level != null;
        BlockState blockstate = hasLevel ? te.getBlockState() : te.block.defaultBlockState().setValue(CustomChest.FACING, Direction.SOUTH);

        Block block = blockstate.getBlock();
        if (block instanceof AbstractChestBlock<?>) {
            pose.pushPose();
            float f = blockstate.getValue(CustomChest.FACING).toYRot();
            pose.translate(0.5F, 0.5F, 0.5F);
            pose.mulPose(Axis.YP.rotationDegrees(-f));
            pose.translate(-0.5F, -0.5F, -0.5F);

            float f1 = te.getOpenNess(partialTicks);
            f1 = 1.0F - f1;
            f1 = 1.0F - f1 * f1 * f1;
            VertexConsumer vertexconsumer = Sheets.CHEST_LOCATION.buffer(buffer, RenderType::entityCutout);

            lid.xRot = -(f1 * ((float) Math.PI / 2F));
            lock.xRot = lid.xRot;
            lid.render(pose, vertexconsumer, packedLight, packedOverlay);
            lock.render(pose, vertexconsumer, packedLight, packedOverlay);
            bottom.render(pose, vertexconsumer, packedLight, packedOverlay);

            pose.popPose();
        }
    }

}
