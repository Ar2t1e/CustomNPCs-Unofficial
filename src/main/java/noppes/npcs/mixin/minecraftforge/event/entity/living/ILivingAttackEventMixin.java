package noppes.npcs.mixin.minecraftforge.event.entity.living;

import net.minecraftforge.event.entity.living.LivingAttackEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = LivingAttackEvent.class, priority = 502, remap = false)
public interface ILivingAttackEventMixin {

    @Mutable
    @Accessor
    void setAmount(float newAmount);

}
