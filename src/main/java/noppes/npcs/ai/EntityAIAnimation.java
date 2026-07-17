package noppes.npcs.ai;

import net.minecraft.world.entity.ai.goal.Goal;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAIAnimation extends Goal {

   private final EntityNPCInterface npc;
   private boolean isAttacking = false;
   private boolean removed = false;
   private boolean isAtStartPoint = false;
   private boolean hasPath = false;
   public int temp = 0;

   public EntityAIAnimation(EntityNPCInterface npcIn) { npc = npcIn; }

   @Override
   public boolean canUse() {
      removed = !npc.isAlive();
      if (removed) { return npc.currentAnimation != 2; }
      if (npc.stats.ranged.getHasAimAnimation() && npc.isAttacking()) { return npc.currentAnimation != 6; }
      hasPath = !npc.getNavigation().isDone();
      isAttacking = npc.isAttacking();
      isAtStartPoint = npc.ais.shouldReturnHome() && npc.isVeryNearAssignedPlace();
      if (temp != 0) {
         if (!hasNavigation()) { return npc.currentAnimation != temp; }
         temp = 0;
      }
      if (hasNavigation() && notWalkingAnimation(npc.currentAnimation)) { return npc.currentAnimation != 0; }
      return npc.currentAnimation != npc.ais.animationType;
   }

   @Override
   public void tick() {
      if (npc.stats.ranged.getHasAimAnimation() && npc.isAttacking()) { setAnimation(6); }
      else {
         int type = npc.ais.animationType;
         if (removed) { type = 2; }
         else if (notWalkingAnimation(npc.ais.animationType) && hasNavigation()) { type = 0; }
         else if (temp != 0) {
            if (hasNavigation()) { temp = 0; }
            else { type = temp;}
         }
         // if (this.npc.stats.ranged.getHasAimAnimation() && this.npc.isAttacking()) { type = 6; } // <- AI target
         setAnimation(type);
      }
   }

   public static int getWalkingAnimationGuiIndex(int animation) {
      return switch (animation) {
         case 3 -> 5;
         case 4 -> 1;
         case 5 -> 3;
         case 6 -> 2;
         case 7 -> 4;
         default -> 0;
      };
   }

   public static boolean notWalkingAnimation(int animation) { return getWalkingAnimationGuiIndex(animation) == 0; }

   private void setAnimation(int animation) {
      npc.setCurrentAnimation(animation);
      npc.refreshDimensions();
      npc.setPos(npc.getX(), npc.getY(), npc.getZ());
   }

   private boolean hasNavigation() { return isAttacking || npc.ais.shouldReturnHome() && !isAtStartPoint && !npc.isFollower() || hasPath; }

}
