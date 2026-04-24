package noppes.npcs.mixin.recipebook;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.protocol.game.ClientboundPlaceGhostRecipePacket;
import net.minecraft.recipebook.ServerPlaceRecipe;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.common.crafting.IShapedRecipe;
import noppes.npcs.controllers.data.RecipeCarpentry;
import noppes.npcs.util.CustomStackedContents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;

@Mixin(value = ServerPlaceRecipe.class, priority = 498)
public class ServerPlaceRecipeMixin<C extends Container> {

    @Final @Shadow protected StackedContents stackedContents = new CustomStackedContents();
    @Shadow protected Inventory inventory;
    @Shadow protected RecipeBookMenu<C> menu;

    @Unique private boolean npcs$ignoreDamage = false;
    @Unique private boolean npcs$ignoreNBT = false;

    @Inject(at = {@At("HEAD")}, method = {"recipeClicked"}, cancellable = true)
    public void npcs$recipeClicked(ServerPlayer player, Recipe<C> recipe, boolean isShiftPress, CallbackInfo ci) {
        if (recipe instanceof RecipeCarpentry npcRecipe && player.getRecipeBook().contains(recipe)) {
            ci.cancel();
            IServerPlaceRecipeMixin mixin = (IServerPlaceRecipeMixin) this;
            // Checking the possibility of clearing the grid or creative
            if (mixin.invokeTestClearGrid() || player.isCreative()) {
                npcs$ignoreDamage = npcRecipe.getIgnoreDamage();
                npcs$ignoreNBT = npcRecipe.getIgnoreNBT();
                inventory = player.getInventory();
                stackedContents.clear();
                player.getInventory().fillStackedContents(stackedContents);
                menu.fillCraftSlotsStackedContents(stackedContents);
                // Checking the availability of a crafting recipe for a player
                if (npcRecipe.availability.isAvailable(player) &&
                        stackedContents.canCraft(recipe, null)) {
                    mixin.invokeHandleRecipeClicked(recipe, isShiftPress);
                }
                else {
                    mixin.invokeClearGrid();
                    player.connection.send(new ClientboundPlaceGhostRecipePacket(player.containerMenu.containerId, recipe));
                }
                player.getInventory().setChanged();
            }
        }
    }

    @Inject(at = {@At("HEAD")}, method = {"handleRecipeClicked"}, cancellable = true)
    public void npcs$handleRecipeClicked(Recipe<C> recipe, boolean isShiftPress, CallbackInfo ci) {
        if (recipe instanceof RecipeCarpentry) {
            IServerPlaceRecipeMixin mixin = (IServerPlaceRecipeMixin) this;
            ci.cancel();
            boolean canPlace = menu.recipeMatches(recipe);
            int maxStackSize = stackedContents.getBiggestCraftableStack(recipe, null);
            if (canPlace) {
                for(int j = 0; j < menu.getGridHeight() * menu.getGridWidth() + 1; ++j) {
                    if (j != menu.getResultSlotIndex()) {
                        ItemStack itemstack = menu.getSlot(j).getItem();
                        if (!itemstack.isEmpty() && Math.min(maxStackSize, itemstack.getMaxStackSize()) < itemstack.getCount() + 1) {
                            return;
                        }
                    }
                }
            }
            int realMaxStackSize = mixin.invokeGetStackSize(isShiftPress, maxStackSize, canPlace);
            IntList ingredientList = new IntArrayList();
            if (stackedContents.canCraft(recipe, ingredientList, realMaxStackSize)) {
                int stackSize = realMaxStackSize;
                for(int itemId : ingredientList) {
                    int size = StackedContents.fromStackingIndex(itemId).getMaxStackSize();
                    if (size < stackSize) { stackSize = size; }
                }
                if (stackedContents.canCraft(recipe, ingredientList, stackSize)) {
                    mixin.invokeClearGrid();
                    npcs$placeRecipe(menu.getGridWidth(), menu.getGridHeight(), menu.getResultSlotIndex(), recipe, ingredientList.iterator(), stackSize);
                }
            }
        }
    }

    @Unique
    private void npcs$placeRecipe(int gridWidth, int gridHeight, int startingSlot, Recipe<C> recipe, Iterator<?> ingredientList, int stackSize) {
        IServerPlaceRecipeMixin mixin = (IServerPlaceRecipeMixin) this;
        // Determine the actual width and height of the recipe
        int recipeWidth = gridWidth;
        int recipeHeight = gridHeight;
        // Exact width and height of the recipe
        if (recipe instanceof IShapedRecipe<?> shapedRecipe) {
            recipeWidth = shapedRecipe.getRecipeWidth();
            recipeHeight = shapedRecipe.getRecipeHeight();
        }
        else if (recipe instanceof RecipeCarpentry carpentryRecipe && carpentryRecipe.isShaped()) {
            recipeWidth = carpentryRecipe.getWidth();
            recipeHeight = carpentryRecipe.getHeight();
        }
        int currentSlotIndex = 0;
        // We go along the height of the prescription grid
        for (int row = 0; row < gridHeight; ++row) {
            // Skipping the initial slot
            if (currentSlotIndex == startingSlot) { ++currentSlotIndex; }
            // Check if the current row is in the top half of the grid
            boolean isUpperHalf = (float) recipeHeight < (float) gridHeight / 2.0F;
            // Calculating the indentation to center the recipe
            int verticalOffset = Mth.floor((float) gridHeight / 2.0F - (float) recipeHeight / 2.0F);
            // If the recipe is in the top half and the indent is greater than the current line
            if (isUpperHalf && verticalOffset > row) {
                currentSlotIndex += gridWidth;
                ++row;
            }
            // We go through the width of the prescription grid
            for (int column = 0; column < gridWidth; ++column) {
                // Checking if there are any more ingredients
                if (!ingredientList.hasNext()) { return; }
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
                    mixin.invokeAddItemToSlot(ingredientList, currentSlotIndex, stackSize, row, column);
                }
                // If you have reached the maximum column
                else if (maxColumn == column) {
                    currentSlotIndex += gridWidth - column;
                    break;
                }
                ++currentSlotIndex;
            }
        }
    }

}
