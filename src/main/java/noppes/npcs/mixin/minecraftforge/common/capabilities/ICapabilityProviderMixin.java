package noppes.npcs.mixin.minecraftforge.common.capabilities;

import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@SuppressWarnings("all")
@Mixin(value = CapabilityProvider.class, priority = 502)
public interface ICapabilityProviderMixin {

    @Accessor("capabilities") CapabilityDispatcher getCapas();

}
