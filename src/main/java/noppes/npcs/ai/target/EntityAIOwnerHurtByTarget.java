package noppes.npcs.ai.target;

import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAIOwnerHurtByTarget extends TargetGoal {

   EntityNPCInterface npc;
   LivingEntity theOwnerAttacker;
   private int timer;

   public EntityAIOwnerHurtByTarget(EntityNPCInterface npc) {
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
            this.theOwnerAttacker = entity.getLastHurtByMob();
            int i = entity.getLastHurtByMobTimestamp();
            return i != this.timer && this.canAttack(this.theOwnerAttacker, TargetingConditions.DEFAULT);
         }
      } else {
         return false;
      }
   }

   public void start() {
      this.npc.setTarget(this.theOwnerAttacker);
      LivingEntity entity = this.npc.getOwner();
      if (entity != null) {
         this.timer = entity.getLastHurtByMobTimestamp();
      }

      super.start();
   }

}
