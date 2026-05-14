package noppes.npcs.roles.companion;

import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.constants.EnumCompanionJobs;

public class CompanionFarmer extends CompanionJobInterface {

	public boolean isStanding = false;

	@Override
	public NBTTagCompound getNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setBoolean("CompanionFarmerStanding", isStanding);
		return compound;
	}

	@Override
	public boolean isSelfSufficient() { return isStanding; }

    @Override
	public void setNBT(NBTTagCompound compound) { isStanding = compound.getBoolean("CompanionFarmerStanding"); }

	@Override
	public EnumCompanionJobs getType() { return EnumCompanionJobs.FARMER; }

}
