package noppes.npcs.mixin.client.renderer.tileentity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelShield;
import net.minecraft.client.renderer.BannerTextures;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.NoppesUtilServer;
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

@Mixin(value = TileEntityItemStackRenderer.class, priority = 498)
public class TileEntityItemStackRendererMixin {

    @Final @Shadow private ModelShield modelShield;

    @Unique private ModelRenderer npcs$faction_flag;

    /**
     * @author BetaZavr
     * @reason Added faction ID for flag display
     */
    @Inject(
            at = @At("HEAD"),
            method = "renderByItem(Lnet/minecraft/item/ItemStack;F)V",
            cancellable = true
    )
    public void npcs$renderByItem(ItemStack stack, float partialTicks, CallbackInfo ci) {
        NBTTagCompound compound = stack.getSubCompound("BlockEntityTag");
        if (stack.getItem() == Items.SHIELD && compound != null) {
            ResourceLocation resource = null;
            if (compound.hasKey("FlagResource", 8)) { resource = new ResourceLocation(NoppesUtilServer.validLocation(compound.getString("FlagResource"))); }
            else if (compound.hasKey("FactionID", 3)) {
                Faction faction = FactionController.instance.getFaction(compound.getInteger("FactionID"));
                if (faction != null) { resource = faction.flag; }
            }
            if (resource != null) {
                ci.cancel();
                if (npcs$faction_flag == null) { npcs$faction_flag = CustomCube.createShieldFlag(modelShield); }
                GlStateManager.pushMatrix();
                GlStateManager.scale(1.0F, -1.0F, -1.0F);
                Minecraft.getMinecraft().getTextureManager().bindTexture(BannerTextures.SHIELD_BASE_TEXTURE);
                modelShield.render();
                Minecraft.getMinecraft().getTextureManager().bindTexture(resource);
                npcs$faction_flag.render(partialTicks);
                GlStateManager.popMatrix();
            }
        }
    }


}
