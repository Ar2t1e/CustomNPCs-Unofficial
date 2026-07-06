package noppes.npcs.mixin.client.renderer;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import noppes.npcs.blocks.custom.CustomBlockLiquid;
import noppes.npcs.fluids.CustomFluid;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemRenderer.class, priority = 498)
public class ItemRendererMixin {

    @Final @Shadow private Minecraft mc;

    @Inject(method = "renderWaterOverlayTexture", at = @At("HEAD"), cancellable = true)
    private void onRenderWaterOverlay(float partialTicks, CallbackInfo ci) {
        if (!mc.player.isInsideOfMaterial(Material.WATER)) return;
        BlockPos pos = new BlockPos(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
        IBlockState state = mc.player.world.getBlockState(pos);
        if (state.getBlock() instanceof CustomBlockLiquid) {
            Fluid fluid = ((CustomBlockLiquid) state.getBlock()).getFluid();
            if (fluid instanceof CustomFluid) { ci.cancel(); }
        }
    }

}
