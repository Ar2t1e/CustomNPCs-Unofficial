package noppes.npcs.mixin.world.level;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = BaseSpawner.class, priority = 502)
public interface IBaseSpawnerMixin {

   @Invoker void callSetNextSpawnData(@Nullable Level level, BlockPos blockPos, SpawnData spawnData);

}
