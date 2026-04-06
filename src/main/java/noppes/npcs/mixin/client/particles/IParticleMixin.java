package noppes.npcs.mixin.client.particles;

import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Particle.class, priority = 502)
public interface IParticleMixin {

    @Accessor boolean getStoppedByCollision();

    @Accessor void setStoppedByCollision(boolean newStoppedByCollision);

}
