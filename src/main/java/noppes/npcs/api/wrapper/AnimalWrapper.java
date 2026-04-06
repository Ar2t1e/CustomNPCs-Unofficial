package noppes.npcs.api.wrapper;

import net.minecraft.world.entity.animal.Animal;
import noppes.npcs.api.entity.IAnimal;

public class AnimalWrapper<T extends Animal> extends EntityLivingWrapper<T> implements IAnimal<T> {

   public AnimalWrapper(T entity) {
      super(entity);
   }

   public int getType() {
      return 4;
   }

   public boolean typeOf(int type) {
      return type == 4 || super.typeOf(type);
   }

}
