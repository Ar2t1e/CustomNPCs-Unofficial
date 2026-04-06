package noppes.npcs.api.wrapper;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.trading.MerchantOffers;
import noppes.npcs.api.entity.IVillager;

public class VillagerWrapper<T extends Villager> extends EntityLivingWrapper<T> implements IVillager<T> {

   public VillagerWrapper(T entity) {
      super(entity);
   }

   @Override
   public String getProfession() { return entity.getVillagerData().getProfession().toString(); }

   @Override
   public String villagerType() { return entity.getVillagerData().getType().toString(); }

   @Override
   public int getType() {
      return 9;
   }

   @Override
   public boolean typeOf(int type) { return type == 9 || super.typeOf(type); }

   @Override
   public Container getMCVillagerContainer() { return entity.getInventory(); }

   @Override
   public MerchantOffers getMCRecipes() { return entity.getOffers(); }

}
