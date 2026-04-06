package noppes.npcs.mixin.util;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = SoundEvent.class, priority = 502)
public interface ISoundEventMixin {

    @Accessor
    ResourceLocation getSoundName();

}
