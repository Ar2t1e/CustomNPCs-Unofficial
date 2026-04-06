package noppes.npcs.api.wrapper;

import java.util.Objects;
import java.util.UUID;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.IEntityItem;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.mixin.world.entity.item.IItemEntityMixin;
import noppes.npcs.util.ValueUtil;

public class EntityItemWrapper<T extends ItemEntity> extends EntityWrapper<T> implements IEntityItem<T> {

   public EntityItemWrapper(T entity) {
      super(entity);
   }

   public String getOwner() {
      return this.entity.getOwner() == null ? null : this.entity.getOwner().getUUID().toString();
   }

   public void setOwner(String uuid) {
      this.entity.setThrower(UUID.fromString(uuid));
   }

   public int getPickupDelay() {
      return ((IItemEntityMixin) entity).getPickupDelay();
   }

   public void setPickupDelay(int delay) {
      this.entity.setPickUpDelay(delay);
   }

   public int getType() {
      return 6;
   }

   public long getAge() {
      return this.entity.getAge();
   }

   public void setAge(long age) {
      ((IItemEntityMixin) entity).setAge((int) ValueUtil.correctLong(age, -2147483648L, 2147483647L));
   }

   public int getLifeSpawn() {
      return this.entity.lifespan;
   }

   public void setLifeSpawn(int age) {
      this.entity.lifespan = age;
   }

   public IItemStack getItem() {
      return Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(this.entity.getItem());
   }

   public void setItem(IItemStack item) {
      ItemStack stack = item == null ? ItemStack.EMPTY : item.getMCItemStack();
      this.entity.setItem(stack);
   }

}
