package noppes.npcs.api.handler.data;

import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.interfaces.ParamName;

public interface IPlayerData {

    NBTTagCompound save(@ParamName("compound") NBTTagCompound compound);

    void load(@ParamName("compound") NBTTagCompound compound);

}
