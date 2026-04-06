package noppes.npcs.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import noppes.npcs.api.handler.data.INpcRecipe;
import noppes.npcs.items.crafting.NpcShapedRecipes;
import noppes.npcs.items.crafting.NpcShapelessRecipes;

import javax.annotation.Nonnull;

public class ContainerManageRecipes extends Container {

	public InventoryBasic craftingMatrix;
	public INpcRecipe recipe;
	public int size;
	public int width;

	public ContainerManageRecipes(EntityPlayer player, int size) {
		if (size > 4) { size = 4; }
		size = size * size;
		width = size;
		craftingMatrix = new InventoryBasic("crafting", false, size + 1);
		recipe = new NpcShapedRecipes();
		addSlotToContainer(new Slot(craftingMatrix, 0, 87, 61));
		for (int i = 0; i < size; ++i) {
			for (int j = 0; j < size; ++j) {
				addSlotToContainer(new Slot(craftingMatrix, i * width + j + 1, j * 18 + 8, i * 18 + 35));
			}
		}
		for (int i2 = 0; i2 < 3; ++i2) {
			for (int l1 = 0; l1 < 9; ++l1) {
				addSlotToContainer(new Slot(player.inventory, l1 + i2 * 9 + 9, 8 + l1 * 18, 113 + i2 * 18));
			}
		}
		for (int j2 = 0; j2 < 9; ++j2) {
			addSlotToContainer(new Slot(player.inventory, j2, 8 + j2 * 18, 171));
		}
	}

	@Override
	public boolean canInteractWith(@Nonnull EntityPlayer entityplayer) { return true; }

	@Override
	public @Nonnull ItemStack transferStackInSlot(@Nonnull EntityPlayer player, int i) { return ItemStack.EMPTY; }

	public void saveRecipe(String group, String name, boolean shaped) {
		if (group.isEmpty()) { group = "default"; }
		if (name.isEmpty()) { name = "default"; }
		if (craftingMatrix.getSizeInventory() == size + 1) {
			NonNullList<Ingredient> ingredients = NonNullList.create();
			for (int i = 1, j = 0; i <= size; i++) {
				ItemStack stack = craftingMatrix.getStackInSlot(i);
				Ingredient ing = stack.isEmpty() ? Ingredient.EMPTY
						: Ingredient.fromStacks(stack.copy());
				if (shaped) {
					ingredients.add(i - 1, ing);
				} else if (ing != Ingredient.EMPTY) {
					ingredients.add(j, ing);
					j++;
				}
			}
			INpcRecipe recipeIn;
			if (shaped) {
				int f = width;
				recipeIn = new NpcShapedRecipes(group, name, f, f, ingredients,
						craftingMatrix.getStackInSlot(0).copy());
				((NpcShapedRecipes) recipeIn).global = width == 3;
			} else {
				recipeIn = new NpcShapelessRecipes(group, name, ingredients, craftingMatrix.getStackInSlot(0).copy());
				((NpcShapelessRecipes) recipeIn).global = width == 3;
			}
			recipe = recipeIn;
		}
	}

	public void setRecipe(INpcRecipe recipeIn) {
		recipe = recipeIn;
		craftingMatrix.setInventorySlotContents(0, recipe.getProduct().getMCItemStack());
		NonNullList<Ingredient> ings;
		if (recipe.isShaped()) {
			ings = ((NpcShapedRecipes) recipe).getIngredients();
		} else {
			ings = ((NpcShapelessRecipes) recipe).getIngredients();
		}
		for (int i = 0; i < ings.size() && i < (1 + size); i++) {
			if (ings.get(i).getMatchingStacks().length == 0) {
				craftingMatrix.setInventorySlotContents((i + 1), ItemStack.EMPTY);
				continue;
			}
			craftingMatrix.setInventorySlotContents((i + 1), ings.get(i).getMatchingStacks()[0]);
		}
		detectAndSendChanges();
	}


}
