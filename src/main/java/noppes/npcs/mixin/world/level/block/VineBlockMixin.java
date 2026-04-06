package noppes.npcs.mixin.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import noppes.npcs.CustomNpcs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = VineBlock.class, priority = 498)
public class VineBlockMixin {

   @Inject(
      at = {@At("HEAD")},
      method = {"randomTick"},
      cancellable = true
   )
   private void setupAnimPre(BlockState p_222655_, ServerLevel p_222656_, BlockPos p_222657_, RandomSource p_222658_, CallbackInfo ci) {
      if (!CustomNpcs.VineGrowthEnabled) {
         ci.cancel();
      }
   }

}
