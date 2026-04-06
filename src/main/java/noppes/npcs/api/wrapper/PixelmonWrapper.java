package noppes.npcs.api.wrapper;

import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import noppes.npcs.api.entity.IPixelmon;
import noppes.npcs.controllers.PixelmonHelper;

public class PixelmonWrapper<T extends TamableAnimal> extends AnimalWrapper<T> implements IPixelmon<T> {

   public PixelmonWrapper(T entity) {
      super(entity);
   }

   public Object getPokemonData() {
      return PixelmonHelper.getPokemonData(this.entity);
   }

   public int getType() {
      return 8;
   }

   public boolean typeOf(int type) {
      return type == 8 || super.typeOf(type);
   }

}
