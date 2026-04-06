package noppes.npcs.ai.attack;

import java.util.EnumSet;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.Util;

import javax.annotation.Nonnull;

public class EntityAIPounceTarget extends Goal {

   protected final @Nonnull EntityNPCInterface npc;
   protected final float leapSpeed = 1.3F;
   protected LivingEntity leapTarget;

   public EntityAIPounceTarget(@Nonnull EntityNPCInterface npcIn) {
      npc = npcIn;
      setFlags(EnumSet.of(Flag.JUMP));
   }

   @Override
   public boolean canUse() {
      if (npc.onGround()) {
         leapTarget = npc.getTarget();
         if (leapTarget != null && npc.getSensing().hasLineOfSight(leapTarget)) {
            return !npc.isInRange(leapTarget, 4.0D) && npc.isInRange(leapTarget, 8.0D) && npc.getRandom().nextInt(5) == 0;
         }
      }
      return false;
   }

   @Override
   public boolean canContinueToUse() {
      return !npc.onGround();
   }

   @Override
   public void start() {
      Util.instance.jumpTowards(leapSpeed, npc, new Vec3(leapTarget.getX(), leapTarget.getBoundingBox().minY, leapTarget.getZ()));
   }

}
