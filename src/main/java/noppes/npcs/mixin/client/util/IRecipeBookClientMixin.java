package noppes.npcs.mixin.client.util;

import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.client.util.RecipeBookClient;
import net.minecraft.creativetab.CreativeTabs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(value = RecipeBookClient.class, priority = 502)
public interface IRecipeBookClientMixin {

    @Accessor("RECIPES_BY_TAB") Map<CreativeTabs, List<RecipeList>> getCollectionsByTab();

}
