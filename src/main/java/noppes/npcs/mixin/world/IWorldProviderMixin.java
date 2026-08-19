package noppes.npcs.mixin.world;

import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = WorldProvider.class, priority = 502)
public interface IWorldProviderMixin {

    @Accessor WorldType getTerrainType();

    @Accessor String getGeneratorSettings();

    @Accessor float[] getColorsSunriseSunset();
}
