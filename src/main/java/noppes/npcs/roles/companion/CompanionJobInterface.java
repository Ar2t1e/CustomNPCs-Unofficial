package noppes.npcs.roles.companion;

import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.constants.EnumCompanionJobs;
import noppes.npcs.entity.EntityNPCInterface;

public abstract class CompanionJobInterface {

	public EntityNPCInterface npc;

	public abstract NBTTagCompound getNBT();

	public abstract void setNBT(NBTTagCompound compound);

	public boolean isSelfSufficient() { return false; }

	public void onUpdate() {}

	// New from Unofficial (GoodBird)
	public abstract EnumCompanionJobs getType();

}
