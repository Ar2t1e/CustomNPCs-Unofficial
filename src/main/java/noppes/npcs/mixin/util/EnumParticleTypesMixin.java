package noppes.npcs.mixin.util;

import net.minecraft.util.EnumParticleTypes;
import noppes.npcs.api.mixin.util.IEnumParticleTypesMixin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(value = EnumParticleTypes.class, priority = 498)
public class EnumParticleTypesMixin implements IEnumParticleTypesMixin {

    @Final
    @Shadow
    private static Map<Integer, EnumParticleTypes> PARTICLES;

    @Final
    @Shadow
    private static Map<String, EnumParticleTypes> BY_NAME;

    @Override
    public Map<Integer, EnumParticleTypes> npcs$getParticles() { return PARTICLES; }

    @Override
    public Map<String, EnumParticleTypes> npcs$getByName() { return BY_NAME; }

}
