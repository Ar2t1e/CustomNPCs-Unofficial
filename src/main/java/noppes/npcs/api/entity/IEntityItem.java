package noppes.npcs.api.entity;

import net.minecraft.world.entity.item.ItemEntity;
import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.item.IItemStack;

public interface IEntityItem<T extends ItemEntity> extends IEntity<T> {

   String getOwner();

   void setOwner(@ParamName("uuid") String uuid);

   int getPickupDelay();

   void setPickupDelay(@ParamName("delay") int delay);

   long getAge();

   void setAge(@ParamName("age") long age);

   int getLifeSpawn();

   void setLifeSpawn(@ParamName("age") int age);

   IItemStack getItem();

   void setItem(@ParamName("item") IItemStack item);

}
