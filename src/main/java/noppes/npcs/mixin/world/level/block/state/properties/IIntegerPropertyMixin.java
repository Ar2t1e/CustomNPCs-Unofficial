package noppes.npcs.mixin.world.level.block.state.properties;

import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = IntegerProperty.class, priority = 502)
public interface IIntegerPropertyMixin {

    @Accessor int getMin();

}
