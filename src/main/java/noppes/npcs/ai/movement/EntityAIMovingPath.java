package noppes.npcs.ai.movement;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.world.entity.ai.goal.Goal;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAIMovingPath extends Goal {

   private final EntityNPCInterface npc;
   private int[] pos;
   private int retries = 0;

   public EntityAIMovingPath(EntityNPCInterface iNpc) {
      this.npc = iNpc;
      this.setFlags(EnumSet.of(Flag.MOVE));
   }

   public boolean canUse() {
      if (!this.npc.isAttacking() && !this.npc.isInteracting() && (this.npc.getRandom().nextInt(40) == 0 || !this.npc.ais.movingPause) && this.npc.getNavigation().isDone()) {
         List<int[]> list = this.npc.ais.getMovingPath();
         if (list.size() < 2) {
            return false;
         } else {
            this.npc.ais.incrementMovingPath();
            this.pos = this.npc.ais.getCurrentMovingPath();
            this.retries = 0;
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean canContinueToUse() {
      if (!this.npc.isAttacking() && !this.npc.isInteracting()) {
         if (this.npc.getNavigation().isDone()) {
            this.npc.getNavigation().stop();
            if (this.npc.distanceToSqr(this.pos[0], this.pos[1], this.pos[2]) < 3.0D) {
               return false;
            } else if (this.retries++ < 3) {
               this.start();
               return true;
            } else {
               return false;
            }
         } else {
            return true;
         }
      } else {
         this.npc.ais.decreaseMovingPath();
         return false;
      }
   }

   public void start() {
      this.npc.getNavigation().moveTo((double)this.pos[0] + 0.5D, this.pos[1], (double)this.pos[2] + 0.5D, 1.0D);
   }
}
