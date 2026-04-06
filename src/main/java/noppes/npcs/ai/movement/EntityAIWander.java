package noppes.npcs.ai.movement;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;
import noppes.npcs.CustomNpcs;
import noppes.npcs.ai.selector.NPCInteractSelector;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAIWander extends Goal {

   private final EntityNPCInterface entity;
   public final Predicate<? super Entity> selector;
   private double x;
   private double y;
   private double zPosition;
   private EntityNPCInterface nearbyNPC;

   public EntityAIWander(EntityNPCInterface npc) {
      this.entity = npc;
      this.setFlags(EnumSet.of(Flag.MOVE));
      this.selector = new NPCInteractSelector(npc);
   }

   @Override
   public boolean canUse() {
      if (this.entity.getNoActionTime() >= 100 || !this.entity.getNavigation().isDone() || this.entity.isInteracting() || this.entity.isPassenger() || this.entity.ais.movingPause && this.entity.getRandom().nextInt(80) != 0) {
         return false;
      } else {
         if (this.entity.ais.npcInteracting && this.entity.getRandom().nextInt(this.entity.ais.movingPause ? 6 : 16) == 1) {
            this.nearbyNPC = this.getNearbyNPC();
         }

         if (this.nearbyNPC != null) {
            this.x = Mth.floor(this.nearbyNPC.getX());
            this.y = Mth.floor(this.nearbyNPC.getY());
            this.zPosition = Mth.floor(this.nearbyNPC.getZ());
            this.nearbyNPC.addInteract(this.entity);
         } else {
            Vec3 vec = this.getVec();
            if (vec == null) {
               return false;
            }

            this.x = vec.x;
            this.y = vec.y;
            if (this.entity.ais.movementType == 1) {
               this.y = this.entity.getStartYPos() + (double)this.entity.getRandom().nextFloat() * 0.75D * (double)this.entity.ais.walkingRange;
            }

            this.zPosition = vec.z;
         }

         return true;
      }
   }

   @Override
   public void tick() {
      if (this.nearbyNPC != null) {
         this.nearbyNPC.getNavigation().stop();
      }
   }

   private EntityNPCInterface getNearbyNPC() {
      List<Entity> list = this.entity.level().getEntities(entity,
              this.entity.getBoundingBox().inflate(this.entity.ais.walkingRange,
                      (this.entity.ais.walkingRange > 7) ? 7.0 : this.entity.ais.walkingRange,
                      this.entity.ais.walkingRange),
              selector);
      Iterator<?> ita = list.iterator();
      while(true) {
         EntityNPCInterface npc;
         do {
            if (!ita.hasNext()) {
               if (list.isEmpty()) {
                  return null;
               }
               return (EntityNPCInterface) list.get(this.entity.getRandom().nextInt(list.size()));
            }
            npc = (EntityNPCInterface)ita.next();
         } while(npc.ais.stopAndInteract && !npc.isAttacking() && npc.isAlive() && !this.entity.faction.isAggressiveToNpc(npc));
         ita.remove();
      }
   }

   private Vec3 getVec() {
      if (this.entity.ais.walkingRange > 0) {
         BlockPos start = new BlockPos((int)this.entity.getStartXPos(), (int)this.entity.getStartYPos(), (int)this.entity.getStartZPos());
         int distance = (int)Math.sqrt(this.entity.blockPosition().distSqr(start));
         int range = Math.min(this.entity.ais.walkingRange, CustomNpcs.NpcNavRange);
         if (range - distance < 4) {
            Vec3 pos2 = new Vec3((this.entity.getX() + (double)start.getX()) / 2.0D, (this.entity.getY() + (double)start.getY()) / 2.0D, (this.entity.getZ() + (double)start.getZ()) / 2.0D);
            return DefaultRandomPos.getPosTowards(this.entity, range / 2, Math.min(range / 2, 7), pos2, 1.5707963267948966D);
         } else {
            return DefaultRandomPos.getPos(this.entity, range / 2, Math.min(range / 2, 7));
         }
      } else {
         return DefaultRandomPos.getPos(this.entity, CustomNpcs.NpcNavRange, 7);
      }
   }

   @Override
   public boolean canContinueToUse() {
      if (this.nearbyNPC != null && (!this.selector.apply(this.nearbyNPC) || this.entity.isInRange(this.nearbyNPC, this.entity.getBbWidth()))) {
         return false;
      } else {
         return !this.entity.getNavigation().isDone() && this.entity.isAlive() && !this.entity.isInteracting();
      }
   }

   @Override
   public void start() {
      this.entity.getNavigation().moveTo(this.entity.getNavigation().createPath(this.x, this.y, this.zPosition, 0), 1.0D);
   }

   @Override
   public void stop() {
      if (this.nearbyNPC != null && this.entity.isInRange(this.nearbyNPC, 3.5D)) {
         EntityNPCInterface talk = this.entity;
         if (this.entity.getRandom().nextBoolean()) {
            talk = this.nearbyNPC;
         }
         Line line = talk.advanced.getNPCInteractLine();
         if (line == null) {
            line = new Line(".........");
         }
         line.setShowText(false);
         talk.saySurrounding(line);
         this.entity.addInteract(this.nearbyNPC);
         this.nearbyNPC.addInteract(this.entity);
      }
      this.nearbyNPC = null;
   }

}
