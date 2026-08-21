package noppes.npcs.api.handler.capability;

import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.interfaces.ParamName;

public interface IMarkDataHandler {

	NBTTagCompound getNBT();

	void setNBT(@ParamName("compound") NBTTagCompound compound);

}
