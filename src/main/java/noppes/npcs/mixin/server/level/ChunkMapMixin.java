package noppes.npcs.mixin.server.level;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.PlayerMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ChunkMap.class, priority = 502)
public interface ChunkMapMixin {

   @Accessor("visibleChunkMap") Long2ObjectLinkedOpenHashMap<ChunkHolder> visibleChunkMap();

   @Accessor("playerMap") PlayerMap playerMap();

}
