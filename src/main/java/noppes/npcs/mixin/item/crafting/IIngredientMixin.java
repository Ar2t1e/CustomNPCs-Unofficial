package noppes.npcs.mixin.item.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Ingredient.class, priority = 502)
public interface IIngredientMixin {

    @Accessor
    ItemStack[] getMatchingStacks();

}
