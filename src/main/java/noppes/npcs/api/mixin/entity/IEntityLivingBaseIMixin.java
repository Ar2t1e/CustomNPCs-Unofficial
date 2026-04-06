package noppes.npcs.api.mixin.entity;

import net.minecraft.util.DamageSource;

public interface IEntityLivingBaseIMixin {

    void npcs$setCurrentDamageSource(DamageSource source);

}

