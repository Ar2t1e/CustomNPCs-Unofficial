package noppes.npcs.ai.attack;

import java.util.EnumSet;
import java.util.List;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import noppes.npcs.constants.EnumSeeTarget;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.util.LogWriter;

public class EntityAIAvoidTarget extends Goal {

   private final EntityNPCInterface npc;
   private Entity closestLivingEntity;
   private final float distanceFromEntity;
   private Path entityPathEntity;
   private final PathNavigation entityPathNavigate;

    public EntityAIAvoidTarget(EntityNPCInterface npc) {
      this.npc = npc;
      this.distanceFromEntity = (float)this.npc.stats.aggroRange;
      this.entityPathNavigate = npc.getNavigation();
      this.setFlags(EnumSet.of(Flag.MOVE));
   }

   public boolean canUse() {
      LivingEntity target = this.npc.getTarget();
      if (target == null) {
         return false;
      } else {
         Class<? extends Entity> targetEntityClass = target.getClass();
         if (Player.class.isAssignableFrom(targetEntityClass)) {
            this.closestLivingEntity = this.npc.level().getNearestPlayer(this.npc, this.distanceFromEntity);
            if (this.closestLivingEntity == null) {
               return false;
            }
         } else {
            List<? extends Entity> var1 = this.npc.level().getEntitiesOfClass(targetEntityClass, this.npc.getBoundingBox().inflate(this.distanceFromEntity, 3.0D, this.distanceFromEntity));
            if (var1.isEmpty()) {
               return false;
            }
            this.closestLivingEntity = var1.get(0);
         }

         if (!npc.canSee(closestLivingEntity) && npc.ais.directLOS != EnumSeeTarget.NONE && npc.ais.directLOS != EnumSeeTarget.BLIND) {
            return false;
         }
         Vec3 var2 = DefaultRandomPos.getPosAway(this.npc, 16, 7, new Vec3(this.closestLivingEntity.getX(), this.closestLivingEntity.getY(), this.closestLivingEntity.getZ()));
         if (var2 != null && var2 != Vec3.ZERO) {
            if (this.closestLivingEntity.distanceToSqr(var2.x, var2.y, var2.z) < this.closestLivingEntity.distanceToSqr(this.npc)) {
               return false;
            } else {
               this.entityPathEntity = this.entityPathNavigate.createPath(var2.x, var2.y, var2.z, 0);
               return this.entityPathEntity != null;
            }
         } else {
            return false;
         }
      }
   }

   public boolean canContinueToUse() {
      return !this.entityPathNavigate.isDone();
   }

   public void start() {
      this.entityPathNavigate.moveTo(this.entityPathEntity, 1.0D);
   }

   public void stop() {
      this.closestLivingEntity = null;
      this.npc.setTarget(null);
   }

   public void tick() {
      if (this.npc.isInRange(this.closestLivingEntity, 7.0D)) {
         this.npc.getNavigation().setSpeedModifier(1.2D);
      } else {
         this.npc.getNavigation().setSpeedModifier(1.0D);
      }

   }
}
