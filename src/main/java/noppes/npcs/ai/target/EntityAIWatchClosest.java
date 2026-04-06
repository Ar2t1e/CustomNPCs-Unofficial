package noppes.npcs.ai.target;

import java.util.EnumSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAIWatchClosest extends Goal {

   protected final Class<? extends LivingEntity> watchedClass;
   protected final TargetingConditions predicate;
   protected final EntityNPCInterface npc;
   protected final float maxDistance;
   protected Entity closestEntity;
   protected int lookTime;

   public EntityAIWatchClosest(EntityNPCInterface npcIn, Class<? extends LivingEntity> limbSwingAmountClass, float distance) {
      npc = npcIn;
      watchedClass = limbSwingAmountClass;
      maxDistance = distance;
      setFlags(EnumSet.of(Flag.LOOK));
      predicate = TargetingConditions.forNonCombat().range(distance);
   }

   public boolean canUse() {
       if (npc.getRandom().nextFloat() < 0.002F && !npc.isInteracting()) {
         if (npc.getTarget() != null) { closestEntity = npc.getTarget(); }
         if (watchedClass == Player.class) { closestEntity = npc.level().getNearestPlayer(npc, maxDistance); }
         else { closestEntity = npc.level().getNearestEntity(watchedClass, predicate, npc, npc.getX(), npc.getEyeY(), npc.getZ(), npc.getBoundingBox().inflate(maxDistance, 3.0D, maxDistance)); }
      }
      return closestEntity != null;
   }

   public boolean canContinueToUse() {
      if (!npc.isInteracting() && !npc.isAttacking() && closestEntity.isAlive() && npc.isAlive()) {
         return npc.isInRange(closestEntity, maxDistance) && lookTime > 0;
      }
      return false;
   }

   public void start() { lookTime = 60 + npc.getRandom().nextInt(60); }

   public void stop() { closestEntity = null; }

   public void tick() {
      npc.getLookControl().setLookAt(closestEntity.getX(), closestEntity.getY() + (double) closestEntity.getEyeHeight(), closestEntity.getZ(), 10.0F, (float) npc.getMaxHeadXRot());
      --lookTime;
   }

}
