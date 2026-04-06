package noppes.npcs.api.handler.data;

import net.minecraft.world.item.ItemStack;
import noppes.npcs.api.interfaces.ParamName;

public interface IRecipe {

   String getName();

   boolean isGlobal();

   void setIsGlobal(@ParamName("bo") boolean bo);

   boolean getIgnoreNBT();

   void setIgnoreNBT(@ParamName("bo") boolean bo);

   boolean getIgnoreDamage();

   void setIgnoreDamage(@ParamName("bo") boolean bo);

   int getWidth();

   int getHeight();

   ItemStack getResult();

   ItemStack[] getRecipe();

   void saves(@ParamName("bo") boolean bo);

   boolean saves();

   void save();

   void delete();

}
