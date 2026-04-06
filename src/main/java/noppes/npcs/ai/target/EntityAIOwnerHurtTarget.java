package noppes.npcs.ai.target;

import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAIOwnerHurtTarget extends TargetGoal {

   EntityNPCInterface npc;
   LivingEntity theTarget;
   private int timestamp;

   public EntityAIOwnerHurtTarget(EntityNPCInterface npc) {
      super(npc, false);
      this.npc = npc;
      this.setFlags(EnumSet.of(Flag.TARGET));
   }

   public boolean canUse() {
      if (this.npc.isFollower() && this.npc.role.defendOwner()) {
         LivingEntity entity = this.npc.getOwner();
         if (entity == null) {
            return false;
         } else {
            this.theTarget = entity.getLastHurtMob();
            int i = entity.getLastHurtMobTimestamp();
            return i != timestamp && this.canAttack(this.theTarget, TargetingConditions.DEFAULT);
         }
      } else {
         return false;
      }
   }

   public void start() {
      this.npc.setTarget(this.theTarget);
      LivingEntity entity = this.npc.getOwner();
      if (entity != null) {
         timestamp = entity.getLastHurtMobTimestamp();
      }
      super.start();
   }

}
