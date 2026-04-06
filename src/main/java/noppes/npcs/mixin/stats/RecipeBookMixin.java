package noppes.npcs.mixin.stats;

import com.google.common.collect.Sets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.api.mixin.stats.IRecipeBookMixin;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(value = RecipeBook.class, priority = 499)
public class RecipeBookMixin implements IRecipeBookMixin {

    @Mutable
    @Final
    @Shadow
    protected Set<ResourceLocation> known; // recipes

    @Mutable
    @Final
    @Shadow
    protected Set<ResourceLocation> highlight; // newRecipes

    /**
     * @author BetaZavr
     * @reason Custom recipes can be deleted during gameplay
     */
    @Inject(method = "add(Lnet/minecraft/world/item/crafting/Recipe;)V", at = @At("HEAD"), cancellable = true)
    public void npcs$add(Recipe<?> recipe, CallbackInfo ci) {
        ci.cancel();
        RecipeType<?> recipeType = ForgeRegistries.RECIPE_TYPES.getValue(recipe.getId());
        if (recipeType != null && !recipe.isSpecial()) { known.add(recipe.getId()); }
    }

    @Override
    public RecipeBook npcs$copyToNew(boolean isGlobal, Player player) {
        RecipeBook newBook = new RecipeBook();
        for (ResourceLocation id : known) {
            RecipeType<?> recipeType = ForgeRegistries.RECIPE_TYPES.getValue(id);
            if (recipeType != null) {
                ((RecipeBookMixin) (Object) newBook).known.add(id);
            }
        }
        for (ResourceLocation id : highlight) {
            RecipeType<?> recipeType = ForgeRegistries.RECIPE_TYPES.getValue(id);
            if (recipeType != null) {
                ((RecipeBookMixin) (Object) newBook).highlight.add(id);
            }
        }
        return newBook;
    }

    @Override
    public boolean npcs$checkRecipes() {
        boolean bo = true;
        Set<ResourceLocation> newKnown = Sets.newHashSet();
        Set<ResourceLocation> newHighlight = Sets.newHashSet();
        for (ResourceLocation id : known) {
            RecipeType<?> recipeType = ForgeRegistries.RECIPE_TYPES.getValue(id);
            if (recipeType != null) { newKnown.add(id); } else { bo = false; }
        }
        for (ResourceLocation id : highlight) {
            RecipeType<?> recipeType = ForgeRegistries.RECIPE_TYPES.getValue(id);
            if (recipeType != null) { newHighlight.add(id); } else { bo = false; }
        }
        if (!bo) {
            known.clear();
            known.addAll(newKnown);
            highlight.clear();
            highlight.addAll(newHighlight);
        }
        return bo;
    }

}
