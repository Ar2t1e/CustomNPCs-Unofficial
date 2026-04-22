package noppes.npcs.controllers.data;

import java.util.ArrayList;
import java.util.HashMap;
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
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.handler.data.INpcRecipe;
import noppes.npcs.controllers.RecipeController;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RecipeCarpentry extends ShapedRecipe implements INpcRecipe {

   public Availability availability = new Availability();
   public boolean isGlobal = false;
   public boolean ignoreDamage = false;
   public boolean ignoreNBT = false;
   public boolean savesRecipe = true;
   public String name;

   public RecipeCarpentry(ResourceLocation location, int width, int height, NonNullList<Ingredient> recipe, ItemStack result) {
      super(location, location.getNamespace(), CraftingBookCategory.MISC, width, height, recipe, result);
      name = location.getPath();
   }

   public RecipeCarpentry(ResourceLocation location) {
      this(location, 4, 4, NonNullList.create(), ItemStack.EMPTY);
   }

   public static RecipeCarpentry load(CompoundTag compound) {
      ResourceLocation location;
      if (compound.contains("ID", 8)) { location = new ResourceLocation(CustomNpcs.MODID, NoppesUtilServer.validPath(compound.getString("ID"))); }
      else { location = new ResourceLocation(NoppesUtilServer.validLocation(compound.getString("Id"))); }

      RecipeCarpentry recipe = new RecipeCarpentry(location, compound.getInt("Width"), compound.getInt("Height"),
              NBTTags.getIngredientList(compound.getList("Materials", 10)),
              compound.contains("Item", 10) ? ItemStack.of(compound.getCompound("Item")) : ItemStack.EMPTY);
      recipe.availability.load(compound.getCompound("Availability"));
      recipe.ignoreDamage = compound.getBoolean("IgnoreDamage");
      recipe.ignoreNBT = compound.getBoolean("IgnoreNBT");
      recipe.isGlobal = compound.getBoolean("Global");
      recipe.name = compound.getString("Name");
      return recipe;
   }

   public CompoundTag saveTo() {
      CompoundTag compound = new CompoundTag();
      compound.putInt("Width", getRecipeWidth());
      compound.putInt("Height", getRecipeHeight());
      if (getResult() != null) { compound.put("Item", getResult().save(new CompoundTag())); }
      compound.put("Materials", NBTTags.nbtIngredientList(getIngredients()));
      compound.put("Availability", availability.save(new CompoundTag()));
      compound.putString("Name", name);
      compound.putString("Id", getId().toString());
      compound.putBoolean("Global", isGlobal);
      compound.putBoolean("IgnoreDamage", ignoreDamage);
      compound.putBoolean("IgnoreNBT", ignoreNBT);
      return compound;
   }

   public static RecipeCarpentry createRecipe(ResourceLocation location, RecipeCarpentry recipe, ItemStack result, Object... objects) {
      StringBuilder lineData = new StringBuilder();
      int size = 0;
      int widht = 0;
      int height = 0;
      if (objects[size] instanceof String[]) {
         for (String line : (String[]) objects[size++]) {
            ++height;
            widht = line.length();
            lineData.append(line);
         }
      }
      else {
         while(objects[size] instanceof String) {
            String line = (String) objects[size++];
            ++height;
            widht = line.length();
            lineData.append(line);
         }
      }

      HashMap<Character, ItemStack> mapData;
      for(mapData = new HashMap<>(); size < objects.length; size += 2) {
         Character character = (Character) objects[size];
         ItemStack stack = ItemStack.EMPTY;
         if (objects[size + 1] instanceof Item item) { stack = new ItemStack(item); }
         else if (objects[size + 1] instanceof Block block) { stack = new ItemStack(block, 1); }
         else if (objects[size + 1] instanceof ItemStack stackIn) { stack = stackIn; }
         mapData.put(character, stack);
      }
      NonNullList<Ingredient> ingredients = NonNullList.create();
      for(int slotId = 0; slotId < widht * height; ++slotId) {
         char var18 = lineData.charAt(slotId);
         if (mapData.containsKey(var18)) {
            ingredients.add(slotId, Ingredient.of(mapData.get(var18).copy()));
         } else {
            ingredients.add(slotId, Ingredient.EMPTY);
         }
      }
      RecipeCarpentry newRecipe = new RecipeCarpentry(location, widht, height, ingredients, result);
      newRecipe.copy(recipe);
      newRecipe.isGlobal = widht < 4 && height < 4;
      return newRecipe;
   }

   @Override
   public boolean matches(@Nonnull CraftingContainer inventoryCrafting, @Nullable Level world) {
      for(int i = 0; i <= 4 - getRecipeWidth(); ++i) {
         for(int j = 0; j <= 4 - getRecipeHeight(); ++j) {
            if (checkMatch(inventoryCrafting, i, j, true)) { return true; }
            if (checkMatch(inventoryCrafting, i, j, false)) { return true; }
         }
      }
      return false;
   }

   @Override
   public @Nonnull ItemStack getResultItem(@Nonnull RegistryAccess registryAccess) {
      ItemStack stack = super.getResultItem(registryAccess);
      return stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
   }

   @Override
   public @Nonnull NonNullList<ItemStack> getRemainingItems(CraftingContainer inventoryCrafting) {
      NonNullList<ItemStack> list = NonNullList.withSize(inventoryCrafting.getContainerSize(), ItemStack.EMPTY);
      for(int i = 0; i < list.size(); ++i) {
         ItemStack itemstack = inventoryCrafting.getItem(i);
         list.set(i, ForgeHooks.getCraftingRemainingItem(itemstack));
      }
      return list;
   }

   private boolean checkMatch(Container inventoryCrafting, int x, int y, boolean isRevers) {
      for(int i = 0; i < 4; ++i) {
         for(int j = 0; j < 4; ++j) {
            int u = i - x;
            int v = j - y;
            Ingredient ingredient = Ingredient.EMPTY;
            if (u >= 0 && v >= 0 && u < getRecipeWidth() && v < getRecipeHeight()) {
               if (isRevers) { ingredient = getIngredients().get(getRecipeWidth() - u - 1 + v * getRecipeWidth()); }
               else { ingredient = getIngredients().get(u + v * getRecipeWidth()); }
            }
            ItemStack stack = ItemStack.EMPTY;
            if (inventoryCrafting instanceof TransientCraftingContainer tcc) { stack = tcc.getItem(i + j * tcc.getWidth()); }
            if (!stack.isEmpty() && ingredient.getItems().length == 0) { return false; }
            if (!stack.isEmpty() || ingredient.getItems().length != 0) {
               ItemStack var9 = ingredient.getItems()[0];
               if ((!stack.isEmpty() || !var9.isEmpty()) && !NoppesUtilPlayer.compareItems(var9, stack, ignoreDamage, ignoreNBT)) { return false; }
            }
         }
      }
      return true;
   }

   public void copy(RecipeCarpentry recipe) {
      availability = recipe.availability;
      isGlobal = recipe.isGlobal;
      ignoreDamage = recipe.ignoreDamage;
      ignoreNBT = recipe.ignoreNBT;
   }

   public ItemStack getCraftingItem(int slotId) {
      if (slotId >= getIngredients().size()) { return ItemStack.EMPTY; }
      Ingredient ingredients = getIngredients().get(slotId);
      return ingredients.getItems().length == 0 ? ItemStack.EMPTY : ingredients.getItems()[0];
   }

   public boolean isValid() {
      if (name.isEmpty() || getIngredients().isEmpty() || getResult().isEmpty()) { return false; }
      for (Ingredient ingredient : getIngredients()) {
         if (ingredient.getItems().length > 0) { return true; }
      }
      return false;
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
         if (ingredient.getItems().length > 0) { list.add(ingredient.getItems()[0]); }
      }
      return list.toArray(new ItemStack[0]);
   }

   @Override
   public void saves(boolean bo) { savesRecipe = bo; }

   @Override
   public boolean saves() { return savesRecipe; }

}
