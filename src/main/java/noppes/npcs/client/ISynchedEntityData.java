package noppes.npcs.client;

import java.util.List;
import net.minecraft.network.syncher.SynchedEntityData.DataItem;

public interface ISynchedEntityData {

   <T> List<DataItem<T>> cnpcs$getAll();

}
