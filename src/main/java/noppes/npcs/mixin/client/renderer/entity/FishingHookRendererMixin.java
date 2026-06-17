package noppes.npcs.mixin.client.renderer.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.FishingHookRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import noppes.npcs.items.custom.CustomFishingRod;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@Mixin(value = FishingHookRenderer.class, priority = 498)
public class FishingHookRendererMixin {

    @Final @Shadow private static RenderType RENDER_TYPE;

    @Final @Unique private static Map<ResourceLocation, RenderType> NPCS$CUSTOM_RENDER_TYPES = new HashMap<>();

    @Inject(method = "render(Lnet/minecraft/world/entity/projectile/FishingHook;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"),
            cancellable = true)
    @SuppressWarnings("ConstantConditions")
    private void npcs$render(FishingHook hook, float partialTicks, float aimRot, PoseStack pose, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        CustomFishingRod item = npcs$getCustomFishingRodData(hook);
        if (item != null && (item.getFishingLineColor() != 0 || item.getFishingHookTexture() != null)) {
            Player player = hook.getPlayerOwner();
            if (player != null) {
                ci.cancel();
                EntityRenderDispatcher entityRenderDispatcher = ((IEntityRendererMixin) this).getEntityRenderDispatcher();
                // fishing rod float
                pose.pushPose();
                    pose.pushPose();
                    RenderSystem.enableBlend();
                    pose.scale(0.5F, 0.5F, 0.5F);
                    pose.mulPose(entityRenderDispatcher.cameraOrientation());
                    pose.mulPose(Axis.YP.rotationDegrees(180.0F));
                    PoseStack.Pose posestack$pose = pose.last();
                    Matrix4f matrix4f = posestack$pose.pose();
                    Matrix3f matrix3f = posestack$pose.normal();
                    RenderType renderType = RENDER_TYPE;
                    ResourceLocation customTexture = item.getFishingHookTexture();
                    if (customTexture != null) {
                        renderType = NPCS$CUSTOM_RENDER_TYPES.get(customTexture);
                        if (renderType == null) { NPCS$CUSTOM_RENDER_TYPES.put(customTexture, renderType = RenderType.entityTranslucent(customTexture)); }
                    }
                    VertexConsumer vertexconsumer = buffer.getBuffer(renderType);
                    vertexconsumer.vertex(matrix4f, -0.5f, -0.5f, 0.0F).color(255, 255, 255, 255).uv(0.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
                    vertexconsumer.vertex(matrix4f, 0.5f, -0.5f, 0.0F).color(255, 255, 255, 255).uv(1.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
                    vertexconsumer.vertex(matrix4f, 0.5f, 0.5f, 0.0F).color(255, 255, 255, 255).uv(1.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
                    vertexconsumer.vertex(matrix4f, -0.5f, 0.5f, 0.0F).color(255, 255, 255, 255).uv(0.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
                    RenderSystem.disableBlend();
                    pose.popPose();
                // fishing rod line
                int i = player.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
                ItemStack itemstack = player.getMainHandItem();
                if (!itemstack.canPerformAction(net.minecraftforge.common.ToolActions.FISHING_ROD_CAST)) { i = -i; }
                float f = player.getAttackAnim(aimRot);
                float f1 = Mth.sin(Mth.sqrt(f) * (float)Math.PI);
                float f2 = Mth.lerp(aimRot, player.yBodyRotO, player.yBodyRot) * ((float)Math.PI / 180F);
                double d0 = Mth.sin(f2);
                double d1 = Mth.cos(f2);
                double d2 = (double)i * 0.35D;
                double d3 = 0.8D;
                double d4;
                double d5;
                double d6;
                float f3;
                if ((entityRenderDispatcher.options == null || entityRenderDispatcher.options.getCameraType().isFirstPerson()) && player == Minecraft.getInstance().player) {
                    double d7 = 960.0D / (double) entityRenderDispatcher.options.fov().get();
                    Vec3 vec3 = entityRenderDispatcher.camera.getNearPlane().getPointOnPlane((float)i * 0.525F, -0.1F);
                    vec3 = vec3.scale(d7);
                    vec3 = vec3.yRot(f1 * 0.5F);
                    vec3 = vec3.xRot(-f1 * 0.7F);
                    d4 = Mth.lerp(aimRot, player.xo, player.getX()) + vec3.x;
                    d5 = Mth.lerp(aimRot, player.yo, player.getY()) + vec3.y;
                    d6 = Mth.lerp(aimRot, player.zo, player.getZ()) + vec3.z;
                    f3 = player.getEyeHeight();
                }
                else {
                    d4 = Mth.lerp(aimRot, player.xo, player.getX()) - d1 * d2 - d0 * d3;
                    d5 = player.yo + (double)player.getEyeHeight() + (player.getY() - player.yo) * (double)aimRot - 0.45D;
                    d6 = Mth.lerp(aimRot, player.zo, player.getZ()) - d0 * d2 + d1 * d3;
                    f3 = player.isCrouching() ? -0.1875F : 0.0F;
                }
                double d9 = Mth.lerp(aimRot, hook.xo, hook.getX());
                double d10 = Mth.lerp(aimRot, hook.yo, hook.getY()) + 0.25D;
                double d8 = Mth.lerp(aimRot, hook.zo, hook.getZ());
                float f4 = (float)(d4 - d9);
                float f5 = (float)(d5 - d10) + f3;
                float f6 = (float)(d6 - d8);
                vertexconsumer = buffer.getBuffer(RenderType.lineStrip());
                PoseStack.Pose lastPose = pose.last();
                // 16 parts
                float j = 16.0f;
                int r = FastColor.ARGB32.red(item.getFishingLineColor());
                int g = FastColor.ARGB32.green(item.getFishingLineColor());
                int b = FastColor.ARGB32.blue(item.getFishingLineColor());
                for(float k = 0; k <= j; ++k) { npcs$customStringVertex(f4, f5, f6, vertexconsumer, lastPose, k / j, (k + 1.0f) / j, r, g, b); }
                pose.popPose();
            }
        }
    }

    @Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/projectile/FishingHook;)Lnet/minecraft/resources/ResourceLocation;",
            at = @At("HEAD"),
            cancellable = true)
    public void getTextureLocation(FishingHook hook, CallbackInfoReturnable<ResourceLocation> cir) {
        CustomFishingRod item = npcs$getCustomFishingRodData(hook);
        if (item != null && item.getFishingHookTexture() != null) {
            cir.setReturnValue(item.getFishingHookTexture());
        }
    }

    @Unique
    private static void npcs$customStringVertex(float x, float y, float z, VertexConsumer vertexConsumer, PoseStack.Pose pose, float u, float v, int red, int green, int blue) {
        float f = x * u;
        float f1 = y * (u * u + u) * 0.5F + 0.25F;
        float f2 = z * u;
        float f3 = x * v - f;
        float f4 = y * (v * v + v) * 0.5F + 0.25F - f1;
        float f5 = z * v - f2;
        float f6 = Mth.sqrt(f3 * f3 + f4 * f4 + f5 * f5);
        f3 /= f6;
        f4 /= f6;
        f5 /= f6;
        vertexConsumer.vertex(pose.pose(), f, f1, f2).color(red, green, blue, 255).normal(pose.normal(), f3, f4, f5).endVertex();
    }

    @Unique
    private @Nullable CustomFishingRod npcs$getCustomFishingRodData(FishingHook hook) {
        Player player = hook.getPlayerOwner();
        if (player != null) {
            ItemStack stack;
            if (player.getMainHandItem().getItem() instanceof FishingRodItem) { stack = player.getMainHandItem(); }
            else { stack = player.getOffhandItem(); }
            if (stack.getItem() instanceof CustomFishingRod item) { return item; }
        }
        return null;
    }

}
