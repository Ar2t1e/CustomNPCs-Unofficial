package noppes.npcs.controllers.data;

import net.minecraft.nbt.NBTTagCompound;

public class FactionOption {

	public boolean decreaseFactionPoints = false;
	public int factionId = -1;
	public int factionPoints = 100;

	public FactionOption(int factionIdIn, int factionPointsIn, boolean take) {
		factionId = factionIdIn;
		factionPoints = factionPointsIn;
		decreaseFactionPoints = take;
	}

	public FactionOption(NBTTagCompound compound) { load(compound); }

	public void check() {
		if (factionPoints < 0) {
			factionPoints *= -1;
            decreaseFactionPoints = !decreaseFactionPoints;
		}
	}

	public void load(NBTTagCompound compound) {
		factionId = compound.getInteger("FactionID");
		decreaseFactionPoints = compound.getBoolean("IsDecrease");
		factionPoints = compound.getInteger("Points");
	}

	public NBTTagCompound save() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setInteger("FactionID", factionId);
		compound.setBoolean("IsDecrease", decreaseFactionPoints);
		compound.setInteger("Points", factionPoints);
		return compound;
	}

}
