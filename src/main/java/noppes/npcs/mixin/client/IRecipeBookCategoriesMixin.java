package noppes.npcs.mixin.client;

import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = RecipeBookCategories.class, priority = 502)
public interface IRecipeBookCategoriesMixin {

    @Mutable @Accessor void setItemIcons(List<ItemStack> newList);

}
