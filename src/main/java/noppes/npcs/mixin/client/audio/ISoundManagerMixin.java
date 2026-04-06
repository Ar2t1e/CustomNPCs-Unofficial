package noppes.npcs.mixin.client.audio;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = SoundManager.class, priority = 502)
public interface ISoundManagerMixin {

    @Accessor
    Map<String, ISound> getPlayingSounds();

}
