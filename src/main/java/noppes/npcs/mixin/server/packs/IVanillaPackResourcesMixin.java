package noppes.npcs.mixin.server.packs;

import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Mixin(value = VanillaPackResources.class, priority = 502)
public interface IVanillaPackResourcesMixin {

    @Accessor Map<PackType, List<Path>> getPathsForType();

}
