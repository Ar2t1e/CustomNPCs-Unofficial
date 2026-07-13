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

   public EntityAIOwnerHurtTarget(EntityNPCInterface npcIn) {
      super(npcIn, false);
      npc = npcIn;
      setFlags(EnumSet.of(Flag.TARGET));
   }

   @Override
   public boolean canUse() {
      if (npc.isFollower() && npc.role.defendOwner()) {
         LivingEntity entity = npc.getOwner();
         if (entity != null) {
            theTarget = entity.getLastHurtMob();
            return entity.getLastHurtMobTimestamp() != timestamp && canAttack(theTarget, TargetingConditions.DEFAULT);
         }
      }
      return false;
   }

   @Override
   public void start() {
      npc.setTarget(theTarget);
      LivingEntity entity = npc.getOwner();
      if (entity != null) { timestamp = entity.getLastHurtMobTimestamp(); }
      super.start();
   }

}
