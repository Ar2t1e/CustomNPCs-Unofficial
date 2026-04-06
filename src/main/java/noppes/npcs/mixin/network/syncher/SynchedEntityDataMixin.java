package noppes.npcs.mixin.network.syncher;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.annotation.Nullable;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.DataItem;
import noppes.npcs.client.ISynchedEntityData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = SynchedEntityData.class, priority = 498)
public class SynchedEntityDataMixin implements ISynchedEntityData {

   @Final @Shadow private Int2ObjectMap<DataItem<?>> itemsById = new Int2ObjectOpenHashMap<>();
   @Final @Shadow private ReadWriteLock lock = new ReentrantReadWriteLock();

   @Nullable
   @SuppressWarnings("unchecked")
   public <T> List<DataItem<T>> cnpcs$getAll() {
      List<DataItem<T>> list = null;
      lock.readLock().lock();
      DataItem<T> dataitem;
      for(ObjectIterator<?> var2 = itemsById.values().iterator(); var2.hasNext(); list.add(new DataItem<>(dataitem.getAccessor(), (T) dataitem.value()))) {
         dataitem = (DataItem<T>) var2.next();
         if (list == null) {
            list = Lists.newArrayList();
         }
      }
      lock.readLock().unlock();
      return list;
   }

}
