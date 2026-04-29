package noppes.npcs.mixin.client.gui.screens.recipebook;

import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.gui.screens.recipebook.GhostRecipe;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookTabButton;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.controllers.data.RecipeCarpentry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mixin(value = RecipeBookComponent.class, priority = 498)
public class RecipeBookComponentMixin {

    @Final @Shadow private List<RecipeBookTabButton> tabButtons;
    @Final @Shadow protected GhostRecipe ghostRecipe;
    @Shadow protected RecipeBookMenu<?> menu;
    @Shadow protected Minecraft minecraft;
    @Shadow private ClientRecipeBook book;
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

    @Inject(
            method = "setupGhostRecipe",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeBookComponent;placeRecipe(IIILnet/minecraft/world/item/crafting/Recipe;Ljava/util/Iterator;I)V"
            ),
            cancellable = true
    )
    public void npcs$setupGhostRecipe(Recipe<?> recipe, List<Slot> slots, CallbackInfo ci) {
        if (recipe instanceof RecipeCarpentry npcRecipe) {
            ci.cancel();
            int gridWidth = menu.getGridWidth();
            int gridHeight = menu.getGridHeight();
            int recipeWidth = gridWidth;
            int recipeHeight = gridHeight;
            List<Ingredient> ingredients = new ArrayList<>(npcRecipe.getIngredients());
            if (npcRecipe.isShaped()) {
                recipeWidth = npcRecipe.getWidth();
                recipeHeight = npcRecipe.getHeight();
            }
            else { ingredients.removeIf(Ingredient::isEmpty); }
            Iterator<Ingredient> intList = ingredients.iterator();
            // Exact width and height of the recipe
            int slotId = 0;
            // We go along the height of the prescription grid
            for (int row = 0; row < gridHeight; ++row) {
                // Skipping the initial slot
                if (slotId == menu.getResultSlotIndex()) { ++slotId; }
                // Check if the current row is in the top half of the grid
                boolean isUpperHalf = (float) recipeHeight < (float) gridHeight / 2.0F;
                // Calculating the indentation to center the recipe
                int verticalOffset = Mth.floor((float) gridHeight / 2.0F - (float) recipeHeight / 2.0F);
                // If the recipe is in the top half and the indent is greater than the current line
                if (isUpperHalf && verticalOffset > row) {
                    slotId += gridWidth;
                    ++row;
                }
                // We go through the width of the prescription grid
                for (int column = 0; column < gridWidth; ++column) {
                    // Checking if there are any more ingredients
                    if (!intList.hasNext()) { return; }
                    // Check if the current column is in the left half of the grid
                    isUpperHalf = (float) recipeWidth < (float)gridWidth / 2.0F;
                    // Calculating the horizontal indentation to center the recipe
                    int horizontalOffset = Mth.floor((float)gridWidth / 2.0F - (float) recipeWidth / 2.0F);
                    // Maximum recipe column
                    int maxColumn = recipeWidth;
                    // Check if the current column is within the recipe
                    boolean isWithinRecipe = column < recipeWidth;
                    // If the recipe is centered
                    if (isUpperHalf) {
                        maxColumn = horizontalOffset + recipeWidth;
                        isWithinRecipe = horizontalOffset <= column && column < horizontalOffset + recipeWidth;
                    }
                    // If the current cell is within the recipe
                    if (isWithinRecipe) {
                        Ingredient ingredient = intList.next();
                        if (!ingredient.isEmpty()) {
                            Slot slot = menu.slots.get(slotId);
                            ghostRecipe.addIngredient(ingredient, slot.x, slot.y);
                        }
                    }
                    // If you have reached the maximum column
                    else if (maxColumn == column) {
                        slotId += gridWidth - column;
                        break;
                    }
                    ++slotId;
                }
            }
        }
    }

}
