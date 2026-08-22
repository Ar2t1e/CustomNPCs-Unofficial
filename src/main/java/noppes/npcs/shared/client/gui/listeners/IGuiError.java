package noppes.npcs.shared.client.gui.listeners;

import net.minecraft.nbt.NBTTagCompound;

public interface IGuiError {

	void setError(int type, NBTTagCompound nbt);

}
