package noppes.npcs.api.handler.capability;

import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.interfaces.ParamName;

public interface IItemStackWrapperHandler {

	NBTTagCompound getMCNbt();

	void setMCNbt(@ParamName("compound") NBTTagCompound compound);

}
