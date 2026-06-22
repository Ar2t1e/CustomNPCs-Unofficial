package noppes.npcs.ai.movement;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;
import noppes.npcs.CustomNpcs;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.Util;

public class EntityAIReturn extends Goal {

   protected static final int MaxTotalTicks = 600;
   protected final EntityNPCInterface npc;
   protected boolean wasAttacked = false;
   protected int stuckTicks = 0;
   protected int totalTicks = 0;
   protected int stuckCount = 0;
   protected Vec3 endPos;
   protected Vec3 preAttackPos;

   public EntityAIReturn(EntityNPCInterface npcIn) {
      npc = npcIn;
      setFlags(EnumSet.of(Flag.MOVE));
   }

   @Override
   public boolean canUse() {
      if (!npc.hasOwner() &&
              !npc.isPassenger() &&
              npc.ais.shouldReturnHome() &&
              !npc.isKilled() &&
              npc.getNavigation().isDone() &&
              !npc.isInteracting()) {
         // AI Attack
         if (npc.aiOwnerNPC != null && npc.getNavigation().isDone()) {
            totalTicks = 0;
            return false;
         }
         // AI Panic
         if (npc.ais.onAttack == 1) {
            if (npc.isOnFire() || npc.getTarget() != null) {
               totalTicks = 0;
               return false;
            }
         }
         BlockPos pos = new BlockPos((int) npc.getStartXPos(), (int) npc.getStartYPos(), (int) npc.getStartZPos());
         // Shelter at Night
         if (npc.ais.findShelter == 0 && (!npc.level().isDay() || npc.level().isRaining()) && !npc.level().dimensionType().hasSkyLight()
                 && (npc.level().canSeeSky(pos) || npc.level().getLightEmission(pos) <= 8)) { return false; }
         // Shelter at Day
         else if (npc.ais.findShelter == 1 && npc.level().isDay() && npc.level().canSeeSky(pos)) { return false; }
         if (npc.isAttacking()) {
            if (!wasAttacked) {
               wasAttacked = true;
               preAttackPos = new Vec3(npc.getX(), npc.getY(), npc.getZ());
            }
            return false;
         }
         else if (wasAttacked) { return true; }
         else if (!npc.homeDimensionId.equals(npc.level().dimension())) { return true; }
         else if (npc.ais.getMovingType() == 2 && npc.ais.distanceToSqrToPathPoint() < (double)(CustomNpcs.NpcNavRange * CustomNpcs.NpcNavRange)) { return false; }
         else if (npc.ais.getMovingType() == 1) { return !npc.isInRange(npc.getStartXPos(), -6666.0D, npc.getStartZPos(), npc.ais.walkingRange); }
         else if (npc.ais.getMovingType() == 0) { return !npc.isVeryNearAssignedPlace(); }
      }
      return false;
   }

   @Override
   public boolean canContinueToUse() {
      boolean bo = true;
      if (npc.ais.onAttack == 2) {
         double dist = Util.instance.distanceTo(npc.getX(), npc.getY(), npc.getZ(), npc.getStartXPos(), npc.getStartYPos(), npc.getStartZPos());
         bo = dist > npc.stats.aggroRange;
      }
      return npc.getHealth() > 0 && !npc.isFollower() && !npc.isKilled() && !npc.isAttacking() &&
              !npc.isVeryNearAssignedPlace() && !npc.isInteracting() && !npc.isPassenger() &&
              !(npc.getNavigation().isDone() && wasAttacked && isTooFar()) &&
              totalTicks <= MaxTotalTicks && bo;
   }

   @Override
   public void start() {
      stuckTicks = 0;
      totalTicks = 0;
      stuckCount = 0;
      if (!isAtHome()) { navigate(false); }
   }

   @Override
   public void stop() {
      wasAttacked = false;
      npc.getNavigation().stop();
   }

   @Override
   public void tick() {
      ++totalTicks;
      if (totalTicks > MaxTotalTicks) { forceBackHome(); }
      else if (isAtHome()) { npc.getNavigation().stop(); }
      else {
         if (stuckTicks > 0) { --stuckTicks; }
         else if (npc.getNavigation().isDone()) {
            ++stuckCount;
            stuckTicks = 10;
            if ((totalTicks <= 30 || !wasAttacked || !isTooFar()) && stuckCount <= 5) { navigate(stuckCount % 2 == 1); }
            else {
               stuckCount = 0;
               navigate(false);
            }
         }
         else { stuckCount = 0; }
      }
   }

   private boolean isTooFar() {
      if (endPos == null) { return false; }
      if (!npc.level().dimension().equals(npc.homeDimensionId)) { return true; }
      int allowedDistance = npc.stats.aggroRange * 2;
      if (npc.ais.getMovingType() == 1) { allowedDistance += npc.ais.walkingRange; }
      return Util.instance.distanceTo(npc.getX(), npc.getY(), npc.getZ(), endPos.x, endPos.y, endPos.z) > allowedDistance;
   }

   private void navigate(boolean towards) {
      if (!wasAttacked) { endPos = new Vec3(npc.getStartXPos(), npc.getStartYPos(), npc.getStartZPos()); }
      else { endPos = preAttackPos; }
      Vec3 pos = endPos;
      double range = Math.sqrt(npc.distanceToSqr(pos));
      if (range > (double) CustomNpcs.NpcNavRange || towards) {
         int distance = (int)range;
         if (distance > CustomNpcs.NpcNavRange) { distance = CustomNpcs.NpcNavRange / 2; }
         else { distance /= 2; }
         if (distance > 2) {
            pos = DefaultRandomPos.getPosTowards(npc, distance / 2, Math.min(distance / 2, 7), pos, Math.PI / 2.0d);
            if (pos == null) { pos = endPos; }
         }
      }
      npc.getNavigation().moveTo(pos.x, pos.y, pos.z, 2.0d);
   }

   // New from Unofficial (BetaZavr)
   private boolean isAtHome() {
      if (!wasAttacked) { return npc.isVeryNearAssignedPlace(); }
      if (preAttackPos == null) return false;
      double dist = npc.distanceToSqr(preAttackPos.x, preAttackPos.y, preAttackPos.z);
      return dist < 4.0D;
   }

   protected void forceBackHome() {
      if (wasAttacked) { npc.setTarget(null); }
      npc.getNavigation().stop();
      if (!npc.homeDimensionId.equals(npc.level().dimension())) {
         Util.instance.teleportEntity(npc.getServer(), npc, npc.homeDimensionId, npc.getStartXPos(), npc.getStartYPos(), npc.getStartZPos());
      }
      else { npc.setPos(endPos.x, endPos.y, endPos.z); }
   }

}
