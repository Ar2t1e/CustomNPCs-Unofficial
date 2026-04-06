package noppes.npcs.api.handler;

import java.util.List;

import net.minecraft.world.item.ItemStack;
import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.handler.data.IRecipe;

@SuppressWarnings("all")
public interface IRecipeHandler {

   List<IRecipe> getGlobalList();

   List<IRecipe> getCarpentryList();

   IRecipe addRecipe(@ParamName("name") String name, @ParamName("global") boolean global, @ParamName("result") ItemStack result,
                     @ParamName("objects") Object... objects);

   IRecipe addRecipe(@ParamName("name") String name, @ParamName("global") boolean global, @ParamName("result") ItemStack result,
                     @ParamName("width") int width, @ParamName("height") int height, @ParamName("objects") ItemStack... objects);

   IRecipe delete(@ParamName("id") String id);

}
