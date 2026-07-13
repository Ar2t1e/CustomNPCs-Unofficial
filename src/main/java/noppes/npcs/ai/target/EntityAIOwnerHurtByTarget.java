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

   public EntityAIOwnerHurtByTarget(EntityNPCInterface npcIn) {
      super(npcIn, false);
      npc = npcIn;
      setFlags(EnumSet.of(Flag.TARGET));
   }

   @Override
   public boolean canUse() {
      if (npc.isFollower() && npc.role.defendOwner()) {
         LivingEntity entity = npc.getOwner();
         if (entity != null) {
            theOwnerAttacker = entity.getLastHurtByMob();
            return entity.getLastHurtByMobTimestamp() != timer && canAttack(theOwnerAttacker, TargetingConditions.DEFAULT);
         }
      }
      return false;
   }

   @Override
   public void start() {
      npc.setTarget(theOwnerAttacker);
      LivingEntity entity = npc.getOwner();
      if (entity != null) { timer = entity.getLastHurtByMobTimestamp(); }
      super.start();
   }

}
