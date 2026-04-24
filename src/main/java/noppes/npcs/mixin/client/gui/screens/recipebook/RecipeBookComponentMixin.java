package noppes.npcs.mixin.client.gui.screens.recipebook;

import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookTabButton;
import noppes.npcs.controllers.RecipeController;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = RecipeBookComponent.class, priority = 498)
public class RecipeBookComponentMixin {

    @Shadow private ClientRecipeBook book;
    @Final @Shadow private List<RecipeBookTabButton> tabButtons;
    @Shadow protected Minecraft minecraft;
    @Shadow private int xOffset;
    @Shadow private int width;
    @Shadow private int height;

    /** Custom tabs are always visible */
    @Inject(at = {@At("TAIL")}, method = {"updateTabs"})
    private void npcs$updateTabs(CallbackInfo ci) {
        int x = (width - 147) / 2 - xOffset - 30;
        int y = (height - 166) / 2 + 3;
        int tabHeight = 27;
        int tabId = 0;
        for(RecipeBookTabButton tabButton : tabButtons) {
            RecipeBookCategories category = tabButton.getCategory();
            if (category == RecipeBookCategories.CRAFTING_SEARCH ||
                    category == RecipeBookCategories.FURNACE_SEARCH ||
                    category == RecipeController.CRAFTING_CUSTOM_GLOBAL_CATEGORY ||
                    category == RecipeController.CRAFTING_CUSTOM_ANVIL_CATEGORY) {
                tabButton.visible = true;
                tabButton.setPosition(x, y + tabHeight * tabId++);
            }
            else if (tabButton.updateVisibility(book)) {
                tabButton.setPosition(x, y + tabHeight * tabId++);
                tabButton.startAnimation(minecraft);
            }
        }
    }

}
