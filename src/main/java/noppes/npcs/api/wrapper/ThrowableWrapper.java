package noppes.npcs.api.wrapper;

import net.minecraft.world.entity.projectile.ThrowableProjectile;
import noppes.npcs.api.entity.IThrowable;

public class ThrowableWrapper<T extends ThrowableProjectile> extends EntityWrapper<T> implements IThrowable<T> {


   public ThrowableWrapper(T entity) {
      super(entity);
   }

   public int getType() {
      return 11;
   }

   public boolean typeOf(int type) {
      return type == 11 || super.typeOf(type);
   }

}
