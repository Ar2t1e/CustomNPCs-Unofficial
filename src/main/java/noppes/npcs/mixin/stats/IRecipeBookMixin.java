package noppes.npcs.mixin.stats;

import net.minecraft.stats.RecipeBook;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.BitSet;

@Mixin(value = RecipeBook.class, priority = 502)
public interface IRecipeBookMixin {

    @Accessor("recipes") BitSet getKnown();

    @Accessor("newRecipes") BitSet getHighlight();

}
