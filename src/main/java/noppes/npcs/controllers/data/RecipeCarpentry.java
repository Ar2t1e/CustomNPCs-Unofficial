package noppes.npcs.controllers.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeHooks;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NBTTags;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.api.handler.data.IRecipe;
import noppes.npcs.controllers.RecipeController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RecipeCarpentry extends ShapedRecipe implements IRecipe {

   public Availability availability = new Availability();
   public boolean isGlobal = false;
   public boolean ignoreDamage = false;
   public boolean ignoreNBT = false;
   public boolean savesRecipe = true;
   public String name;

   public RecipeCarpentry(ResourceLocation location, int width, int height, NonNullList<Ingredient> recipe, ItemStack result) {
      super(location, CustomNpcs.MODID, CraftingBookCategory.MISC, width, height, recipe, result);
      name = location.getPath();
   }

   public RecipeCarpentry(ResourceLocation location, String nameIn) {
      super(location, CustomNpcs.MODID, CraftingBookCategory.MISC, 4, 4, NonNullList.create(), ItemStack.EMPTY);
      name = nameIn;
   }

   public static RecipeCarpentry load(CompoundTag compound) {
      ResourceLocation location;
      if (compound.contains("ID")) {
         location = new ResourceLocation(CustomNpcs.MODID, compound.getString("ID"));
      } else {
         location = ResourceLocation.tryParse(compound.getString("Id"));
      }

      RecipeCarpentry recipe = new RecipeCarpentry(location, compound.getInt("Width"), compound.getInt("Height"), NBTTags.getIngredientList(compound.getList("Materials", 10)), ItemStack.of(compound.getCompound("Item")));
      recipe.availability.load(compound.getCompound("Availability"));
      recipe.ignoreDamage = compound.getBoolean("IgnoreDamage");
      recipe.ignoreNBT = compound.getBoolean("IgnoreNBT");
      recipe.isGlobal = compound.getBoolean("Global");
      recipe.name = compound.getString("Name");
      return recipe;
   }

   public CompoundTag writeNBT() {
      CompoundTag compound = new CompoundTag();
      compound.putInt("Width", getRecipeWidth());
      compound.putInt("Height", getRecipeHeight());
      if (getResult() != null) {
         compound.put("Item", getResult().save(new CompoundTag()));
      }

      compound.put("Materials", NBTTags.nbtIngredientList(getIngredients()));
      compound.put("Availability", availability.save(new CompoundTag()));
      compound.putString("Name", name);
      compound.putString("Id", getId().toString());
      compound.putBoolean("Global", isGlobal);
      compound.putBoolean("IgnoreDamage", ignoreDamage);
      compound.putBoolean("IgnoreNBT", ignoreNBT);
      return compound;
   }

   public static RecipeCarpentry createRecipe(ResourceLocation location, RecipeCarpentry recipe, ItemStack par1ItemStack, Object... limbSwingAmountArrayOfObj) {
      StringBuilder var3 = new StringBuilder();
      int var4 = 0;
      int var5 = 0;
      int var6 = 0;
      int var9;
      if (limbSwingAmountArrayOfObj[var4] instanceof String[]) {
         for (String var11 : (String[]) limbSwingAmountArrayOfObj[var4++]) {
            ++var6;
            var5 = var11.length();
            var3.append(var11);
         }
      } else {
         while(limbSwingAmountArrayOfObj[var4] instanceof String) {
            String var13 = (String)limbSwingAmountArrayOfObj[var4++];
            ++var6;
            var5 = var13.length();
            var3.append(var13);
         }
      }

      HashMap<Character, ItemStack> var14;
      for(var14 = new HashMap<>(); var4 < limbSwingAmountArrayOfObj.length; var4 += 2) {
         Character var16 = (Character)limbSwingAmountArrayOfObj[var4];
         ItemStack var17 = ItemStack.EMPTY;
         if (limbSwingAmountArrayOfObj[var4 + 1] instanceof Item) {
            var17 = new ItemStack((Item)limbSwingAmountArrayOfObj[var4 + 1]);
         } else if (limbSwingAmountArrayOfObj[var4 + 1] instanceof Block) {
            var17 = new ItemStack((Block)limbSwingAmountArrayOfObj[var4 + 1], 1);
         } else if (limbSwingAmountArrayOfObj[var4 + 1] instanceof ItemStack) {
            var17 = (ItemStack)limbSwingAmountArrayOfObj[var4 + 1];
         }
         var14.put(var16, var17);
      }
      NonNullList<Ingredient> ingredients = NonNullList.create();
      for(var9 = 0; var9 < var5 * var6; ++var9) {
         char var18 = var3.charAt(var9);
         if (var14.containsKey(var18)) {
            ingredients.add(var9, Ingredient.of(var14.get(var18).copy()));
         } else {
            ingredients.add(var9, Ingredient.EMPTY);
         }
      }
      RecipeCarpentry newrecipe = new RecipeCarpentry(location, var5, var6, ingredients, par1ItemStack);
      newrecipe.copy(recipe);
      if (var5 == 4 || var6 == 4) {
         newrecipe.isGlobal = false;
      }
      return newrecipe;
   }

   @Override
   public boolean matches(@NotNull CraftingContainer inventoryCrafting, @Nullable Level world) {
      for(int i = 0; i <= 4 - getRecipeWidth(); ++i) {
         for(int j = 0; j <= 4 - getRecipeHeight(); ++j) {
            if (checkMatch(inventoryCrafting, i, j, true)) {
               return true;
            }
            if (checkMatch(inventoryCrafting, i, j, false)) {
               return true;
            }
         }
      }
      return false;
   }

   @Override
   public @NotNull ItemStack getResultItem(@NotNull RegistryAccess registryAccess) {
      return super.getResultItem(registryAccess).isEmpty() ? ItemStack.EMPTY : super.getResultItem(registryAccess).copy();
   }

   private boolean checkMatch(Container inventoryCrafting, int par2, int par3, boolean par4) {
      for(int i = 0; i < 4; ++i) {
         for(int j = 0; j < 4; ++j) {
            int var7 = i - par2;
            int var8 = j - par3;
            Ingredient ingredient = Ingredient.EMPTY;
            if (var7 >= 0 && var8 >= 0 && var7 < getRecipeWidth() && var8 < getRecipeHeight()) {
               if (par4) {
                  ingredient = getIngredients().get(getRecipeWidth() - var7 - 1 + var8 * getRecipeWidth());
               } else {
                  ingredient = getIngredients().get(var7 + var8 * getRecipeWidth());
               }
            }

            ItemStack var10 = ItemStack.EMPTY;
            if (inventoryCrafting instanceof TransientCraftingContainer tcc) {
                var10 = tcc.getItem(i + j * tcc.getWidth());
            }
            if (!var10.isEmpty() && ingredient.getItems().length == 0) {
               return false;
            }
            if (!var10.isEmpty() || ingredient.getItems().length != 0) {
               ItemStack var9 = ingredient.getItems()[0];
               if ((!var10.isEmpty() || !var9.isEmpty()) && !NoppesUtilPlayer.compareItems(var9, var10, ignoreDamage, ignoreNBT)) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   @Override
   public @NotNull NonNullList<ItemStack> getRemainingItems(CraftingContainer inventoryCrafting) {
      NonNullList<ItemStack> list = NonNullList.withSize(inventoryCrafting.getContainerSize(), ItemStack.EMPTY);
      for(int i = 0; i < list.size(); ++i) {
         ItemStack itemstack = inventoryCrafting.getItem(i);
         list.set(i, ForgeHooks.getCraftingRemainingItem(itemstack));
      }
      return list;
   }

   public void copy(RecipeCarpentry recipe) {
      availability = recipe.availability;
      isGlobal = recipe.isGlobal;
      ignoreDamage = recipe.ignoreDamage;
      ignoreNBT = recipe.ignoreNBT;
   }

   public ItemStack getCraftingItem(int i) {
      if (i >= getIngredients().size()) {
         return ItemStack.EMPTY;
      } else {
         Ingredient ingredients = getIngredients().get(i);
         return ingredients.getItems().length == 0 ? ItemStack.EMPTY : ingredients.getItems()[0];
      }
   }

   public boolean isValid() {
      if (name.isEmpty()) { return false; }
      if (!getIngredients().isEmpty() && !getResult().isEmpty()) {
         Iterator<Ingredient> var1 = getIngredients().iterator();
         Ingredient ingredient;
         do {
            if (!var1.hasNext()) {
               return false;
            }

            ingredient = var1.next();
         } while(ingredient.getItems().length == 0);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public String getName() { return name; }

   @Override
   public ItemStack getResult() { return getResultItem(RegistryAccess.EMPTY); }

   @Override
   public boolean isGlobal() { return isGlobal; }

   @Override
   public void setIsGlobal(boolean bo) { isGlobal = bo; }

   @Override
   public boolean getIgnoreNBT() { return ignoreNBT; }

   @Override
   public void setIgnoreNBT(boolean bo) { ignoreNBT = bo; }

   @Override
   public boolean getIgnoreDamage() { return ignoreDamage; }

   @Override
   public void setIgnoreDamage(boolean bo) { ignoreDamage = bo; }

   @Override
   public void save() { RecipeController.getInstance().saveRecipe(this); }

   @Override
   public void delete() { }

   @Override
   public ItemStack[] getRecipe() {
      List<ItemStack> list = new ArrayList<>();
      for (Ingredient ingredient : getIngredients()) {
         if (ingredient.getItems().length > 0) {
            list.add(ingredient.getItems()[0]);
         }
      }
      return list.toArray(new ItemStack[0]);
   }

   @Override
   public void saves(boolean bo) { savesRecipe = bo; }

   @Override
   public boolean saves() { return savesRecipe; }

}
