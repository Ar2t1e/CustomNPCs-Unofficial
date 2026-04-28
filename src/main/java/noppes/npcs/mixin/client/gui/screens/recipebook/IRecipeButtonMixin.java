package noppes.npcs.mixin.client.gui.screens.recipebook;

import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(value = RecipeButton.class, priority = 502)
public interface IRecipeButtonMixin {

    @Invoker Recipe<?> invokeGetRecipe();

    @Invoker List<Recipe<?>> invokeGetOrderedRecipes();

}
