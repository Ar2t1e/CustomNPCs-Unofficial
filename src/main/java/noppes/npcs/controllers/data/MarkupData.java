package noppes.npcs.controllers.data;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class MarkupData {

	public int id = -1;
	public int xp = 0;
	public int level = 0;
	public float buy = 0.0f;
	public float sell = 0.04f;

	public MarkupData(int levelIn, float buyIn, float sellIn, int xpIn) {
		level = levelIn;
		buy = buyIn;
		sell = sellIn;
		xp = xpIn;
	}

	public MarkupData(int idIn, int levelIn, int xpIn) {
		id = idIn;
		level = levelIn;
		xp = xpIn;
	}

	public MarkupData(NBTTagCompound data) { setNBT(data); }

	public void addXP(int xpIn) {
		xp += xpIn;
		if (xp < 0) { xp = 0; }
	}

	public NBTTagCompound getNBT() {
		NBTTagCompound data = new NBTTagCompound();
		data.setInteger("level", level);
		data.setInteger("xp", xp);
		data.setFloat("buy", buy);
		data.setFloat("sell", sell);
		return data;
	}

	public NBTBase getPlayerNBT() {
		NBTTagCompound data = new NBTTagCompound();
		data.setInteger("id", id);
		data.setInteger("xp", xp);
		data.setInteger("level", level);
		return data;
	}

	public void setNBT(NBTTagCompound data) {
		level = data.getInteger("level");
		xp = data.getInteger("xp");
		buy = data.getFloat("buy");
		sell = data.getFloat("sell");
	}

}
