package noppes.npcs.controllers.data;

import net.minecraft.block.Block;
import net.minecraft.inventory.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.Component;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.crafting.IRecipeContainer;
import net.minecraftforge.common.util.RecipeMatcher;
import net.minecraftforge.fml.relauncher.Side;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NBTTags;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.handler.data.INpcRecipe;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.WrapperRecipe;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.util.CustomStackedContents;
import noppes.npcs.util.Util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class RecipeCarpentry implements IRecipe, INpcRecipe {

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
    protected boolean isSimple;

    public RecipeCarpentry(@Nonnull ResourceLocation idIn, @Nonnull String groupIn, int widthIn, int heightIn, boolean isGlobalIn, boolean isShapedIn, @Nonnull NonNullList<Ingredient> recipeItemsIn, @Nonnull ItemStack resultIn) {
        id = idIn;
        group = groupIn;
        width = widthIn;
        height = heightIn;
        ingredients = recipeItemsIn;
        result = resultIn;
        isSimple = ingredients.stream().allMatch(Ingredient::isSimple);

        name = idIn.getResourcePath();
        result = resultIn;
        isShaped = isShapedIn;
        isGlobal = isGlobalIn;

        wrapper = new WrapperRecipe(result);
    }

    public static RecipeCarpentry create(NBTTagCompound compound) {
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
            if (objects[size + 1] instanceof Item) { stack = new ItemStack((Item) objects[size + 1]); }
            else if (objects[size + 1] instanceof Block) { stack = new ItemStack((Block) objects[size + 1], 1); }
            else if (objects[size + 1] instanceof ItemStack) { stack = (ItemStack) objects[size + 1]; }
            mapData.put(character, stack);
        }
        NonNullList<Ingredient> ingredients = NonNullList.create();
        for(int slotId = 0; slotId < widht * height; ++slotId) {
            char c = lineData.charAt(slotId);
            if (mapData.containsKey(c)) {
                ingredients.add(slotId, Ingredient.fromStacks(mapData.get(c).copy()));
            } else {
                ingredients.add(slotId, Ingredient.EMPTY);
            }
        }
        return new RecipeCarpentry(location, group, widht, height, isGlobal, isShaped, ingredients, result);
    }

    @Override
    public boolean matches(@Nonnull InventoryCrafting inventoryCrafting, @Nullable World world) {
        if (isShaped) {
            for(int i = 0; i <= 4 - width; ++i) {
                for(int j = 0; j <= 4 - height; ++j) {
                    if (checkMatch(inventoryCrafting, i, j, true)) { return true; }
                    if (checkMatch(inventoryCrafting, i, j, false)) { return true; }
                }
            }
            return false;
        }
        CustomStackedContents stackedContents = new CustomStackedContents();
        List<ItemStack> inputs = new ArrayList<>();
        int i = 0;
        for(int j = 0; j < inventoryCrafting.getSizeInventory(); ++j) {
            ItemStack itemstack = inventoryCrafting.getStackInSlot(j);
            if (!itemstack.isEmpty()) {
                ++i;
                if (isSimple) { stackedContents.accountStack(itemstack, -1); }
                else { inputs.add(itemstack); }
            }
        }
        return i == ingredients.size() && (isSimple ?
                stackedContents.canCraft(this, null) :
                RecipeMatcher.findMatches(inputs,  ingredients) != null);
    }

    @Override
    public @Nonnull ItemStack getCraftingResult(@Nonnull InventoryCrafting container) { return result.copy(); }

    @Override
    public boolean canFit(int widthIn, int heightIn) {
        return isShaped ? widthIn >= width && heightIn >= height : widthIn * heightIn >= ingredients.size();
    }

    @Override
    public @Nonnull ItemStack getRecipeOutput() { return result.isEmpty() ? ItemStack.EMPTY : result.copy(); }

    @Override
    public @Nonnull NonNullList<ItemStack> getRemainingItems(@Nonnull InventoryCrafting inventoryCrafting) {
        NonNullList<ItemStack> list = NonNullList.withSize(inventoryCrafting.getSizeInventory(), ItemStack.EMPTY);
        for(int i = 0; i < list.size(); ++i) {
            ItemStack itemstack = inventoryCrafting.getStackInSlot(i);
            list.set(i, ForgeHooks.getContainerItem(itemstack));
        }
        return list;
    }

    @Override
    public String getName() { return name; }

    @Override
    public IItemStack getResult() { return Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(getRecipeOutput()); }

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
        if (Util.instance.getSide() == Side.SERVER) {
            RecipeController.getInstance().addAndSaveRecipe(this);
        }
    }

    @Override
    public void delete() {
        if (Util.instance.getSide() == Side.SERVER) {
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
    public IItemStack[][] getRecipe() {
        IItemStack[][] array = new IItemStack[ingredients.size()][];
        for (int i = 0; i < ingredients.size(); i++) {
            if (ingredients.get(i).getMatchingStacks().length > 0) {
                ItemStack[] items = ingredients.get(i).getMatchingStacks();
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
    public boolean isDynamic() { return !showInRecipeBook; }

    @Override
    public @Nonnull ResourceLocation getMCId() { return id; }

    @Override
    public @Nonnull NonNullList<Ingredient> getIngredients() { return ingredients; }

    @Override
    public @Nonnull String getGroup() { return group; }

    @Override
    public boolean isShaped() { return isShaped; }

    @Override
    public void setIsShaped(boolean isShapedIn) { isShaped = isShapedIn; }

    public void setResult(ItemStack newResult) { result = newResult == null || newResult.isEmpty() ? ItemStack.EMPTY : newResult; }

    public NBTTagCompound saveTo() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("Id", id.toString());
        compound.setString("Group", group);
        compound.setInteger("Width", width);
        compound.setInteger("Height", height);
        compound.setTag("Materials", NBTTags.nbtIngredientList(ingredients));
        compound.setTag("Item", result.writeToNBT(new NBTTagCompound()));
        compound.setTag("Availability", availability.save(new NBTTagCompound()));
        compound.setBoolean("IgnoreDamage", ignoreDamage);
        compound.setBoolean("IgnoreNBT", ignoreNBT);
        compound.setBoolean("Global", isGlobal);
        compound.setBoolean("IsKnown", isKnown);
        compound.setBoolean("IsShaped", isShaped);
        compound.setBoolean("ShowInRecipeBook", showInRecipeBook);
        compound.setString("Name", name);
        return compound;
    }

    public void loadFrom(NBTTagCompound compound) {
        if (compound.hasKey("ID", 8)) { id = new ResourceLocation(CustomNpcs.MODID, NoppesUtilServer.validPath(compound.getString("ID"))); }
        else { id = new ResourceLocation(NoppesUtilServer.validLocation(compound.getString("Id"))); }
        group = compound.getString("Group");
        width = compound.getInteger("Width");
        height = compound.getInteger("Height");
        ingredients = NBTTags.getIngredientList(compound.getTagList("Materials", 10));
        isSimple = ingredients.stream().allMatch(Ingredient::isSimple);
        result = compound.hasKey("Item", 10) ? new ItemStack(compound.getCompoundTag("Item")) : ItemStack.EMPTY;
        availability.load(compound.getCompoundTag("Availability"));
        ignoreDamage = compound.getBoolean("IgnoreDamage");
        ignoreNBT = compound.getBoolean("IgnoreNBT");
        isGlobal = compound.getBoolean("Global");
        isKnown = compound.getBoolean("IsKnown");
        isShaped = compound.getBoolean("IsShaped");
        showInRecipeBook = compound.getBoolean("ShowInRecipeBook");
        name = compound.getString("Name");
    }

    private boolean checkMatch(InventoryCrafting inventoryCrafting, int x, int y, boolean isRevers) {
        for(int i = 0; i < 4; ++i) {
            for(int j = 0; j < 4; ++j) {
                int u = i - x;
                int v = j - y;
                Ingredient ingredient = Ingredient.EMPTY;
                if (u >= 0 && v >= 0 && u < width && v < height) {
                    if (isRevers) { ingredient = ingredients.get(width - u - 1 + v * width); }
                    else { ingredient = ingredients.get(u + v * width); }
                }
                ItemStack stack = inventoryCrafting.getStackInSlot(i + j * inventoryCrafting.getWidth());
                if (!stack.isEmpty() && ingredient.getMatchingStacks().length == 0) { return false; }
                if (!stack.isEmpty() || ingredient.getMatchingStacks().length != 0) {
                    ItemStack var9 = ingredient.getMatchingStacks()[0];
                    if ((!stack.isEmpty() || !var9.isEmpty()) && !NoppesUtilPlayer.compareItems(var9, stack, ignoreDamage, ignoreNBT)) { return false; }
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

    @Override
    public boolean isValid() {
        if (name.isEmpty() || ingredients.isEmpty() || getResult().isEmpty()) { return false; }
        for (Ingredient ingredient : ingredients) {
            for (ItemStack stack : ingredient.getMatchingStacks()) {
                if (!stack.isEmpty()) { return true; }
            }
        }
        return false;
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
        wrapper.availability.load(availability.save(new NBTTagCompound()));

        wrapper.ingredients.clear();
        int pos = 0;
        for (Ingredient ingr : new ArrayList<>(ingredients)) {
            ItemStack[] items = ingr.getMatchingStacks();
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
