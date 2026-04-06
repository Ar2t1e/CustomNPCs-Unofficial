package noppes.npcs.mixin.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockState;
import noppes.npcs.CustomNpcs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = IceBlock.class, priority = 498)
public class IceBlockMixin {

   @Inject(
      at = {@At("HEAD")},
      method = {"randomTick"},
      cancellable = true
   )
   private void setupAnimPre(BlockState blockState, ServerLevel level, BlockPos blockPos, RandomSource rndSource, CallbackInfo ci) {
      if (!CustomNpcs.IceMeltsEnabled) {
         ci.cancel();
      }
   }

}
