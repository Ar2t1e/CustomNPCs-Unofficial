package noppes.npcs.mixin.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import noppes.npcs.api.mixin.world.level.block.entity.ITileEntityBanner;
import noppes.npcs.client.model.custom.CustomCube;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.data.Faction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(value = BannerRenderer.class, priority = 498)
public class BannerRendererMixin {

    @Final @Shadow private ModelPart pole;
    @Final @Shadow private ModelPart bar;

    @Unique private static final ModelPart npcs$faction_flag = new ModelPart(Collections.singletonList(CustomCube.createBannerFlag()), new HashMap<>());

    /**
     * @author BetaZavr
     * @reason Added faction ID for flag display
     */
    @Inject(
            at = @At("HEAD"),
            method = "render(Lnet/minecraft/world/level/block/entity/BannerBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
            cancellable = true
    )
    public void npcS$render(BannerBlockEntity tile, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, CallbackInfo ci) {
        if (tile instanceof ITileEntityBanner mix) {
            ResourceLocation resource = mix.npcs$getResourceFlag();
            if (resource == null) {
                Faction faction = FactionController.instance.getFaction(mix.npcs$getFactionId());
                if (faction != null) { resource = faction.flag; }
            }
            if (resource != null) {
                ci.cancel();
                boolean isEmptyLevel = tile.getLevel() == null;
                poseStack.pushPose();
                long i;
                if (isEmptyLevel) {
                    i = 0L;
                    poseStack.translate(0.5F, 0.5F, 0.5F);
                    pole.visible = true;
                }
                else {
                    i = tile.getLevel().getGameTime();
                    BlockState blockstate = tile.getBlockState();
                    if (blockstate.getBlock() instanceof BannerBlock) {
                        poseStack.translate(0.5F, 0.5F, 0.5F);
                        float f1 = -RotationSegment.convertToDegrees(blockstate.getValue(BannerBlock.ROTATION));
                        poseStack.mulPose(Axis.YP.rotationDegrees(f1));
                        pole.visible = true;
                    }
                    else {
                        poseStack.translate(0.5F, -0.16666667F, 0.5F);
                        float f3 = -blockstate.getValue(WallBannerBlock.FACING).toYRot();
                        poseStack.mulPose(Axis.YP.rotationDegrees(f3));
                        poseStack.translate(0.0F, -0.3125F, -0.4375F);
                        pole.visible = false;
                    }
                }
                poseStack.pushPose();
                poseStack.scale(0.6666667f, -0.6666667f, -0.6666667f);
                VertexConsumer consumer = ModelBakery.BANNER_BASE.buffer(buffer, RenderType::entitySolid);
                pole.render(poseStack, consumer, packedLight, packedOverlay);
                bar.render(poseStack, consumer, packedLight, packedOverlay);
                BlockPos blockpos = tile.getBlockPos();
                float f2 = ((float) Math.floorMod(blockpos.getX() * 7L + blockpos.getY() * 9L + blockpos.getZ() * 13L + i, 100L) + partialTicks) / 100.0F;

                npcs$faction_flag.xRot = (-0.0125F + 0.01F * Mth.cos(((float) Math.PI * 2F) * f2)) * (float)Math.PI;
                npcs$faction_flag.y = -32.0F;
                npcs$faction_flag.render(poseStack, buffer.getBuffer(RenderType.entityCutout(resource)), packedLight, packedOverlay);

                poseStack.popPose();
                poseStack.popPose();
            }
        }
    }

}
