package noppes.npcs.mixin.entity.ai.attributes;

import net.minecraft.entity.ai.attributes.RangedAttribute;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = RangedAttribute.class, priority = 502)
public interface IRangedAttributeMixin {

    @Accessor double getMinimumValue();

    @Mutable @Accessor void setMinimumValue(double newMinimumValue);

    @Accessor double getMaximumValue();

    @Mutable @Accessor void setMaximumValue(double newMinimumValue);

}
