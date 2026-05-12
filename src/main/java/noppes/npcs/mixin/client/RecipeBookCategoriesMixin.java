package noppes.npcs.mixin.client;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.RecipeBookCategories;
import noppes.npcs.client.util.ClientRecipeRegister;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(value = RecipeBookCategories.class, priority = 498)
public class RecipeBookCategoriesMixin {

    @Final @Shadow public static List<RecipeBookCategories> CRAFTING_CATEGORIES = ImmutableList.of(RecipeBookCategories.CRAFTING_SEARCH,
            RecipeBookCategories.CRAFTING_EQUIPMENT, RecipeBookCategories.CRAFTING_BUILDING_BLOCKS,
            RecipeBookCategories.CRAFTING_MISC, RecipeBookCategories.CRAFTING_REDSTONE,
            ClientRecipeRegister.CRAFTING_CUSTOM_GLOBAL_CATEGORY);

}
