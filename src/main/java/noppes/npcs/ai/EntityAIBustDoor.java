package noppes.npcs.ai;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.DoorInteractGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;

public class EntityAIBustDoor extends DoorInteractGoal {

   private int breakingTime;
   private int field_75358_j = -1;

   public EntityAIBustDoor(Mob entity) {
      super(entity);
   }

   public boolean canUse() {
      return super.canUse() && !this.isOpen();
   }

   public void start() {
      super.start();
      this.breakingTime = 0;
   }

   public boolean canContinueToUse() {
      return this.breakingTime <= 240 && !this.isOpen() && this.mob.blockPosition().distSqr(this.doorPos) < 4.0D;
   }

   public void stop() {
      super.stop();
      this.mob.level().destroyBlockProgress(this.mob.getId(), this.doorPos, -1);
   }

   public void tick() {
      super.tick();
      if (this.mob.getRandom().nextInt(20) == 0) {
         this.mob.level().levelEvent(null, 1010, this.doorPos, 0);
         this.mob.swing(InteractionHand.MAIN_HAND);
      }

      ++this.breakingTime;
      int var1 = (int)((float)this.breakingTime / 240.0F * 10.0F);
      if (var1 != this.field_75358_j) {
         this.mob.level().destroyBlockProgress(this.mob.getId(), this.doorPos, var1);
         this.field_75358_j = var1;
      }

      if (this.breakingTime == 240) {
         this.mob.level().removeBlock(this.doorPos, false);
         this.mob.level().levelEvent(null, 1012, this.doorPos, 0);
         this.mob.level().levelEvent(null, 2001, this.doorPos, Block.getId(this.mob.level().getBlockState(this.doorPos)));
      }
   }

}
