package noppes.npcs.mixin.client.gui.recipebook;

import net.minecraft.client.gui.recipebook.GuiButtonRecipe;
import net.minecraft.item.crafting.IRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(value = GuiButtonRecipe.class, priority = 502)
public interface IGuiButtonRecipeMixinMixin {

    @Invoker IRecipe invokeGetRecipe();

    @Invoker List<IRecipe> invokeGetOrderedRecipes();

}
