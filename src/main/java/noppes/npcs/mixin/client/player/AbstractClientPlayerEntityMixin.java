package noppes.npcs.mixin.client.player;

import com.google.common.base.MoreObjects;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import noppes.npcs.client.controllers.ClientSkinController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AbstractClientPlayer.class, priority = 498)
public abstract class AbstractClientPlayerEntityMixin extends Player {

   public AbstractClientPlayerEntityMixin(Level level, BlockPos blockPos, float yRot, GameProfile gameProfile) {
      super(level, blockPos, yRot, gameProfile);
   }

   @Inject(
      at = {@At("RETURN")},
      method = {"getSkinTextureLocation"},
      cancellable = true
   )
   public void getSkinLocation(CallbackInfoReturnable<ResourceLocation> cir) {
      cir.setReturnValue(MoreObjects.firstNonNull(ClientSkinController.getSkinForPlayer(this.getDisplayName().getString()), cir.getReturnValue()));
   }

}
