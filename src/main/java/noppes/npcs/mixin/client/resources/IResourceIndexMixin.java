package noppes.npcs.mixin.client.resources;

import net.minecraft.client.resources.ResourceIndex;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.io.File;
import java.util.Map;

@Mixin(value = ResourceIndex.class, priority = 502)
public interface IResourceIndexMixin {

    @Accessor Map<String, File> getResourceMap();

}
