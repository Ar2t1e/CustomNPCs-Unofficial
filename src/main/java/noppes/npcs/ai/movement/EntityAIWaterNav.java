package noppes.npcs.ai.movement;

import net.minecraft.world.entity.ai.goal.Goal;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAIWaterNav extends Goal {

   private final EntityNPCInterface entity;

   public EntityAIWaterNav(EntityNPCInterface npc) {
      this.entity = npc;
      npc.getNavigation().setCanFloat(true);
   }

   public boolean canUse() {
      if (!this.entity.isInWater() && !this.entity.isInLava()) {
         return false;
      } else {
         return this.entity.ais.canSwim || this.entity.horizontalCollision;
      }
   }

   public void tick() {
      if (this.entity.getRandom().nextFloat() < 0.8F) {
         this.entity.getJumpControl().jump();
      }
   }

}
