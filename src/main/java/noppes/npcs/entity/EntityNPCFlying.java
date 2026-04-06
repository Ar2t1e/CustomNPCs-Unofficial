package noppes.npcs.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public abstract class EntityNPCFlying extends EntityNPCInterface {

   public EntityNPCFlying(EntityType<? extends PathfinderMob> type, Level world) {
      super(type, world);
   }

   public boolean canFly() {
      return ais.movementType == 1;
   }

   public boolean causeFallDamage(float distance, float damageMultiplier, @NotNull DamageSource source) {
      return !canFly() && super.causeFallDamage(distance, damageMultiplier, source);
   }

   protected void checkFallDamage(double y, boolean onGroundIn, @NotNull BlockState state, @NotNull BlockPos pos) {
      if (!canFly()) {
         super.checkFallDamage(y, onGroundIn, state, pos);
      }
   }

   public void travel(@NotNull Vec3 v) {
      if (canFly() && (!isAlive() || !isVehicle() || !ais.mountControl || getControllingPassenger() == null)) {
         Vec3 m = getDeltaMovement();
         if (!isInWater() && ais.movementType == 2) {
            m = new Vec3(0.0D, -0.15D, 0.0D);
            move(MoverType.SELF, m);
         }
         else if (isInWater() && ais.movementType == 1) {
            moveRelative(0.02F, v);
            move(MoverType.SELF, m);
            m = getDeltaMovement().scale(0.8D);
         }
         else if (isInLava()) {
            moveRelative(0.02F, v);
            move(MoverType.SELF, m);
            m = getDeltaMovement().scale(0.5D);
         }
         else {
            BlockPos ground = new BlockPos((int)getX(), (int)(getY() - 1.0D), (int)getZ());
            float f = 0.91F;
            if (onGround()) {
               f = level().getBlockState(ground).getFriction(level(), ground, this) * 0.91F;
            }
            float f1 = 0.16277137F / (f * f * f);
            f = 0.91F;
            if (onGround()) {
               f = level().getBlockState(ground).getFriction(level(), ground, this) * 0.91F;
            }
            moveRelative(onGround() ? 0.1F * f1 : 0.02F, v);
            move(MoverType.SELF, getDeltaMovement());
            m = getDeltaMovement().scale(f);
         }
         setDeltaMovement(m);
         calculateEntityAnimation(false);
      }
      else { super.travel(v); }
   }

   public boolean onClimbable() {
      return false;
   }
}
