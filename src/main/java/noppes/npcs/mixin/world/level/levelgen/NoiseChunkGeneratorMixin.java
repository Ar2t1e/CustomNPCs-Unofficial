package noppes.npcs.mixin.world.level.levelgen;

import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import noppes.npcs.NPCSpawning;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = NoiseBasedChunkGenerator.class, priority = 498)
public class NoiseChunkGeneratorMixin {

   @Inject(
      at = {@At("HEAD")},
      method = {"spawnOriginalMobs"}
   )
   private void spawnOriginalMobs(WorldGenRegion region, CallbackInfo ci) {
      ChunkPos chunkpos = region.getCenter();
      int x = chunkpos.getMinBlockX();
      int z = chunkpos.getMinBlockZ();
      Biome biome = region.getBiome((new ChunkPos(x, z)).getWorldPosition()).value();
      NPCSpawning.performLevelGenSpawning(region, biome, x, z, region.getRandom());
   }

}
