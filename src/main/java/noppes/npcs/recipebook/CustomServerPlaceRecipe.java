package noppes.npcs.recipebook;

import com.google.common.collect.Lists;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.controllers.data.RecipeCarpentry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomServerPlaceRecipe<C extends Container> {

    protected final Inventory inventory;
    protected final RecipeBookMenu<C> menu;
    protected final CustomStackedContents<C> customStackedContents;

    public CustomServerPlaceRecipe(Recipe<C> npcRecipe, boolean isShiftPress, Inventory inventoryIn,
                                                                 RecipeBookMenu<C> menuIn, CustomStackedContents<C> customStackedContentsIn) {
        customStackedContents = customStackedContentsIn;
        inventory = inventoryIn;
        menu = menuIn;
        handleRecipeClicked(npcRecipe, isShiftPress);
    }

    public void handleRecipeClicked(Recipe<C> recipe, boolean isShiftPress) {
        RecipeCarpentry npcRecipe = (RecipeCarpentry) recipe;
        int craftSize = isShiftPress ? Integer.MAX_VALUE : 1;
        if (!isShiftPress && menu.recipeMatches(recipe)) { craftSize = getStackSize(npcRecipe) + 1; }
        if (craftSize != 0) {
            Map<Integer, ItemStack> ingredients = customStackedContents.getCraftableStack(npcRecipe, craftSize, menu);
            if (ingredients != null) {
                clearGrid();
                for (Map.Entry<Integer, ItemStack> entry : ingredients.entrySet()) {
                    Slot slot = menu.getSlot(entry.getKey() + (menu.getResultSlotIndex() == 0 ? 1 : 0));
                    ItemStack stack = entry.getValue();
                    if (!stack.isEmpty()) {
                        ItemStack oneItem = stack.copy();
                        oneItem.setCount(1);
                        for(int i = 0; i < stack.getCount(); ++i) {
                            moveItemToGrid(npcRecipe, slot, oneItem);
                        }
                    }
                }
                menu.getSlot(menu.getResultSlotIndex()).set(npcRecipe.getResult().getMCItemStack());
            }
        }
    }

    private int getStackSize(RecipeCarpentry npcRecipe) {
        List<Ingredient> ingredients = Lists.newArrayList();
        ingredients.addAll(npcRecipe.getIngredients());
        int craftSize = Integer.MAX_VALUE;
        if (npcRecipe.isShaped()) {
            int sX = 0;
            int sY = 0;
            int slotId;
            for (slotId = 0; slotId < menu.getSize(); slotId++) {
                if (slotId != menu.getResultSlotIndex() && !menu.getSlot(slotId).getItem().isEmpty()) {
                    int slot = (slotId - (menu.getResultSlotIndex() == 0 ? 1 : 0));
                    sX = slot % menu.getGridWidth();
                    sY = (int) Math.floor((double) slot / (double) menu.getGridHeight());
                    break;
                }
            }
            slotId = 0;
            for(Ingredient ingredient : ingredients) {
                if (!ingredient.isEmpty()) {
                    int slot = sX + slotId % npcRecipe.getWidth() +
                            (sY + (int) Math.floor((double) slotId / (double) npcRecipe.getWidth())) * menu.getGridWidth() +
                            (menu.getResultSlotIndex() == 0 ? 1 : 0);
                    ItemStack stack = menu.getSlot(slot).getItem();
                    for (ItemStack target : ingredient.getItems()) {
                        if (NoppesUtilPlayer.compareItems(target, stack, npcRecipe.getIgnoreDamage(), npcRecipe.getIgnoreNBT())) {
                            craftSize = (int) Math.min(craftSize, Math.floor((double) stack.getCount() / (double) target.getCount()));
                            break;
                        }
                    }
                }
                slotId++;
            }
        }
        else {
            HashMap<ItemStack, Integer> contents = new HashMap<>();
            for (int slotId = 0; slotId < menu.getSize(); slotId++) {
                if (slotId != menu.getResultSlotIndex()) {
                    ItemStack stack = menu.getSlot(slotId).getItem();
                    ItemStack key = customStackedContents.getKey(stack);
                    if (key != null) {
                        if (!contents.containsKey(key)) { contents.put(key, 0); }
                        contents.put(key, contents.get(key) + stack.getCount());
                    }
                }
            }
            ingredients.removeIf(Ingredient::isEmpty);
            for(Ingredient ingredient : ingredients) {
                if (!ingredient.isEmpty()) {
                    for (ItemStack key : contents.keySet()) {
                        for (ItemStack target : ingredient.getItems()) {
                            if (NoppesUtilPlayer.compareItems(target, key, npcRecipe.getIgnoreDamage(), npcRecipe.getIgnoreNBT())) {
                                craftSize = (int) Math.min(craftSize, Math.floor((double) contents.get(key) / (double) target.getCount()));
                                break;
                            }
                        }
                    }
                }
            }
        }
        return craftSize == Integer.MAX_VALUE ? 0 : craftSize;
    }

    protected void clearGrid() {
        for(int i = 0; i < menu.getSize(); ++i) {
            if (menu.shouldMoveToInventory(i)) {
                ItemStack itemstack = menu.getSlot(i).getItem().copy();
                inventory.placeItemBackInInventory(itemstack, false);
                menu.getSlot(i).set(itemstack);
            }
        }
        menu.clearCraftingContent();
    }

    protected void moveItemToGrid(RecipeCarpentry npcRecipe, Slot slot, ItemStack stack) {
        int slotId = findSlotMatchingUnusedItem(npcRecipe, stack);
        if (slotId != -1) {
            ItemStack itemstack = inventory.getItem(slotId);
            if (!itemstack.isEmpty()) {
                if (itemstack.getCount() > 1) { inventory.removeItem(slotId, 1); }
                else { inventory.removeItemNoUpdate(slotId); }
                if (slot.getItem().isEmpty()) { slot.set(itemstack.copyWithCount(1)); }
                else { slot.getItem().grow(1); }
            }
        }
    }

    protected int findSlotMatchingUnusedItem(RecipeCarpentry npcRecipe, ItemStack stack) {
        for(int slotId = 0; slotId < inventory.items.size(); ++slotId) {
            ItemStack itemstack = inventory.items.get(slotId);
            if (NoppesUtilPlayer.compareItems(stack, itemstack, npcRecipe.getIgnoreDamage(), npcRecipe.getIgnoreNBT())) {
                return slotId;
            }
        }
        return -1;
    }

}
