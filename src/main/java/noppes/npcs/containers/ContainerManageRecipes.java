package noppes.npcs.containers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomContainer;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.data.RecipeCarpentry;

import javax.annotation.Nonnull;

public class ContainerManageRecipes extends AbstractContainerMenu {

   public final SimpleContainer craftingMatrix;
   public RecipeCarpentry recipe;
   public int size;
   public int width;
   public boolean init = false;

   public ContainerManageRecipes(int containerId, Inventory playerInventory, int sizeIn) {
      super(CustomContainer.container_managerecipes, containerId);
      size = sizeIn * sizeIn;
      width = sizeIn;
      craftingMatrix = new SimpleContainer(size + 1);
      recipe = new RecipeCarpentry(new ResourceLocation(CustomNpcs.MODID, ""), "");
      addSlot(new Slot(craftingMatrix, 0, 87, 61));

      int j1;
      int l1;
      for(j1 = 0; j1 < sizeIn; ++j1) {
         for(l1 = 0; l1 < sizeIn; ++l1) {
            addSlot(new Slot(craftingMatrix, j1 * width + l1 + 1, l1 * 18 + 8, j1 * 18 + 35) {
               public int getMaxStackSize() { return 1; }
            });
         }
      }

      for(j1 = 0; j1 < 3; ++j1) {
         for(l1 = 0; l1 < 9; ++l1) {
            addSlot(new Slot(playerInventory, l1 + j1 * 9 + 9, 8 + l1 * 18, 113 + j1 * 18));
         }
      }

      for(j1 = 0; j1 < 9; ++j1) {
         addSlot(new Slot(playerInventory, j1, 8 + j1 * 18, 171));
      }

   }

   @Override
   public @Nonnull ItemStack quickMoveStack(@Nonnull Player playerIn, int i) { return ItemStack.EMPTY; }

   @Override
   public boolean stillValid(@Nonnull Player playerIn) { return true; }

   public void setRecipe(RecipeCarpentry recipeIn, RegistryAccess access) {
      craftingMatrix.setItem(0, recipeIn.getResultItem(access));

      for(int i = 0; i < width; ++i) {
         for(int j = 0; j < width; ++j) {
            if (j >= recipeIn.getRecipeWidth()) { craftingMatrix.setItem(i * width + j + 1, ItemStack.EMPTY); }
            else { craftingMatrix.setItem(i * width + j + 1, recipeIn.getCraftingItem(i * recipeIn.getRecipeWidth() + j)); }
         }
      }
      recipe = recipeIn;
   }

   public void saveRecipe() {
      int nextChar = 0;
      char[] chars = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P'};
      Map<ItemStack, Character> nameMapping = new HashMap<>();
      int firstRow = width;
      int lastRow = 0;
      int firstColumn = width;
      int lastColumn = 0;
      boolean seenRow = false;

      Iterator<ItemStack> var14;
      ItemStack mapped;
      for(int i = 0; i < width; ++i) {
         boolean seenColumn = false;
         for(int j = 0; j < width; ++j) {
            ItemStack item = craftingMatrix.getItem(i * width + j + 1);
            if (!NoppesUtilServer.isItemStackNull(item)) {
               if (!seenColumn && j < firstColumn) { firstColumn = j; }
               if (j > lastColumn) { lastColumn = j; }
               seenColumn = true;
               Character letter = null;
               var14 = nameMapping.keySet().iterator();
               while(var14.hasNext()) {
                  mapped = var14.next();
                  if (NoppesUtilPlayer.compareItems(mapped, item, recipe.ignoreDamage, recipe.ignoreNBT)) {
                     letter = nameMapping.get(mapped);
                  }
               }
               if (letter == null) {
                  letter = chars[nextChar];
                  ++nextChar;
                  nameMapping.put(item, letter);
               }
            }
         }
         if (seenColumn) {
            if (!seenRow) {
               firstRow = i;
               lastRow = i;
               seenRow = true;
            }
            else { lastRow = i; }
         }
      }

      ArrayList<String> recipeIn = new ArrayList<>();
      for(int i = 0; i < width; ++i) {
         if (i >= firstRow && i <= lastRow) {
            StringBuilder row = new StringBuilder();
            for(int j = 0; j < width; ++j) {
               if (j >= firstColumn && j <= lastColumn) {
                  ItemStack item = craftingMatrix.getItem(i * width + j + 1);
                  if (NoppesUtilServer.isItemStackNull(item)) { row.append(" "); }
                  else {
                     var14 = nameMapping.keySet().iterator();
                     while(var14.hasNext()) {
                        mapped = var14.next();
                        if (NoppesUtilPlayer.compareItems(mapped, item, false, false)) {
                           row.append(nameMapping.get(mapped));
                        }
                     }
                  }
               }
            }
            recipeIn.add(row.toString());
         }
      }

      if (nameMapping.isEmpty()) {
         RecipeCarpentry r = new RecipeCarpentry(new ResourceLocation(CustomNpcs.MODID, recipe.name), recipe.name);
         r.copy(recipe);
         recipe = r;
      }
      else {
         for (ItemStack itemStack : nameMapping.keySet()) {
            mapped = itemStack;
            Character letter = nameMapping.get(mapped);
            recipeIn.add(String.valueOf(letter));
            recipeIn.add(String.valueOf(mapped));
         }
         String name = recipe.name;
         recipe = RecipeCarpentry.createRecipe(recipe.getId(), recipe, craftingMatrix.getItem(0), recipeIn.toArray());
         recipe.name = name;
      }
   }
}
