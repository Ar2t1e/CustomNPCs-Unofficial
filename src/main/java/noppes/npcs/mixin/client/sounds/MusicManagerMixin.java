package noppes.npcs.mixin.client.sounds;

import net.minecraft.client.sounds.MusicManager;
import noppes.npcs.client.ClientTickHandler;
import noppes.npcs.client.controllers.MusicController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MusicManager.class, priority = 498)
public class MusicManagerMixin {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void npcs$tick(CallbackInfo ci) {
        if (ClientTickHandler.inGame && MusicController.Instance.music != null && MusicController.Instance.isPlaying(MusicController.Instance.music)) {
            ci.cancel();
        }
    }
}
