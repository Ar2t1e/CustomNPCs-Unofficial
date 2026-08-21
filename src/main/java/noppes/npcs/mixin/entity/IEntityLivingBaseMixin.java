package noppes.npcs.mixin.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.throwables.MixinException;

@Mixin(value = EntityLivingBase.class, priority = 502)
public interface IEntityLivingBaseMixin {

    @Accessor("HAND_STATES") static DataParameter<Byte> getHandStates() { throw new MixinException("Mixin did not initialize properly."); }

    @Accessor float getLastDamage();

    @Accessor void setRecentlyHit(int newRecentlyHit);

    @Accessor void setLastDamageSource(DamageSource newLastDamageSource);

    @Accessor void setLastDamageStamp(long newLastDamageStamp);

}
