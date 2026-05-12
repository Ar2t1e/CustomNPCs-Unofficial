package noppes.npcs.client.util;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;

import java.util.List;

public class ClientRecipeRegister {
    public static final RecipeBookCategories CRAFTING_CUSTOM_GLOBAL_CATEGORY = RecipeBookCategories.create("CRAFTING_CUSTOM_GLOBAL_CATEGORY", new ItemStack(CustomItems.wand), new ItemStack(CustomItems.cloner));
    public static final RecipeBookCategories CRAFTING_CUSTOM_ANVIL_CATEGORY = RecipeBookCategories.create("CRAFTING_CUSTOM_ANVIL_CATEGORY", new ItemStack(CustomBlocks.carpenty));
    public static final List<RecipeBookCategories> CRAFTING_CUSTOM_ANVIL_CATEGORIES = ImmutableList.of(CRAFTING_CUSTOM_ANVIL_CATEGORY);
}
