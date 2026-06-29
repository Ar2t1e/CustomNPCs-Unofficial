package noppes.npcs.mixin.client.audio;

import net.minecraft.client.audio.ISoundEventAccessor;
import net.minecraft.client.audio.Sound;
import net.minecraft.client.audio.SoundEventAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = SoundEventAccessor.class, priority = 502)
public interface ISoundEventAccessorMixin {

    @Accessor List<ISoundEventAccessor<Sound>> getAccessorList();

}
