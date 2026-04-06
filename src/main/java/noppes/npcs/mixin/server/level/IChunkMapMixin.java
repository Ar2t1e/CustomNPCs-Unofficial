package noppes.npcs.mixin.server.level;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ChunkMap.class, priority = 502)
public interface IChunkMapMixin {

    @Accessor Long2ObjectLinkedOpenHashMap<ChunkHolder> getVisibleChunkMap();

}
