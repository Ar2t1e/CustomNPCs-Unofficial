package noppes.npcs.api;

import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.interfaces.IgnoreForAPI;

@IgnoreForAPI
public interface ICompatibilty {

	int getVersion();

	void setVersion(int version);

	NBTTagCompound save(NBTTagCompound nbt);

}
