package noppes.npcs.api.mixin.util;

import net.minecraft.util.EnumParticleTypes;

import java.util.Map;

public interface IEnumParticleTypesMixin {

    Map<Integer, EnumParticleTypes> npcs$getParticles();

    Map<String, EnumParticleTypes> npcs$getByName();

}
