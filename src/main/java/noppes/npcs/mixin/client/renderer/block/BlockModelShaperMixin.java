package noppes.npcs.mixin.client.renderer.block;

import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.world.level.block.state.BlockState;
import noppes.npcs.blocks.custom.CustomCauldronBlock;
import noppes.npcs.client.resources.model.CustomCauldronBakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(BlockModelShaper.class)
public class BlockModelShaperMixin {

    @Shadow private Map<BlockState, BakedModel> modelByStateCache;

    @Inject(method = "getBlockModel", at = @At("RETURN"), cancellable = true)
    private void npcs$getBlockModel(BlockState state, CallbackInfoReturnable<BakedModel> cir) {
        if (state.getBlock() instanceof CustomCauldronBlock cauldron && !(cir.getReturnValue() instanceof CustomCauldronBakedModel)) {
            BakedModel bakedmodel = cir.getReturnValue();
            modelByStateCache.remove(state);
            modelByStateCache.put(state, new CustomCauldronBakedModel((SimpleBakedModel) bakedmodel, cauldron));
            cir.setReturnValue(modelByStateCache.get(state));
        }
    }

}
