package noppes.npcs.ai;

import net.minecraft.world.entity.ai.goal.Goal;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAIWorldLines extends Goal {

   private final EntityNPCInterface npc;
   private int cooldown = 100;

   public EntityAIWorldLines(EntityNPCInterface npc) {
      this.npc = npc;
   }

   public boolean canUse() {
      if (this.cooldown > 0) {
         --this.cooldown;
      }

      return !this.npc.isAttacking() && !this.npc.isKilled() && this.npc.advanced.hasLevelLines() && this.npc.getRandom().nextInt(1800) == 1;
   }

   public void start() {
      this.cooldown = 100;
      this.npc.saySurrounding(this.npc.advanced.getLevelLine());
   }

}
