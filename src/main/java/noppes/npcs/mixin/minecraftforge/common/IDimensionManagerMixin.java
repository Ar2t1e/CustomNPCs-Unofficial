package noppes.npcs.mixin.minecraftforge.common;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = DimensionManager.class, priority = 502, remap = false)
public interface IDimensionManagerMixin {

    @Accessor static IntSet getUnloadQueue() { throw new IllegalStateException("Mixin did not initialize properly."); }

    @Accessor static Int2ObjectMap<WorldServer> getWorlds() { throw new IllegalStateException("Mixin did not initialize properly."); }

}
