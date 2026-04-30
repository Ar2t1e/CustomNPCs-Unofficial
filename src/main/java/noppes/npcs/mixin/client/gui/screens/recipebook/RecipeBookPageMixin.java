package noppes.npcs.mixin.client.gui.screens.recipebook;

import net.minecraft.client.gui.screens.recipebook.OverlayRecipeComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import noppes.npcs.client.gui.recipebook.CustomOverlayRecipeComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = RecipeBookPage.class, priority = 498)
public class RecipeBookPageMixin {

    @Final
    @Shadow
    @Mutable
    @SuppressWarnings("unused")
    private OverlayRecipeComponent overlay = new CustomOverlayRecipeComponent();

}
