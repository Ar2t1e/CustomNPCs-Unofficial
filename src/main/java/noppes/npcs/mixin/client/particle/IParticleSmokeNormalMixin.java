package noppes.npcs.mixin.client.particle;

import net.minecraft.client.particle.ParticleSmokeNormal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ParticleSmokeNormal.class, priority = 502)
public interface IParticleSmokeNormalMixin {

    @Mutable @Accessor void setSmokeParticleScale(float newSmokeParticleScale);

}
