package noppes.npcs.mixin.world.item.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = RecipeManager.class, priority = 502)
public interface IRecipeManagerMixin {

    @Accessor Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> getRecipes(); // ImmutableMap

    @Accessor void setRecipes(Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> newRecipes); // ImmutableMap

    @Accessor Map<ResourceLocation, Recipe<?>> getByName();

    @Accessor void setByName(Map<ResourceLocation, Recipe<?>> newByName);

}
