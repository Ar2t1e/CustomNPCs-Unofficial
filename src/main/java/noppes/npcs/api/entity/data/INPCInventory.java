package noppes.npcs.api.entity.data;

import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.item.IItemStack;

import java.util.List;
import java.util.Map;

public interface INPCInventory {

   IItemStack getRightHand();

   void setRightHand(@ParamName("item") IItemStack item);

   IItemStack getLeftHand();

   void setLeftHand(@ParamName("item") IItemStack item);

   IItemStack getProjectile();

   void setProjectile(@ParamName("item") IItemStack item);

   IItemStack getArmor(@ParamName("slot") int slot);

   void setArmor(@ParamName("slot") int slot, @ParamName("item") IItemStack item);

   IItemStack getDropItem(@ParamName("slot") int slot);

   boolean removeDrop(@ParamName("slot") int slot);

   int getExpMin();

   int getExpMax();

   int getExpRNG();

   void setExp(@ParamName("min") int min, @ParamName("max") int max);

   // New from Unofficial (BetaZavr)
   ICustomDrop addDropItem(@ParamName("item") IItemStack item, @ParamName("chance") double chance);

   Map<IEntity<?>, List<IItemStack>> createDrops(@ParamName("lootType") int lootType, @ParamName("baseChance") double baseChance);

   ICustomDrop[] getDrops();

}
