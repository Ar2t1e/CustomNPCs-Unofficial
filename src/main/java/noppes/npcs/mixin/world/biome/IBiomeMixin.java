package noppes.npcs.mixin.world.biome;

import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Biome.class, priority = 502)
public interface IBiomeMixin {

    @Accessor
    String getBiomeName();

}
