package noppes.npcs.ai.attack;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAIAttackTarget
        extends Goal {

   private final EntityNPCInterface npc;
   private LivingEntity entityTarget;
   private int attackTick;
   private Path entityPathEntity;
   private int field_75445_i;
   private BlockPos startPos;

   public EntityAIAttackTarget(EntityNPCInterface npcIn) {
      startPos = BlockPos.ZERO;
      attackTick = 0;
      npc = npcIn;
      setFlags(EnumSet.of(Flag.MOVE));
   }

   @Override
   public boolean canUse() {
      LivingEntity target = npc.getTarget();
      if (target != null && target.isAlive()) {
         int melee = npc.stats.ranged.getMeleeRange();
         if (npc.inventory.getProjectile() == null || melee > 0 && npc.isInRange(target, melee)) {
            if (target instanceof Player player) {
               if (CustomNpcs.EnableInvisibleNpcs && CustomNpcs.InvisibilityAlgorithm == 2 && !npc.display.isVisibleTo(player) && !player.isSpectator() && player.getMainHandItem().getItem() != CustomItems.wand) { return false; }
            }
            entityTarget = target;
            entityPathEntity = npc.getNavigation().createPath(target, 0);
            return entityPathEntity != null;
         }
      }
      return false;
   }

   @Override
   public boolean canContinueToUse() {
      entityTarget = npc.getTarget();
      if (entityTarget == null) { entityTarget = npc.getLastHurtByMob(); }
      if (entityTarget != null && entityTarget.isAlive()) {
         if (!npc.isInRange(entityTarget, npc.stats.aggroRange)) { return false; }
         int melee = npc.stats.ranged.getMeleeRange();
         if (melee > 0 && !npc.isInRange(entityTarget, melee)) { return false; }
         LivingEntity var3 = entityTarget;
         if (var3 instanceof Player player) {
            if (CustomNpcs.EnableInvisibleNpcs && CustomNpcs.InvisibilityAlgorithm == 2 && !npc.display.isVisibleTo(player) && !player.isSpectator() && player.getMainHandItem().getItem() != CustomItems.wand) {
               return false;
            }
         }
         return isWithinRestriction(entityTarget.blockPosition());
      }
      return false;
   }

   public boolean isWithinRestriction(BlockPos pos) {
      int range = Math.max(npc.stats.aggroRange * 2, 64);
      return startPos.distSqr(pos) < (double)(range * range);
   }

   @Override
   public void start() {
      startPos = npc.blockPosition();
      npc.getNavigation().moveTo(entityPathEntity, 1.3D);
      field_75445_i = 0;
   }

   @Override
   public void stop() {
      entityPathEntity = null;
      entityTarget = null;
      npc.getNavigation().stop();
   }

   @Override
   public boolean requiresUpdateEveryTick() { return true; }

   @Override
   public void tick() {
      npc.getLookControl().setLookAt(entityTarget, 30.0F, 30.0F);
      if (--field_75445_i <= 0) {
         field_75445_i = 4 + npc.getRandom().nextInt(7);
         npc.getNavigation().moveTo(entityTarget, 1.2999999523162842D);
      }
      attackTick = Math.max(attackTick - 1, 0);
      double y = entityTarget.getBoundingBox().minY;
      double distance = npc.distanceToSqr(entityTarget.getX(), y, entityTarget.getZ());
      double range = (float)(npc.stats.melee.getRange() * npc.stats.melee.getRange()) + entityTarget.getBbWidth();
      double minRange = npc.getBbWidth() * 2.0F * npc.getBbWidth() * 2.0F + entityTarget.getBbWidth();
      if (minRange > range) { range = minRange; }
      if (distance <= range && (npc.canSee(entityTarget) || distance < minRange) && attackTick <= 0) {
         attackTick = npc.stats.melee.getDelay();
         npc.swing(InteractionHand.MAIN_HAND);
         npc.doHurtTarget(entityTarget);
      }
   }

}
