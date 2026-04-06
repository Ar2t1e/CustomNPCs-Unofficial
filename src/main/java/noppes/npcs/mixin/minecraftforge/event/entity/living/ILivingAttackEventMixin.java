package noppes.npcs.mixin.minecraftforge.event.entity.living;

import net.minecraftforge.event.entity.living.LivingAttackEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = LivingAttackEvent.class, priority = 502, remap = false)
public interface ILivingAttackEventMixin {

    @Accessor void setAmount(float newAmount);

}
