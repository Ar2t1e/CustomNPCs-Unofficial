package noppes.npcs.roles.companion;

import net.minecraft.nbt.CompoundTag;
import noppes.npcs.constants.EnumCompanionJobs;

public class CompanionFarmer extends CompanionJobInterface {

   public boolean isStanding = false;

   public CompoundTag getNBT() {
      CompoundTag compound = new CompoundTag();
      compound.putBoolean("CompanionFarmerStanding", this.isStanding);
      return compound;
   }

   public void setNBT(CompoundTag compound) {
      this.isStanding = compound.getBoolean("CompanionFarmerStanding");
   }

   public EnumCompanionJobs getType() {
      return EnumCompanionJobs.FARMER;
   }

   public boolean isSelfSufficient() {
      return this.isStanding;
   }

}
