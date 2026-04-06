package noppes.npcs.api.entity;

import net.minecraft.world.entity.LivingEntity;
import noppes.npcs.api.entity.data.INpcAttribute;
import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.entity.data.IMark;
import noppes.npcs.api.item.IItemStack;

public interface IEntityLiving<T extends LivingEntity>
        extends IEntity<T> {

   float getHealth();

   void setHealth(@ParamName("health") float health);

   float getMaxHealth();

   void setMaxHealth(@ParamName("health") float health);

   boolean isAttacking();

   void setAttackTarget(@ParamName("living") IEntityLiving<T> living);

   IEntityLiving<T> getAttackTarget();

   IEntityLiving<T> getLastAttacked();

   int getLastAttackedTime();

   boolean canSeeEntity(@ParamName("entity") IEntity<?> entity);

   void swingMainhand();

   void swingOffhand();

   IItemStack getMainhandItem();

   void setMainhandItem(@ParamName("item") IItemStack item);

   IItemStack getOffhandItem();

   void setOffhandItem(@ParamName("item") IItemStack item);

   IItemStack getArmor(@ParamName("slot") int slot);

   void setArmor(@ParamName("slot") int slot, @ParamName("item") IItemStack item);

   void addPotionEffect(@ParamName("effect") int effect, @ParamName("duration") int duration,
                        @ParamName("strength") int strength, @ParamName("hideParticles") boolean hideParticles);

   void clearPotionEffects();

   int getPotionEffect(@ParamName("effect") int effect);

   IMark addMark(@ParamName("type") int type);

   void removeMark(@ParamName("mark") IMark mark);

   IMark[] getMarks();

   boolean isChild();

   T getMCEntity();

   float getMoveForward();

   void setMoveForward(@ParamName("move") float move);

   float getMoveStrafing();

   void setMoveStrafing(@ParamName("move") float move);

   float getMoveVertical();

   void setMoveVertical(@ParamName("move") float move);

   // New from Unofficial (BetaZavr)
   INpcAttribute getIAttribute(@ParamName("attributeName") String attributeName);

   String[] getIAttributeNames();

   INpcAttribute[] getIAttributes();

   INpcAttribute addAttribute(@ParamName("attribute") INpcAttribute attribute);

   INpcAttribute addAttribute(@ParamName("attributeName") String attributeName, @ParamName("baseValue") double baseValue,
                              @ParamName("minValue") double minValue, @ParamName("maxValue") double maxValue);

   boolean hasAttribute(@ParamName("attribute") INpcAttribute attribute);

   boolean hasAttribute(@ParamName("attributeName") String attributeName);

   boolean removeAttribute(@ParamName("attribute") INpcAttribute attribute);

   boolean removeAttribute(@ParamName("attributeName") String attributeName);

}
