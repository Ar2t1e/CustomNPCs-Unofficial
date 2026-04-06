package noppes.npcs.api.item;

import noppes.npcs.api.interfaces.ParamName;

@SuppressWarnings("all")
public interface IItemScripted extends IItemStack {

   boolean hasTexture(@ParamName("damage") int damage);

   @Deprecated
   String getTexture(@ParamName("damage") int damage);

   String getTexture();

   @Deprecated
   void setTexture(@ParamName("damage") int damage, @ParamName("texture") String texture);

   void setTexture(@ParamName("texture") String texture);

   void setMaxStackSize(@ParamName("size") int size);

   double getDurabilityValue();

   void setDurabilityValue(@ParamName("value") float value);

   boolean getDurabilityShow();

   void setDurabilityShow(@ParamName("bo") boolean bo);

   int getDurabilityColor();

   void setDurabilityColor(@ParamName("color") int color);

   int getColor();

   void setColor(@ParamName("color") int color);

}
