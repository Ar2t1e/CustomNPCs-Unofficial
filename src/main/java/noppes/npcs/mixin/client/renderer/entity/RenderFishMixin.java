package noppes.npcs.mixin.client.renderer.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderFish;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import noppes.npcs.items.custom.CustomFishingRod;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(value = RenderFish.class, priority = 498)
public abstract class RenderFishMixin extends Render<EntityFishHook> {

    @Final @Shadow private static ResourceLocation FISH_PARTICLES;

    protected RenderFishMixin(RenderManager renderManager) {
        super(renderManager);
    }

    @Inject(method = "doRender(Lnet/minecraft/entity/projectile/EntityFishHook;DDDFF)V",
            at = @At("HEAD"),
            cancellable = true)
    @SuppressWarnings("ConstantConditions")
    private void npcs$doRender(EntityFishHook entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        CustomFishingRod item = npcs$getCustomFishingRodData(entity);
        if (item != null && (item.getFishingLineColor() != 0 || item.getFishingHookTexture() != null)) {
            EntityPlayer player = entity.getAngler();
            if (player != null) {
                ci.cancel();

                GlStateManager.pushMatrix();
                GlStateManager.translate((float)x, (float)y, (float)z);
                GlStateManager.enableRescaleNormal();
                GlStateManager.scale(0.5F, 0.5F, 0.5F);
                GlStateManager.rotate(180.0F - renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate((renderManager.options.thirdPersonView == 2 ? -1.0f : 1.0f) * -renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder buffer = tessellator.getBuffer();

                buffer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);

                if (item.getFishingHookTexture() != null) {
                    bindTexture(item.getFishingHookTexture());
                    buffer.pos(-0.5D, -0.5D, 0.0D).tex(0.0D, 1.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
                    buffer.pos(0.5D, -0.5D, 0.0D).tex(1.0D, 1.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
                    buffer.pos(0.5D, 0.5D, 0.0D).tex(1.0D, 0.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
                    buffer.pos(-0.5D, 0.5D, 0.0D).tex(0.0D, 0.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
                } else {
                    bindTexture(FISH_PARTICLES);
                    buffer.pos(-0.5D, -0.5D, 0.0D).tex(0.0625D, 0.1875D).normal(0.0F, 1.0F, 0.0F).endVertex();
                    buffer.pos(0.5D, -0.5D, 0.0D).tex(0.125D, 0.1875D).normal(0.0F, 1.0F, 0.0F).endVertex();
                    buffer.pos(0.5D, 0.5D, 0.0D).tex(0.125D, 0.125D).normal(0.0F, 1.0F, 0.0F).endVertex();
                    buffer.pos(-0.5D, 0.5D, 0.0D).tex(0.0625D, 0.125D).normal(0.0F, 1.0F, 0.0F).endVertex();
                }
                tessellator.draw();

                GlStateManager.disableRescaleNormal();
                GlStateManager.popMatrix();

                int k = player.getPrimaryHand() == EnumHandSide.RIGHT ? 1 : -1;
                ItemStack itemstack = player.getHeldItemMainhand();
                if (!(itemstack.getItem() instanceof ItemFishingRod)) {
                    k = -k;
                }

                float f7 = player.getSwingProgress(partialTicks);
                float f8 = MathHelper.sin(MathHelper.sqrt(f7) * (float)Math.PI);
                float f9 = (player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * partialTicks) * 0.017453292F;
                double d0 = MathHelper.sin(f9);
                double d1 = MathHelper.cos(f9);
                double d2 = (double)k * 0.35D;
                double d3 = 0.8D;
                double d4;
                double d5;
                double d6;
                double d7;

                if ((renderManager.options == null || renderManager.options.thirdPersonView <= 0) && player == Minecraft.getMinecraft().player) {
                    float f10 = renderManager.options.fovSetting;
                    f10 = f10 / 100.0F;
                    Vec3d vec3d = new Vec3d((double)k * -0.36D * (double)f10, -0.045D * (double)f10, 0.4D);
                    vec3d = vec3d.rotatePitch(-(player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks) * 0.017453292F);
                    vec3d = vec3d.rotateYaw(-(player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * partialTicks) * 0.017453292F);
                    vec3d = vec3d.rotateYaw(f8 * 0.5F);
                    vec3d = vec3d.rotatePitch(-f8 * 0.7F);
                    d4 = player.prevPosX + (player.posX - player.prevPosX) * (double)partialTicks + vec3d.x;
                    d5 = player.prevPosY + (player.posY - player.prevPosY) * (double)partialTicks + vec3d.y;
                    d6 = player.prevPosZ + (player.posZ - player.prevPosZ) * (double)partialTicks + vec3d.z;
                    d7 = player.getEyeHeight();
                } else {
                    d4 = player.prevPosX + (player.posX - player.prevPosX) * (double)partialTicks - d1 * d2 - d0 * d3;
                    d5 = player.prevPosY + (double)player.getEyeHeight() + (player.posY - player.prevPosY) * (double)partialTicks - 0.45D;
                    d6 = player.prevPosZ + (player.posZ - player.prevPosZ) * (double)partialTicks - d0 * d2 + d1 * d3;
                    d7 = player.isSneaking() ? -0.1875D : 0.0D;
                }

                double d13 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double)partialTicks;
                double d8 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double)partialTicks + 0.25D;
                double d9 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double)partialTicks;
                double d10 = (float)(d4 - d13);
                double d11 = (double)((float)(d5 - d8)) + d7;
                double d12 = (float)(d6 - d9);

                GlStateManager.disableTexture2D();
                GlStateManager.disableLighting();
                buffer.begin(3, DefaultVertexFormats.POSITION_COLOR);

                int r = (item.getFishingLineColor() >> 16) & 0xFF;
                int g = (item.getFishingLineColor() >> 8) & 0xFF;
                int b = item.getFishingLineColor() & 0xFF;

                for (int i1 = 0; i1 <= 16; ++i1) {
                    npcs$customStringVertex(x, d10, y, d11,z, d12, buffer, (double) i1 / 16.0d, r, g, b);
                }

                tessellator.draw();
                GlStateManager.enableLighting();
                GlStateManager.enableTexture2D();
                super.doRender(entity, x, y, z, entityYaw, partialTicks);
            }
        }
    }

    @Inject(method = "getEntityTexture(Lnet/minecraft/entity/projectile/EntityFishHook;)Lnet/minecraft/util/ResourceLocation;",
            at = @At("HEAD"),
            cancellable = true)
    public void npcs$getEntityTexture(EntityFishHook entity, CallbackInfoReturnable<ResourceLocation> cir) {
        CustomFishingRod item = npcs$getCustomFishingRodData(entity);
        if (item != null && item.getFishingHookTexture() != null) { cir.setReturnValue(item.getFishingHookTexture()); }
    }

    @Unique
    private static void npcs$customStringVertex(double x, double nx, double y, double ny, double z, double nz, BufferBuilder buffer,
                                                double progress, int red, int green, int blue) {
        double f = x + nx * progress;
        double f1 = y + ny * (progress * progress + progress) * 0.5F + 0.25F;
        double f2 = z + nz * progress;
        buffer.pos(f, f1, f2).color(red, green, blue, 255).endVertex();
    }

    @Unique
    @SuppressWarnings("ConstantConditions")
    private @Nullable CustomFishingRod npcs$getCustomFishingRodData(EntityFishHook hook) {
        EntityPlayer player = hook.getAngler();
        if (player != null) {
            ItemStack stack;
            if (player.getHeldItemMainhand().getItem() instanceof ItemFishingRod) { stack = player.getHeldItemMainhand(); }
            else { stack = player.getHeldItemOffhand(); }
            if (stack.getItem() instanceof CustomFishingRod) { return (CustomFishingRod) stack.getItem(); }
        }
        return null;
    }

}