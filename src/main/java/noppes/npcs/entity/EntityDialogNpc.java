package noppes.npcs.entity;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import noppes.npcs.CustomEntities;
import org.jetbrains.annotations.NotNull;

public class EntityDialogNpc extends EntityNPCInterface {

   public EntityDialogNpc(Level world) {
      super(CustomEntities.entityCustomNpc, world);
   }

   public boolean isInvisibleTo(@NotNull Player player) {
      return true;
   }

   public boolean isInvisible() {
      return true;
   }

   public void tick() {
   }

   public @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
      return InteractionResult.FAIL;
   }

}
