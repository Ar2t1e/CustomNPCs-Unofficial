package noppes.npcs.ai;

import java.util.EnumSet;
import java.util.Iterator;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAILook extends Goal {

   private final EntityNPCInterface npc;
   private int idle = 0;
   private double lookX;
   private double lookZ;
   private boolean forced = false;
   private Entity forcedEntity = null;

   public EntityAILook(EntityNPCInterface npcIn) {
      npc = npcIn;
      setFlags(EnumSet.of(Flag.LOOK));
   }

   public boolean canUse() {
      if (forced) { return true; }
      if (!npc.isAttacking() && npc.getNavigation().isDone() && !npc.isSleeping() && npc.isAlive()) {
         if (!npc.isInteracting() && npc.ais.getStandingType() <= 0 && idle <= 0) { return npc.getRandom().nextFloat() < 0.004F; }
         return true;
      }
      return false;
   }

   public void start() {
      if (npc.ais.getStandingType() == 0 || npc.ais.getStandingType() == 3) {
         double var1 = 6.283185307179586D * npc.getRandom().nextDouble();
         if (npc.ais.getStandingType() == 3) {
            var1 = 0.017453292519943295D * npc.ais.orientation + 0.6283185307179586D + 1.8849555921538759D * npc.getRandom().nextDouble();
         }
         lookX = Math.cos(var1);
         lookZ = Math.sin(var1);
         idle = 20 + npc.getRandom().nextInt(20);
      }
   }

   public void rotate(Entity entity) {
      forced = true;
      forcedEntity = entity;
   }

   public void rotate(int degrees) {
      forced = true;
      npc.yHeadRot = npc.yBodyRot = (float)degrees;
      npc.setYRot((float)degrees);
   }

   public void stop() {
      forced = false;
      forcedEntity = null;
   }

   public void tick() {
      Entity lookat = null;
      if (forced && forcedEntity != null) { lookat = forcedEntity; }
      else if (npc.isInteracting()) {
         Iterator<LivingEntity> ita = npc.interactingEntities.iterator();
         double closestDistance = 12.0D;
         while(ita.hasNext()) {
            LivingEntity entity = ita.next();
            double distance = entity.distanceToSqr(npc);
            if (distance < closestDistance) {
               closestDistance = entity.distanceToSqr(npc);
               lookat = entity;
            }
            else if (distance > 12.0D) { ita.remove(); }
         }
      }
      else if (npc.ais.getStandingType() == 2) { lookat = npc.level().getNearestPlayer(npc, 16.0D); }
      if (lookat != null) { npc.getLookControl().setLookAt(lookat, 10.0F, npc.getMaxHeadXRot()); }
      else {
         if (idle > 0) {
            --idle;
            npc.getLookControl().setLookAt(npc.getX() + lookX, npc.getY() + npc.getEyeHeight(), npc.getZ() + lookZ, 10.0F, npc.getMaxHeadXRot());
         }
         if (npc.ais.getStandingType() == 1 && !forced) {
            npc.yHeadRot = npc.yBodyRot = npc.ais.orientation;
            npc.setYRot(npc.ais.orientation);
         }
      }
   }

}
