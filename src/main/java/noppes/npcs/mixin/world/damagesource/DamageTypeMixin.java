package noppes.npcs.mixin.world.damagesource;

import net.minecraft.world.damagesource.DamageType;
import noppes.npcs.entity.data.Resistances;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DamageType.class, priority = 499)
public class DamageTypeMixin {

    @Inject(at = @At("RETURN"),
            method = "<init>(Ljava/lang/String;Lnet/minecraft/world/damagesource/DamageScaling;FLnet/minecraft/world/damagesource/DamageEffects;Lnet/minecraft/world/damagesource/DeathMessageType;)V")
    private void collectDamageType(CallbackInfo ci) {
        Resistances.add(((DamageType) (Object) this).msgId());
    }

}
