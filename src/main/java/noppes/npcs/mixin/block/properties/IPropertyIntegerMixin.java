package noppes.npcs.mixin.block.properties;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.properties.PropertyInteger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = PropertyInteger.class, priority = 502)
public interface IPropertyIntegerMixin {

    @Accessor
    ImmutableSet<Integer> getAllowedValues();

}
