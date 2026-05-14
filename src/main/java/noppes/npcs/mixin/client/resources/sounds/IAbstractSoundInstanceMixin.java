package noppes.npcs.mixin.client.resources.sounds;

import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = AbstractSoundInstance.class, priority = 502)
public interface IAbstractSoundInstanceMixin {

    @Accessor("x") void setX(double newX);

    @Accessor("y") void setY(double newY);

    @Accessor("z") void setZ(double newZ);

}
