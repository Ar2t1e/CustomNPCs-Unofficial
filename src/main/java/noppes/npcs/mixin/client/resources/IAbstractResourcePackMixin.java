package noppes.npcs.mixin.client.resources;

import net.minecraft.client.resources.AbstractResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.io.File;

@Mixin(value = AbstractResourcePack.class, priority = 502)
public interface IAbstractResourcePackMixin {

    @Accessor File getResourcePackFile();

}
