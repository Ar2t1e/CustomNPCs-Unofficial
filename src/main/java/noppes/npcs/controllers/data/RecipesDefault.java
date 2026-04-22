package noppes.npcs.controllers.data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.RecipeController;

public class RecipesDefault {

   public static void addRecipe(String name, Object ob, boolean isGlobal, Object... recipe) {
      ItemStack stack;
      if (ob instanceof Item item) {
         stack = new ItemStack(item);
      } else if (ob instanceof Block block) {
         stack = new ItemStack(block);
      } else {
         stack = (ItemStack) ob;
      }

      RecipeCarpentry recipeAnvil = new RecipeCarpentry(new ResourceLocation(CustomNpcs.MODID, name));
      recipeAnvil.isGlobal = isGlobal;
      recipeAnvil = RecipeCarpentry.createRecipe(new ResourceLocation(CustomNpcs.MODID, name), recipeAnvil, stack, recipe);
      RecipeController.getInstance().saveRecipe(recipeAnvil);
   }

   public static void loadDefaultRecipes(int versionIn) {
      if (versionIn < 0) {
         addRecipe("npc_wand", CustomItems.wand, true, "XX", " Y", " Y", 'X', Items.BREAD, 'Y', Items.STICK);
         addRecipe("mob_cloner", CustomItems.cloner, true, "XX", "XY", " Y", 'X', Items.BREAD, 'Y', Items.STICK);
      }
   }

}
