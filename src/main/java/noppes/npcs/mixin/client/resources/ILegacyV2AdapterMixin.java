package noppes.npcs.mixin.client.resources;

import net.minecraft.client.resources.LegacyV2Adapter;
import net.minecraft.client.resources.IResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = LegacyV2Adapter.class, priority = 502)
public interface ILegacyV2AdapterMixin {

    @Accessor IResourcePack getPack();

}
