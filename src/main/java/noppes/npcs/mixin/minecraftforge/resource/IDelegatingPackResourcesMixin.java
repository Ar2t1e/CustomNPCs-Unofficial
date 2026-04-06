package noppes.npcs.mixin.minecraftforge.resource;

import net.minecraft.server.packs.PackResources;
import net.minecraftforge.resource.DelegatingPackResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(value = DelegatingPackResources.class, priority = 502, remap = false)
public interface IDelegatingPackResourcesMixin {

    @Accessor Map<String, List<PackResources>> getNamespacesAssets();

}
