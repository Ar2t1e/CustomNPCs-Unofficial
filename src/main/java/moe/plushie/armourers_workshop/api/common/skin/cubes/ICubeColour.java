package moe.plushie.armourers_workshop.api.common.skin.cubes;

import net.minecraft.nbt.NBTTagCompound;

@SuppressWarnings("unused")
public interface ICubeColour {

	byte[] getBlue();

	byte getBlue(int side);

	byte[] getGreen();

	byte getGreen(int side);

	byte[] getPaintType();

	byte getPaintType(int side);

	byte[] getRed();

	byte getRed(int side);

	void readFromNBT(NBTTagCompound compound);

	void setBlue(byte blue, int side);

	@Deprecated
	void setColour(int colour);

	void setColour(int colour, int side);

	void setGreen(byte green, int side);

	void setPaintType(byte type, int side);

	void setRed(byte red, int side);

	void writeToNBT(NBTTagCompound compound);

}
