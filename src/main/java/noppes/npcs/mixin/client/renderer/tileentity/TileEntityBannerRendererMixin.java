package noppes.npcs.mixin.client.renderer.tileentity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBanner;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BannerTextures;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityBannerRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import noppes.npcs.api.mixin.tileentity.ITileEntityBanner;
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

@Mixin(value = TileEntityBannerRenderer.class, priority = 498)
public class TileEntityBannerRendererMixin<T extends TileEntityBanner> {

    @Final @Shadow private ModelBanner bannerModel;

    @Unique private static ModelRenderer npcs$faction_flag;

    /**
     * @author BetaZavr
     * @reason Added faction ID for flag display
     */
    @Inject(
            at = @At("HEAD"),
            method = "render(Lnet/minecraft/tileentity/TileEntityBanner;DDDFIF)V",
            cancellable = true
    )
    public void npcs$render(T tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha, CallbackInfo ci) {
        if (tile instanceof ITileEntityBanner) {
            ITileEntityBanner mix = (ITileEntityBanner) tile;
            ResourceLocation resource = mix.npcs$getResourceFlag();
            if (resource == null) {
                Faction faction = FactionController.instance.getFaction(mix.npcs$getFactionId());
                if (faction != null) {
                    resource = faction.flag;
                }
            }
            if (resource != null) {
                ci.cancel();
                if (npcs$faction_flag == null) { npcs$faction_flag = CustomCube.createBannerFlag(bannerModel); }
                boolean isStanding = !tile.hasWorld() || tile.getBlockType() == Blocks.STANDING_BANNER;
                int meta = tile.hasWorld() ? tile.getBlockMetadata() : 0;
                float ticks = tile.hasWorld() ? tile.getWorld().getTotalWorldTime() : 0.0f;
                GlStateManager.pushMatrix();

                if (isStanding) {
                    GlStateManager.translate((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
                    float f1 = (float) (meta * 360) / 16.0F;
                    GlStateManager.rotate(-f1, 0.0F, 1.0F, 0.0F);
                    bannerModel.bannerStand.showModel = true;
                } else {
                    float rot = 0.0F;
                    if (meta == 2) {
                        rot = 180.0F;
                    }
                    if (meta == 4) {
                        rot = 90.0F;
                    }
                    if (meta == 5) {
                        rot = -90.0F;
                    }
                    GlStateManager.translate((float) x + 0.5F, (float) y - 0.16666667F, (float) z + 0.5F);
                    GlStateManager.rotate(-rot, 0.0F, 1.0F, 0.0F);
                    GlStateManager.translate(0.0F, -0.3125F, -0.4375F);
                    bannerModel.bannerStand.showModel = false;
                }

                BlockPos blockpos = tile.getPos();
                float angle = (float) (blockpos.getX() * 7 + blockpos.getY() * 9 + blockpos.getZ() * 13) + ticks + partialTicks;
                bannerModel.bannerSlate.rotateAngleX = (-0.0125F + 0.01F * MathHelper.cos(angle * (float) Math.PI * 0.02F)) * (float) Math.PI;
                GlStateManager.enableRescaleNormal();
                ResourceLocation resourcelocation = BannerTextures.BANNER_DESIGNS.getResourceLocation(tile.getPatternResourceLocation(), tile.getPatternList(), tile.getColorList());
                if (resourcelocation != null) {
                    GlStateManager.pushMatrix();
                    float scale = 0.66666667F;
                    GlStateManager.scale(scale, -scale, -scale);
                    bannerModel.bannerSlate.rotationPointY = -32.0F;
                    Minecraft.getMinecraft().getTextureManager().bindTexture(resource);
                    npcs$faction_flag.render(0.0625F);
                    Minecraft.getMinecraft().getTextureManager().bindTexture(resourcelocation);
                    bannerModel.bannerStand.render(0.0625F);
                    bannerModel.bannerTop.render(0.0625F);
                    GlStateManager.popMatrix();
                }
                GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
                GlStateManager.popMatrix();
            }
        }
    }

}
