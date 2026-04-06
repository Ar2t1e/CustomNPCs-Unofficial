package noppes.npcs.ai;

import java.util.EnumSet;
import net.minecraft.world.entity.ai.goal.Goal;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAITransform extends Goal {

   private final EntityNPCInterface npc;

   public EntityAITransform(EntityNPCInterface npc) {
      this.npc = npc;
      this.setFlags(EnumSet.of(Flag.MOVE));
   }

   public boolean canUse() {
      if (!this.npc.isKilled() && !this.npc.isAttacking() && !this.npc.transform.editingModus) {
         return this.npc.level().isDay() == this.npc.transform.isActive;
      } else {
         return false;
      }
   }

   public void start() {
      this.npc.transform.transform(!this.npc.transform.isActive);
   }

}
