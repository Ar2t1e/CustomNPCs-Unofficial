package noppes.npcs.mixin.client.audio;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import paulscode.sound.Library;
import paulscode.sound.SoundSystem;

@Mixin(value = SoundSystem.class, remap = false, priority = 502)
public interface ISoundSystemMixin {

    @Accessor Library getSoundLibrary();

}
