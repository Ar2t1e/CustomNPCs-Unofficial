package noppes.npcs.mixin.client;

import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(value = ClientRecipeBook.class, priority = 498)
public interface IClientRecipeBookMixin {

    @Accessor Map<RecipeBookCategories, List<RecipeCollection>> getCollectionsByTab(); // ImmutableMap

    @Accessor void setCollectionsByTab(Map<RecipeBookCategories, List<RecipeCollection>> newCollectionsByTab);

    @Accessor void setAllCollections(List<RecipeCollection> newAllCollections);

}
