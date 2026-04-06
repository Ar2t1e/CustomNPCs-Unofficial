package noppes.npcs.mixin.com.mojang.blaze3d.audio;

import com.mojang.blaze3d.audio.Channel;
import noppes.npcs.api.mixin.com.mojang.blaze3d.audio.IChannelMixin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Channel.class, priority = 498)
public class ChannelMixin implements IChannelMixin {

    @Shadow @Final private int source;

    @Unique private long npcs$time = 0L;
    @Unique private long npcs$pauseTime = 0L;

    @Inject(method = {"play"}, at = {@At("TAIL")})
    private void npcs$play(CallbackInfo ci) {
        if (npcs$time == 0L) {
            npcs$time = System.currentTimeMillis();
            npcs$pauseTime = 0L;
        }
    }

    @Inject(method = {"pause"}, at = {@At("TAIL")})
    private void npcs$pause(CallbackInfo ci) {
        if (npcs$pauseTime == 0L) { npcs$pauseTime = System.currentTimeMillis(); }
    }

    @Inject(method = {"unpause"}, at = {@At("TAIL")})
    private void npcs$unpause(CallbackInfo ci) {
        if (npcs$pauseTime != 0L) {
            npcs$time += System.currentTimeMillis() - npcs$pauseTime;
            npcs$pauseTime = 0L;
        }
    }

    @Inject(method = {"stop"}, at = {@At("TAIL")})
    private void npcs$stop(CallbackInfo ci) {
        if (npcs$pauseTime == 0L) { npcs$time = 0L; }
    }

    @Override
    public int npcs$getSource() { return source; }

    @Override
    public long npcs$getCurrentTime() {
        if (npcs$pauseTime > 0L) { return npcs$pauseTime - npcs$time; }
        return System.currentTimeMillis() - npcs$time;
    }

}
