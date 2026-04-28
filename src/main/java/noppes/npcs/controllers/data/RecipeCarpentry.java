package noppes.npcs.controllers.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeHooks;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NBTTags;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.INbt;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.handler.data.IAvailability;
import noppes.npcs.api.handler.data.INpcRecipe;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.NBTWrapper;
import noppes.npcs.api.wrapper.gui.WrapperRecipe;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.util.CustomRecipeMatcher;
import noppes.npcs.util.Util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RecipeCarpentry implements CraftingRecipe, INpcRecipe {

   public Availability availability = new Availability();
   public boolean isGlobal;
   public boolean ignoreDamage = false;
   public boolean ignoreNBT = false;
   public boolean savesRecipe = true;
   public String name;

   // New from Unofficial (BetaZavr)
   protected final WrapperRecipe wrapper;
   protected @Nonnull NonNullList<Ingredient> ingredients;
   protected @Nonnull ItemStack result;
   protected @Nonnull ResourceLocation id;
   protected @Nonnull String group;
   protected int width;
   protected int height;
   protected boolean showNotification;
   protected boolean isShaped;
   protected boolean isKnown = false;
   protected boolean showInRecipeBook = true;

   public RecipeCarpentry(@Nonnull ResourceLocation idIn, @Nonnull String groupIn, int widthIn, int heightIn, boolean isGlobalIn, boolean isShapedIn, @Nonnull NonNullList<Ingredient> recipeItemsIn, @Nonnull ItemStack resultIn) {
      id = idIn;
      group = groupIn;
      width = widthIn;
      height = heightIn;
      ingredients = recipeItemsIn;
      result = resultIn;

      name = idIn.getPath();
      result = resultIn;
      isShaped = isShapedIn;
      isGlobal = isGlobalIn;

      wrapper = new WrapperRecipe(result);
   }

   public static RecipeCarpentry create(CompoundTag compound) {
      RecipeCarpentry recipe = new RecipeCarpentry(new ResourceLocation(CustomNpcs.MODID, ""), "", 0, 0, true, true, NonNullList.create(), ItemStack.EMPTY);
      recipe.loadFrom(compound);
      return recipe;
   }

   public static RecipeCarpentry createRecipe(@Nonnull ResourceLocation location, String group, boolean isGlobal, boolean isShaped, ItemStack result, Object... objects) {
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
         char c = lineData.charAt(slotId);
         if (mapData.containsKey(c)) {
            ingredients.add(slotId, Ingredient.of(mapData.get(c).copy()));
         } else {
            ingredients.add(slotId, Ingredient.EMPTY);
         }
      }
      return new RecipeCarpentry(location, group, widht, height, isGlobal, isShaped, ingredients, result);
   }

   @Override
   public boolean matches(@Nonnull CraftingContainer inventoryCrafting, @Nullable Level world) {
      if (isShaped) {
         for(int x = 0; x <= inventoryCrafting.getWidth() - width; ++x) {
            for(int y = 0; y <= inventoryCrafting.getHeight() - height; ++y) {
               if (checkMatch(inventoryCrafting, x, y, true)) { return true; }
               if (checkMatch(inventoryCrafting, x, y, false)) { return true; }
            }
         }
         return false;
      }
      List<ItemStack> inputs = new ArrayList<>();
      for(int slotId = 0; slotId < inventoryCrafting.getContainerSize(); ++slotId) {
         ItemStack stackInGrid = inventoryCrafting.getItem(slotId);
         if (!stackInGrid.isEmpty()) {
            inputs.add(stackInGrid);
         }
      }
      return CustomRecipeMatcher.findMatches(inputs, ingredients.stream().filter(ing -> !ing.isEmpty()).toList(),
              ignoreDamage, ignoreNBT) != null;
   }

   @Override
   public @Nonnull ItemStack assemble(@Nonnull CraftingContainer container, @Nonnull RegistryAccess registryAccess) { return result.copy(); }

   @Override
   public boolean canCraftInDimensions(int widthIn, int heightIn) {
      if (isShaped) { return widthIn >= width && heightIn >= height; }
      List<Ingredient> list = new ArrayList<>(ingredients);
      list.removeIf(Ingredient::isEmpty);
      return widthIn * heightIn >= list.size();
   }

   @Override
   public @Nonnull ItemStack getResultItem(@Nonnull RegistryAccess registryAccess) {
      return result.isEmpty() ? ItemStack.EMPTY : result.copy();
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

   @Override
   public String getName() { return name; }

   @Override
   public IItemStack getResult() { return Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(getResultItem(RegistryAccess.EMPTY)); }

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
   public int getWidth() { return width; }

   @Override
   public int getHeight() { return height; }

   @Override
   public void save() {
      if (Util.instance.getSide() == Dist.DEDICATED_SERVER) {
         RecipeController.getInstance().addAndSaveRecipe(this);
      }
   }

   @Override
   public void delete() {
      if (Util.instance.getSide() == Dist.DEDICATED_SERVER) {
         RecipeController.getInstance().delete(id);
      }
   }

   @Override
   public boolean isKnown() { return isKnown; }

   @Override
   public void setIsKnown(boolean isKnownIn) { isKnown = isKnownIn; }

   @Override
   public boolean showInRecipeBook() { return showInRecipeBook; }

   @Override
   public void setShowInRecipeBook(boolean showInRecipeBookIn) { showInRecipeBook = showInRecipeBookIn; }

   @Override
   public IAvailability getAvailability() { return availability; }

   @Override
   public IItemStack[][] getRecipe() {
      IItemStack[][] array = new IItemStack[ingredients.size()][];
      for (int i = 0; i < ingredients.size(); i++) {
         if (ingredients.get(i).getItems().length > 0) {
            ItemStack[] items = ingredients.get(i).getItems();
            array[i] = new IItemStack[items.length];
            for (int j = 0; j < items.length; j++) {
               array[i][j] = Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(items[j]);
            }
         }
         else { array[i] = new IItemStack[0]; }
      }
      return array;
   }

   @Override
   public void saves(boolean bo) { savesRecipe = bo; }

   @Override
   public boolean saves() { return savesRecipe; }

   @Override
   public boolean isSpecial() { return !showInRecipeBook; }

   @Override
   public @Nonnull ResourceLocation getId() { return id; }

   @Override
   public @Nonnull ResourceLocation getMCId() { return id; }

   @Override
   public INbt getNbt() { return new NBTWrapper(saveTo()); }

   @Override
   public String getNpcGroup() { return group; }

   @Override
   public void setNbt(INbt nbt) {
      if (nbt != null) {
         loadFrom(nbt.getMCNBT());
         RecipeController.getInstance().updateToAll();
      }
   }

   @Override
   public boolean isRecipeItemsEmpty() {
      if (!ingredients.isEmpty()) {
         for (Ingredient ingredient : ingredients) {
            for (ItemStack stack : ingredient.getItems()) {
               if (!stack.isEmpty()) { return false; }
            }
         }
      }
      return true;
   }

   @Override
   public @Nonnull RecipeSerializer<?> getSerializer() { return isShaped ? RecipeSerializer.SHAPED_RECIPE : RecipeSerializer.SHAPELESS_RECIPE; }

   @Override
   public @Nonnull NonNullList<Ingredient> getIngredients() { return ingredients; }

   @Override
   public @Nonnull RecipeType<?> getType() { return RecipeType.CRAFTING; }

   @Override
   public @Nonnull CraftingBookCategory category() { return CraftingBookCategory.MISC; }

   @Override
   public @Nonnull String getGroup() { return group; }

   @Override
   public boolean isShaped() { return isShaped; }

   @Override
   public void setIsShaped(boolean isShapedIn) { isShaped = isShapedIn; }

   public void setResult(ItemStack newResult) { result = newResult == null || newResult.isEmpty() ? ItemStack.EMPTY : newResult; }

   public CompoundTag saveTo() {
      CompoundTag compound = new CompoundTag();
      compound.putString("Id", id.toString());
      compound.putString("Group", group);
      compound.putInt("Width", width);
      compound.putInt("Height", height);
      compound.put("Materials", NBTTags.nbtIngredientList(ingredients));
      compound.put("Item", result.save(new CompoundTag()));
      compound.put("Availability", availability.save(new CompoundTag()));
      compound.putBoolean("IgnoreDamage", ignoreDamage);
      compound.putBoolean("IgnoreNBT", ignoreNBT);
      compound.putBoolean("Global", isGlobal);
      compound.putBoolean("IsKnown", isKnown);
      compound.putBoolean("IsShaped", isShaped);
      compound.putBoolean("ShowInRecipeBook", showInRecipeBook);
      compound.putString("Name", name);
      return compound;
   }

   public void loadFrom(CompoundTag compound) {
      if (compound.contains("ID", 8)) { id = new ResourceLocation(CustomNpcs.MODID, NoppesUtilServer.validPath(compound.getString("ID"))); }
      else { id = new ResourceLocation(NoppesUtilServer.validLocation(compound.getString("Id"))); }
      group = compound.getString("Group");
      width = compound.getInt("Width");
      height = compound.getInt("Height");
      ingredients = NBTTags.getIngredientList(compound.getList("Materials", 10));
      result = compound.contains("Item", 10) ? ItemStack.of(compound.getCompound("Item")) : ItemStack.EMPTY;
      availability.load(compound.getCompound("Availability"));
      ignoreDamage = compound.getBoolean("IgnoreDamage");
      ignoreNBT = compound.getBoolean("IgnoreNBT");
      isGlobal = compound.getBoolean("Global");
      isKnown = compound.getBoolean("IsKnown");
      isShaped = compound.getBoolean("IsShaped");
      showInRecipeBook = compound.getBoolean("ShowInRecipeBook");
      name = compound.getString("Name");
   }

   private boolean checkMatch(CraftingContainer inventoryCrafting, int x, int y, boolean isRevers) {
      for(int i = 0; i < inventoryCrafting.getWidth(); ++i) {
         for(int j = 0; j < inventoryCrafting.getHeight(); ++j) {
            int u = i - x;
            int v = j - y;
            Ingredient ingredient = Ingredient.EMPTY;
            if (u >= 0 && v >= 0 && u < width && v < height) {
               if (isRevers) { ingredient = ingredients.get(width - u - 1 + v * width); }
               else { ingredient = ingredients.get(u + v * width); }
            }
            ItemStack stackInGrid = inventoryCrafting.getItem(i + j * inventoryCrafting.getWidth());
            if (!stackInGrid.isEmpty() && ingredient.getItems().length == 0) { return false; }
            if (!stackInGrid.isEmpty() || ingredient.getItems().length != 0) {
               ItemStack ingredientStack = ingredient.getItems()[0];
               if ((!stackInGrid.isEmpty() || !ingredientStack.isEmpty()) &&
                       !NoppesUtilPlayer.compareItems(ingredientStack, stackInGrid, ignoreDamage, ignoreNBT) &&
                       ingredientStack.getCount() >= stackInGrid.getCount()) { return false; }
            }
         }
      }
      return true;
   }

   public void copy(RecipeCarpentry recipe) {
      availability = recipe.availability;
      isGlobal = recipe.isGlobal;
      isKnown = recipe.isKnown;
      showInRecipeBook = recipe.showInRecipeBook;
      ignoreDamage = recipe.ignoreDamage;
      ignoreNBT = recipe.ignoreNBT;
   }

   public ItemStack getCraftingItem(int slotId) {
      if (slotId >= ingredients.size()) { return ItemStack.EMPTY; }
      for (ItemStack stack : ingredients.get(slotId).getItems()) {
         if (!stack.isEmpty()) { return stack; }
      }
      return ItemStack.EMPTY;
   }

   @Override
   public boolean isValid() {
      return !name.isEmpty() && !getResult().isEmpty() && !isRecipeItemsEmpty();
   }

   @Override
   public WrapperRecipe getWrapperRecipe() {
      wrapper.parent = this;
      wrapper.isShaped = isShaped;
      wrapper.isGlobal = isGlobal;
      wrapper.isKnown = isKnown;
      wrapper.ignoreDamage = ignoreDamage;
      wrapper.ignoreNBT = ignoreNBT;
      wrapper.id = id;
      wrapper.width = width;
      wrapper.height = height;
      wrapper.group = Component.literal(getGroup());
      wrapper.product = result.copy();
      wrapper.availability.load(availability.save(new CompoundTag()));

      wrapper.ingredients.clear();
      int pos = 0;
      for (Ingredient ingr : new ArrayList<>(ingredients)) {
         ItemStack[] items = ingr.getItems();
         ItemStack[] array = new ItemStack[items.length];
         for (int j = 0; j < items.length; j++) {
            array[j] = items[j].copy();
         }
         wrapper.ingredients.put(pos, array);
         pos ++;
      }
      return wrapper;
   }

   public void setGroup(@Nonnull String newGroup) { group = newGroup; }

   public void setId(@Nonnull ResourceLocation newId) { id = newId; }

}
