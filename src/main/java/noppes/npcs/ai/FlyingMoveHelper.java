package noppes.npcs.ai;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.Objects;

public class FlyingMoveHelper extends MoveControl {

   private final EntityNPCInterface entity;
   private int courseChangeCooldown;

   public FlyingMoveHelper(EntityNPCInterface npc) {
      super(npc);
      this.entity = npc;
   }

   public void tick() {
      if (this.operation == Operation.MOVE_TO && this.courseChangeCooldown-- <= 0) {
         this.courseChangeCooldown = 4;
         Vec3 vector3d = new Vec3(this.getWantedX() - this.entity.getX(), this.getWantedY() - this.entity.getY(), this.getWantedZ() - this.entity.getZ());
         double length = vector3d.length();
         vector3d = vector3d.normalize();
         if (length > 0.5D && this.isNotColliding(vector3d, Mth.ceil(length))) {
            double speed = Objects.requireNonNull(this.entity.getAttribute(Attributes.MOVEMENT_SPEED)).getValue() / 2.5D;
            if (length < 3.0D && speed > 0.10000000149011612D) {
               speed = 0.10000000149011612D;
            }
            Vec3 m = this.entity.getDeltaMovement().add(vector3d.scale(speed));
            this.entity.setDeltaMovement(m);
            this.entity.setYRot(-((float)Math.atan2(m.x, m.z)) * 180.0F / 3.1415927F);
            this.entity.yBodyRot = this.entity.getYRot();
         } else {
            this.operation = Operation.WAIT;
         }
      }

   }

   private boolean isNotColliding(Vec3 vec, int length) {
      AABB axisAlignedBB = this.entity.getBoundingBox();
      for(int i = 1; i < length; ++i) {
         axisAlignedBB = axisAlignedBB.move(vec);
         if (!this.entity.level().noCollision(this.entity, axisAlignedBB)) {
            return false;
         }
      }
      return true;
   }
}
