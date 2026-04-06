package noppes.npcs.mixin.client.audio;

import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.audio.SoundRegistry;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = SoundRegistry.class, priority = 502)
public interface ISoundRegistryMixin {

    @Accessor
    Map<ResourceLocation, SoundEventAccessor> getSoundRegistry();

}
