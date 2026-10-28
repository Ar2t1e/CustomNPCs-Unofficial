package noppes.npcs.mixin.client.renderer.texture;

import net.minecraft.client.renderer.texture.TextureUtil;
import noppes.npcs.client.util.GlCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TextureUtil.class, priority = 498)
public class TextureUtilMixin {

    @Inject(method = "allocateTextureImpl", at = @At("HEAD"))
    private static void npcs$allocateTexture(int glTextureId, int mipmapLevels, int width, int height, CallbackInfo ci) { GlCache.forget(glTextureId); }

}
