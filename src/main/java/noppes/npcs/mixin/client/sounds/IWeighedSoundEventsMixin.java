package noppes.npcs.mixin.client.sounds;

import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.client.sounds.Weighted;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = WeighedSoundEvents.class, priority = 502)
public interface IWeighedSoundEventsMixin {

    @Accessor List<Weighted<Sound>> getList();

}
