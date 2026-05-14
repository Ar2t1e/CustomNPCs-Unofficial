package noppes.npcs.roles.companion;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.constants.EnumCompanionJobs;
import noppes.npcs.constants.EnumGuiType;

public class CompanionTrader extends CompanionJobInterface {

	@Override
	public NBTTagCompound getNBT() { return new NBTTagCompound(); }

	@Override
	public void setNBT(NBTTagCompound compound) { }

	@Override
	public EnumCompanionJobs getType() { return EnumCompanionJobs.SHOP; }

	public void interact(EntityPlayer playerIn) {
		if (playerIn instanceof EntityPlayerMP) {
			NoppesUtilServer.sendOpenGui((EntityPlayerMP) playerIn, EnumGuiType.CompanionTrader, npc);
		}
	}

}
