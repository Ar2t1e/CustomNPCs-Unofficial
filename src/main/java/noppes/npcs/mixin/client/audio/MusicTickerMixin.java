package noppes.npcs.mixin.client.audio;

import net.minecraft.client.audio.MusicTicker;
import noppes.npcs.client.ClientTickHandler;
import noppes.npcs.client.controllers.MusicController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MusicTicker.class, priority = 498)
public class MusicTickerMixin {

    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    public void npcs$update(CallbackInfo ci) {
        if (ClientTickHandler.inGame && MusicController.Instance.music != null && MusicController.Instance.isPlaying(MusicController.Instance.music)) {
            ci.cancel();
        }
    }

}
