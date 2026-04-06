package noppes.npcs.api;

import net.minecraft.nbt.CompoundTag;
import noppes.npcs.api.interfaces.IgnoreForAPI;
import noppes.npcs.api.interfaces.ParamName;

@IgnoreForAPI
public interface ICompatibilty {

   int getVersion();

   void setVersion(int version);

   CompoundTag save(@ParamName("compound") CompoundTag compound);

}
