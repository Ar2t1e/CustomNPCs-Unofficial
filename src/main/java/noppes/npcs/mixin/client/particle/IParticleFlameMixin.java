package noppes.npcs.mixin.client.particle;

import net.minecraft.client.particle.ParticleFlame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ParticleFlame.class, priority = 502)
public interface IParticleFlameMixin {

    @Mutable @Accessor void setFlameScale(float newFlameScale);

}
