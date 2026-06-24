package noppes.npcs.mixin.entity.ai.attributes;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = AttributeModifier.class, priority = 502)
public interface IAttributeModifierMixin {

    @Mutable @Accessor void setOperation(int newOperation);

    @Mutable @Accessor void setName(String newName);

    @Mutable @Accessor void setAmount(double newAmount);

}
