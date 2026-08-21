package noppes.npcs.mixin.client.audio;

import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.audio.SoundRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = SoundHandler.class, priority = 502)
public interface ISoundHandlerMixin {

    @Accessor SoundManager getSndManager();

    @Accessor SoundRegistry getSoundRegistry();

}
