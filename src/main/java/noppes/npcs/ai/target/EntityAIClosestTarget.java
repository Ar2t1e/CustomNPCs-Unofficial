package noppes.npcs.ai.target;

import java.util.function.Predicate;
import javax.annotation.Nullable;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.scores.Team;
import noppes.npcs.constants.EnumSeeTarget;
import noppes.npcs.entity.EntityNPCInterface;
import org.jetbrains.annotations.NotNull;

/*
* AI to find nearest target to attack:
*/
public class EntityAIClosestTarget<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {

   private int unseenTicks1;

   public EntityAIClosestTarget(EntityNPCInterface npc, Class<T> c, int range, EnumSeeTarget mustSee, boolean mustReach, @Nullable Predicate<LivingEntity> selector) {
      super(npc, c, range, mustSee != EnumSeeTarget.NONE && mustSee != EnumSeeTarget.BLIND, mustReach, selector);
      if (npc.ais.attackInvisible) { targetConditions.ignoreInvisibilityTesting(); }
      if (mustSee == EnumSeeTarget.NONE || mustSee == EnumSeeTarget.BLIND) { targetConditions.ignoreLineOfSight(); }
   }

   @Override
   public void start() {
      unseenTicks1 = 0;
      mob.setTarget(target);
      super.start();
   }

   @Override
   public void stop() {
      if (mob.getTarget() != null) { mob.setTarget(null); }
      targetMob = null;
   }

   @Override
   public boolean canContinueToUse() {
      LivingEntity target = mob.getTarget();
      if (target == null) { target = targetMob; }
      if (target == null || !mob.canAttack(target)) { return false; }
      Team team = mob.getTeam();
      Team team1 = target.getTeam();
      if (team != null && team1 == team) { return false; }
      double dist = getFollowDistance();
      if (mob.distanceToSqr(target) > dist * dist) { return false; }
      if (mustSee) {
         if (mob.getSensing().hasLineOfSight(target)) { unseenTicks1 = 0; }
         else if (++unseenTicks1 > reducedTickDelay(unseenMemoryTicks)) { return false; }
      }
      mob.setTarget(target);
      return true;
   }

   @Override
   protected @NotNull AABB getTargetSearchArea(double value) { return mob.getBoundingBox().inflate(value, value, value); }

}
