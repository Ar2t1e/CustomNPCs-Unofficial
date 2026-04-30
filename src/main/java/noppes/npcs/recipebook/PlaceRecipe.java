package noppes.npcs.recipebook;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.crafting.IShapedRecipe;
import noppes.npcs.controllers.data.RecipeCarpentry;

import java.util.Iterator;

public interface PlaceRecipe<T> {

    default void placeRecipe(int gridWidth, int gridHeight, int resultSlotId, IRecipe recipe, Iterator<T> ingredientList, int amount) {
        int recipeWidth = gridWidth;
        int recipeHeight = gridHeight;
        if (recipe instanceof IShapedRecipe) {
            IShapedRecipe shapedRecipe = (IShapedRecipe) recipe;
            recipeWidth = shapedRecipe.getRecipeWidth();
            recipeHeight = shapedRecipe.getRecipeHeight();
        }
        else if (recipe instanceof RecipeCarpentry && ((RecipeCarpentry) recipe).isShaped()) {
            RecipeCarpentry npcRecipe = (RecipeCarpentry) recipe;
            recipeWidth = npcRecipe.getWidth();
            recipeHeight = npcRecipe.getHeight();
        }
        int slotId = 0;
        for(int row = 0; row < gridHeight; ++row) {
            if (slotId == resultSlotId) { ++slotId; }
            boolean isUpperHalf = (float) recipeHeight < (float) gridHeight / 2.0F;
            int verticalOffset = MathHelper.floor((float) gridHeight / 2.0F - (float) recipeHeight / 2.0F);
            if (isUpperHalf && verticalOffset > row) {
                slotId += gridWidth;
                ++row;
            }
            for(int column = 0; column < gridWidth; ++column) {
                if (!ingredientList.hasNext()) { return; }
                isUpperHalf = (float) recipeWidth < (float) gridWidth / 2.0F;
                int horizontalOffset = MathHelper.floor((float) gridWidth / 2.0F - (float) recipeWidth / 2.0F);
                int maxColumn = recipeWidth;
                boolean isWithinRecipe = column < recipeWidth;
                if (isUpperHalf) {
                    maxColumn = horizontalOffset + recipeWidth;
                    isWithinRecipe = horizontalOffset <= column && column < horizontalOffset + recipeWidth;
                }
                if (isWithinRecipe) {
                    addItemToSlot(ingredientList, slotId, amount, row, column);
                }
                else if (maxColumn == column) {
                    slotId += gridWidth - column;
                    break;
                }
                ++slotId;
            }
        }
    }

    void addItemToSlot(Iterator<T> iterator, int slotId, int amount, int row, int column);

}
