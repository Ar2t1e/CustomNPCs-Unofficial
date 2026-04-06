package noppes.npcs.roles.companion;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import noppes.npcs.constants.EnumCompanionJobs;
import noppes.npcs.entity.EntityNPCInterface;

public class CompanionGuard extends CompanionJobInterface {

   public boolean isStanding = false;

   public CompoundTag getNBT() {
      CompoundTag compound = new CompoundTag();
      compound.putBoolean("CompanionGuardStanding", this.isStanding);
      return compound;
   }

   public void setNBT(CompoundTag compound) {
      this.isStanding = compound.getBoolean("CompanionGuardStanding");
   }

   public boolean isEntityApplicable(Entity entity) {
      if (!(entity instanceof Player) && !(entity instanceof EntityNPCInterface)) {
         if (entity instanceof Creeper) {
            return false;
         } else {
            return entity instanceof Monster;
         }
      } else {
         return false;
      }
   }

   public boolean isSelfSufficient() {
      return this.isStanding;
   }

   public EnumCompanionJobs getType() {
      return EnumCompanionJobs.GUARD;
   }

}
