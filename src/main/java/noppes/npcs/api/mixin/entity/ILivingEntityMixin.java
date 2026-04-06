package noppes.npcs.api.mixin.entity;

import net.minecraft.world.damagesource.DamageSource;

public interface ILivingEntityMixin {

    void npcs$setCurrentDamageSource(DamageSource source);

}