package noppes.npcs.mixin.world.entity.player;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.constants.AnimationKind;
import noppes.npcs.api.mixin.entity.player.IPlayerMixin;
import noppes.npcs.client.model.animation.AnimationConfig;
import noppes.npcs.constants.EnumAnimationStages;
import noppes.npcs.entity.data.DataAnimation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Player.class, priority = 498)
public class PlayerMixin implements IPlayerMixin {

    @Unique
    public DataAnimation npcs$animation;

    @Inject(method = "attack", at = @At("HEAD"))
    public void npcs$attack(Entity targetEntity, CallbackInfo ci) {
        if (CustomNpcs.ShowCustomAnimation) { npcs$animation.tryRunAnimation(AnimationKind.ATTACKING); }
    }

    @Inject(method = "defineSynchedData", at = @At("RETURN"))
    public void npcs$defineSynchedData(CallbackInfo ci) {
        if (npcs$animation == null) { npcs$animation = new DataAnimation((Player) (Object) this); }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    protected void npcs$tick(CallbackInfo ci) {
        if (npcs$animation != null) { npcs$animation.updateTime(); }
    }

    @Inject(method = "jumpFromGround", at = @At("TAIL"))
    public void npcs$jumpFromGround(CallbackInfo ci) { npcs$animation.setJump(true); }

    @Inject(method = "aiStep", at = @At("TAIL"))
    public void npcs$aiStep(CallbackInfo ci) {
        if (CustomNpcs.ShowCustomAnimation) {
            Player player = (Player) (Object) this;
            // Jump
            if (npcs$animation.getJump() && player.onGround() && npcs$animation.getAnimationStage() != EnumAnimationStages.Started) {
                npcs$animation.setJump(false);
                if (npcs$animation.isAnimated(AnimationKind.JUMP)) {
                    npcs$animation.stopAnimation();
                }
            }
            // Swing
            if (!npcs$animation.getSwing() && player.swingTime > 0) {
                npcs$animation.setSwing(true);
                if (!npcs$animation.isAnimated(AnimationKind.ATTACKING, AnimationKind.AIM, AnimationKind.SHOOT)) {
                    AnimationConfig anim = npcs$animation.tryRunAnimation(AnimationKind.SWING);
                    if (anim != null) {
                        player.swingTime = 0;
                        player.swinging = false;
                    }
                }
            }
            else if (npcs$animation.getSwing() && player.swingTime == 0) {
                npcs$animation.setSwing(false);
            }
            // Walking or Standing
            npcs$animation.resetWalkAndStandAnimations();
        }
    }

    @Override
    public DataAnimation npcs$getAnimation() { return npcs$animation; }

}
