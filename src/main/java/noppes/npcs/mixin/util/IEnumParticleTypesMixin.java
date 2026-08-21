package noppes.npcs.mixin.util;

import net.minecraft.util.EnumParticleTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.throwables.MixinException;

import java.util.Map;

@Mixin(value = EnumParticleTypes.class, priority = 502)
public interface IEnumParticleTypesMixin {

    @Accessor("PARTICLES") static Map<Integer, EnumParticleTypes> getParticles() { throw new MixinException("Mixin did not initialize properly."); }

    @Accessor("BY_NAME") static Map<String, EnumParticleTypes> getByNames() { throw new MixinException("Mixin did not initialize properly."); }

}
