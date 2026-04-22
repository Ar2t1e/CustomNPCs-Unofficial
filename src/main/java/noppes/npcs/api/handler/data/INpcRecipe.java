package noppes.npcs.api.handler.data;

import net.minecraft.world.item.ItemStack;
import noppes.npcs.api.interfaces.ParamName;

public interface INpcRecipe {

   String getName();

   boolean isGlobal();

   @SuppressWarnings("unused")
   void setIsGlobal(@ParamName("bo") boolean bo);

   boolean getIgnoreNBT();

   void setIgnoreNBT(@ParamName("bo") boolean bo);

   boolean getIgnoreDamage();

   void setIgnoreDamage(@ParamName("bo") boolean bo);

   int getWidth();

   int getHeight();

   ItemStack getResult();

   ItemStack[] getRecipe();

   @SuppressWarnings("unused")
   void saves(@ParamName("bo") boolean bo);

   @SuppressWarnings("unused")
   boolean saves();

   void save();

   void delete();

}
