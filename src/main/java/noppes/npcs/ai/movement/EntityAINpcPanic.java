package noppes.npcs.ai.movement;

import java.util.EnumSet;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.constants.AnimationKind;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAINpcPanic extends Goal {

   private final EntityNPCInterface npc;
   private final float speed;
   private double randPosX;
   private double randPosY;
   private double randPosZ;

   public EntityAINpcPanic(EntityNPCInterface npcIn, float runSpeed) {
      npc = npcIn;
      speed = runSpeed;
      setFlags(EnumSet.of(Flag.MOVE));
   }

   @Override
   public boolean canUse() {
      if ((npc.getTarget() != null || npc.isOnFire()) &&
              (!CustomNpcs.ShowCustomAnimation ||
                      !npc.animation.isAnimated(AnimationKind.ATTACKING, AnimationKind.INIT, AnimationKind.INTERACT, AnimationKind.DIES))) {
         Vec3 vec = DefaultRandomPos.getPos(npc, 5, 4);
         if (vec != null) {
            randPosX = vec.x;
            randPosY = vec.y;
            randPosZ = vec.z;
            return true;
         }
      }
      return false;
   }

   @Override
   public void start() { npc.getNavigation().moveTo(randPosX, randPosY, randPosZ, speed); }

   @Override
   public boolean canContinueToUse() { return npc.getTarget() != null && (npc.getNavigation().isDone() || !npc.isMoving()); }

}
