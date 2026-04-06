package noppes.npcs.api.handler.data;

import net.minecraft.nbt.CompoundTag;
import noppes.npcs.api.interfaces.ParamName;

public interface IPlayerData {

    CompoundTag save(@ParamName("compound") CompoundTag compound);

    void load(@ParamName("compound") CompoundTag compound);

}
