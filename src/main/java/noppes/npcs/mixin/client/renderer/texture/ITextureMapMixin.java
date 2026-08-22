package noppes.npcs.mixin.client.renderer.texture;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = TextureMap.class, priority = 502)
public interface ITextureMapMixin {

    @Accessor Map<String, TextureAtlasSprite> getMapRegisteredSprites();

}
