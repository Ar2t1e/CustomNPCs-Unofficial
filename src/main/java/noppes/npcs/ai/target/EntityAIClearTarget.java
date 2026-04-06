package noppes.npcs.ai.target;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAIClearTarget extends Goal {

   private final EntityNPCInterface npc;
   private LivingEntity target;

   public EntityAIClearTarget(EntityNPCInterface npc) {
      this.npc = npc;
   }

   public boolean canUse() {
      this.target = this.npc.getTarget();
      if (this.target == null) {
         return false;
      } else {
         return this.npc.getOwner() != null && !this.npc.isInRange(this.npc.getOwner(), this.npc.stats.aggroRange * 2.0D) || this.npc.combatHandler.checkTarget();
      }
   }

   public void start() {
      this.npc.setTarget(null);
      if (this.target == this.npc.getLastHurtByMob()) {
         this.npc.setLastHurtByMob(null);
      }

      super.start();
   }

   public void stop() {
      this.npc.getNavigation().stop();
   }

}
