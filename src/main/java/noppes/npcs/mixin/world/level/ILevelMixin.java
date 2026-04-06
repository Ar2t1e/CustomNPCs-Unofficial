package noppes.npcs.mixin.world.level;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Level.class, priority = 502)
public interface ILevelMixin {

    @Accessor WritableLevelData getLevelData();

}
