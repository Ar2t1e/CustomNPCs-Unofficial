package noppes.npcs.ai.movement;

import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAIFollow extends Goal {

   private final EntityNPCInterface npc;
   private LivingEntity owner;
   public int updateTick = 0;

   public EntityAIFollow(EntityNPCInterface npcIn) {
      npc = npcIn;
      setFlags(EnumSet.of(Flag.MOVE));
   }

   public boolean canUse() {
      if (!canExcute()) { return false; }
      return !npc.isInRange(owner, npc.followRange());
   }

   public boolean canExcute() {
      return npc.isAlive() && npc.isFollower() && !npc.isAttacking() && (owner = npc.getOwner()) != null && npc.ais.animationType != 1;
   }

   public void start() {
      updateTick = 10;
   }

   public boolean canContinueToUse() {
      return !npc.getNavigation().isDone() && !npc.isInRange(owner, 2.0D) && canExcute();
   }

   public void stop() {
      owner = null;
      npc.getNavigation().stop();
   }

   public void tick() {
      ++updateTick;
      if (updateTick >= 10) {
         updateTick = 0;
         npc.getLookControl().setLookAt(owner, 10.0F, (float)npc.getMaxHeadXRot());
         double distance = npc.distanceToSqr(owner);
         double speed = 1.0D + distance / 150.0D;
         if (speed > 3.0D) { speed = 3.0D; }
         if (owner.isSprinting()) { speed += 0.5D; }
         if (!npc.getNavigation().moveTo(owner, speed) || !npc.isInRange(owner, 16.0D)) { npc.tpTo(owner); }
      }
   }
}
