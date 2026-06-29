package noppes.npcs.mixin.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = EntityLivingBase.class, priority = 502)
public interface IEntityLivingBaseMixin {

    @Accessor("HAND_STATES") DataParameter<Byte> getHandStates();

    @Accessor float getLastDamage();

    @Accessor void setRecentlyHit(int newRecentlyHit);

    @Accessor void setLastDamageSource(DamageSource newLastDamageSource);

    @Accessor void setLastDamageStamp(long newLastDamageStamp);

}
