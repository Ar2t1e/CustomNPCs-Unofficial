package noppes.npcs.api.handler;

import java.util.List;

import net.minecraft.world.item.ItemStack;
import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.handler.data.INpcRecipe;

public interface IRecipeHandler {

   List<INpcRecipe> getAnvilRecipes(String group);

   List<INpcRecipe> getGlobalRecipes(String group);

   List<INpcRecipe> getAllAnvilRecipes();

   List<INpcRecipe> getAllGlobalRecipes();

   INpcRecipe addRecipe(@ParamName("name") String name, @ParamName("group") String group, @ParamName("global") boolean global, @ParamName("result") ItemStack result,
                        @ParamName("objects") Object... objects);

   INpcRecipe addRecipe(@ParamName("name") String name, @ParamName("group") String group, @ParamName("global") boolean global, @ParamName("result") ItemStack result,
                        @ParamName("width") int width, @ParamName("height") int height, @ParamName("objects") ItemStack... objects);

   boolean delete(@ParamName("id") String id);

}
