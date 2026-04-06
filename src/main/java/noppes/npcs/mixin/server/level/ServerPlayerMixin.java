package noppes.npcs.mixin.server.level;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import noppes.npcs.util.GameProfileAlt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerPlayer.class, priority = 498)
public class ServerPlayerMixin {

    @Inject(
            method = {"fudgeSpawnLocation"},
            at = {@At("HEAD")},
            cancellable = true
    )
    private void npcs$fudgeSpawnLocation(ServerLevel p_9202_, CallbackInfo ci) {
        if (((Player) (Object) this).getGameProfile() instanceof GameProfileAlt) {
            ci.cancel();
        }
    }

}
