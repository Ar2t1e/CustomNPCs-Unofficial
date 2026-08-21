package noppes.npcs.mixin.world;

import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import noppes.npcs.dimensions.CustomWorldProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = DimensionType.class, priority = 498)
public class DimensionTypeMixin {

    @Final @Shadow private Class <? extends WorldProvider > clazz;

    /**
     * @author BetaZavr
     * @reason Customizable in-game measurements
     */
    @Inject(method = "createDimension", at = @At("HEAD"), cancellable = true)
    public void createDimension(CallbackInfoReturnable<WorldProvider> cir) {
        if (clazz == CustomWorldProvider.class) {
            cir.setReturnValue(new CustomWorldProvider((DimensionType) (Object) this));
        }
    }

}
