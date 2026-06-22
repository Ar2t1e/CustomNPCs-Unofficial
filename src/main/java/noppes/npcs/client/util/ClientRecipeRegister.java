package noppes.npcs.client.util;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.RecipeBookCategories;

import java.util.List;

public class ClientRecipeRegister {

    public static RecipeBookCategories CRAFTING_CUSTOM_GLOBAL_CATEGORY = RecipeBookCategories.create("CRAFTING_CUSTOM_GLOBAL_CATEGORY");
    public static RecipeBookCategories CRAFTING_CUSTOM_ANVIL_CATEGORY = RecipeBookCategories.create("CRAFTING_CUSTOM_ANVIL_CATEGORY");
    public static List<RecipeBookCategories> CRAFTING_CUSTOM_ANVIL_CATEGORIES = ImmutableList.of(CRAFTING_CUSTOM_ANVIL_CATEGORY);

}
