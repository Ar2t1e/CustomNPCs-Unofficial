package noppes.npcs.api.mixin.stats;

import net.minecraft.stats.RecipeBook;
import net.minecraft.world.entity.player.Player;

public interface IRecipeBookMixin {

    RecipeBook npcs$copyToNew(boolean isGlobal, Player player);

    boolean npcs$checkRecipes();

}
