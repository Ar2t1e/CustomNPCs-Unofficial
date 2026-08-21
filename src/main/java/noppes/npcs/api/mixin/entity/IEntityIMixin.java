package noppes.npcs.api.mixin.entity;

import net.minecraft.entity.Entity;
import noppes.npcs.api.wrapper.data.Data;

public interface IEntityIMixin {

    void npcs$copyDataFromOld(Entity entity);

    Data npcs$getStoredData();

}
