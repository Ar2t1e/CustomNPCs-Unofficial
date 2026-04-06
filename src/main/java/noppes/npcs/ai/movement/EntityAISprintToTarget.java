package noppes.npcs.ai.movement;

import java.util.EnumSet;
import java.util.Objects;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import noppes.npcs.CustomNpcs;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAISprintToTarget extends Goal {

   private final EntityNPCInterface npc;

   public EntityAISprintToTarget(EntityNPCInterface npcIn) {
      npc = npcIn;
      setFlags(EnumSet.of(Flag.MOVE));
   }

   @Override
   public boolean canUse() {
      CustomNpcs.debugData.start(npc);
      LivingEntity runTarget = npc.getTarget();
      if (runTarget != null && !npc.getNavigation().isDone()) {
         CustomNpcs.debugData.end(npc);
         return switch (npc.ais.onAttack) {
            case 0 -> !npc.isInRange(runTarget, npc.stats.aggroRange / 3.0d) && npc.onGround(); // Attack
            case 2 -> npc.isInRange(runTarget, npc.stats.aggroRange) && npc.onGround(); // Avoid
            default -> true; // Panic
         };
      }
      CustomNpcs.debugData.end(npc);
      return false;
   }

   @Override
   public boolean canContinueToUse() {
      Vec3 mo = npc.getDeltaMovement();
      return npc.isAlive() && npc.ais.canSprint && npc.onGround() && npc.hurtTime <= 0 && mo.x != 0.0D && mo.z != 0.0D;
   }

   @Override
   public void start() { npc.setSprinting(true); }

   @Override
   public void stop() { npc.setSprinting(false); }
}
