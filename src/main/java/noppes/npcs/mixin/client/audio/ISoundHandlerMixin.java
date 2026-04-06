package noppes.npcs.mixin.client.audio;

import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.audio.SoundRegistry;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = SoundHandler.class, priority = 502)
public interface ISoundHandlerMixin {

    @Accessor
    SoundManager getSndManager();

    @Accessor
    SoundRegistry getSoundRegistry();

}
