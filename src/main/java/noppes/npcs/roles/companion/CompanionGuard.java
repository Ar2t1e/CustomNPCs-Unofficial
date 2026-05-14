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

   @Override
   public CompoundTag getNBT() {
      CompoundTag compound = new CompoundTag();
      compound.putBoolean("CompanionGuardStanding", isStanding);
      return compound;
   }

   @Override
   public void setNBT(CompoundTag compound) { isStanding = compound.getBoolean("CompanionGuardStanding"); }

   @Override
   public boolean isSelfSufficient() { return isStanding; }

   @Override
   public EnumCompanionJobs getType() { return EnumCompanionJobs.GUARD; }

   public boolean isEntityApplicable(Entity entity) {
      if (!(entity instanceof Player) && !(entity instanceof EntityNPCInterface)) {
         if (entity instanceof Creeper) { return false; }
         return entity instanceof Monster;
      }
      return false;
   }

}
