package noppes.npcs.mixin.client.gui.recipebook;

import net.minecraft.client.gui.recipebook.GuiRecipeOverlay;
import net.minecraft.client.gui.recipebook.RecipeBookPage;
import noppes.npcs.client.gui.recipebook.CustomGuiRecipeOverlay;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = RecipeBookPage.class, priority = 498)
public class RecipeBookPageMixin {

    @SuppressWarnings("unused")
    @Final @Shadow @Mutable private GuiRecipeOverlay overlay = new CustomGuiRecipeOverlay();

}
