package noppes.npcs.mixin.client.renderer;

import net.minecraft.client.renderer.CubeMap;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = CubeMap.class, priority = 502)
public interface ICubeMapMixin {

    @Accessor ResourceLocation[] getImages();

}
