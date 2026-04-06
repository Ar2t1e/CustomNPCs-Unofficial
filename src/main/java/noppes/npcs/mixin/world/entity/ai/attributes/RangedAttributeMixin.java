package noppes.npcs.mixin.world.entity.ai.attributes;

import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import noppes.npcs.api.mixin.world.entity.ai.attributes.IRangedAttribute;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = RangedAttribute.class, priority = 499)
public class RangedAttributeMixin implements IRangedAttribute {

    @Final
    @Mutable
    @Shadow
    private double minValue;
    @Final
    @Mutable
    @Shadow
    private double maxValue;

    @Override
    public void npcs$setMinValue(double value) { minValue = value; }

    @Override
    public void npcs$setMaxValue(double value) { maxValue = value; }

}
