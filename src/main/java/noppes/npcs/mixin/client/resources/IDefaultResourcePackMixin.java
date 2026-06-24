package noppes.npcs.mixin.client.resources;

import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.ResourceIndex;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = DefaultResourcePack.class, priority = 502)
public interface IDefaultResourcePackMixin {

    @Accessor ResourceIndex getResourceIndex();

}
