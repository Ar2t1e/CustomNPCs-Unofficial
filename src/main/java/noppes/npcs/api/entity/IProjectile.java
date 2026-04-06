package noppes.npcs.api.entity;

import net.minecraft.world.entity.projectile.ThrowableProjectile;
import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.item.IItemStack;

public interface IProjectile<T extends ThrowableProjectile> extends IThrowable<T> {

   IItemStack getItem();

   void setItem(@ParamName("item") IItemStack item);

   boolean getHasGravity();

   void setHasGravity(@ParamName("bo") boolean bo);

   int getAccuracy();

   void setAccuracy(@ParamName("accuracy") int accuracy);

   void setHeading(@ParamName("entity") IEntity<?> entity);

   void setHeading(@ParamName("x") double x, @ParamName("y") double y, @ParamName("z") double z);

   void setHeading(@ParamName("yaw") float yaw, @ParamName("pitch") float pitch);

   void enableEvents();

}
