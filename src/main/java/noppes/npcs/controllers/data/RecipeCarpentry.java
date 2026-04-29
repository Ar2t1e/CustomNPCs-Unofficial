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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.GameData;
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
import noppes.npcs.api.wrapper.WrapperRecipe;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.util.CustomRecipeMatcher;
import noppes.npcs.util.Util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RecipeCarpentry implements IRecipe, INpcRecipe {

    public final Availability availability = new Availability();
    public boolean isGlobal;
    public boolean ignoreDamage = false;
    public boolean ignoreNBT = false;
    public String name;

    // New from Unofficial (BetaZavr)
    protected @Nonnull NonNullList<Ingredient> ingredients;
    protected @Nonnull ItemStack result;
    protected @Nonnull ResourceLocation id;
    protected @Nonnull String group;
    protected int width;
    protected int height;
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

        name = idIn.getResourcePath();
        result = resultIn;
        isShaped = isShapedIn;
        isGlobal = isGlobalIn;
    }

    public static RecipeCarpentry create(NBTTagCompound compound) {
        RecipeCarpentry recipe = new RecipeCarpentry(new ResourceLocation(CustomNpcs.MODID, ""), "", 0, 0, true, true, NonNullList.create(), ItemStack.EMPTY);
        recipe.loadFrom(compound);
        return recipe;
    }

    public static RecipeCarpentry createRecipe(@Nonnull ResourceLocation location, String group, boolean isGlobal, boolean isShaped, ItemStack result, Object... objects) {
        StringBuilder lineData = new StringBuilder();
        int size = 0;
        int width = 0;
        int height = 0;
        if (objects[size] instanceof String[]) {
            for (String line : (String[]) objects[size++]) {
                ++height;
                width = line.length();
                lineData.append(line);
            }
        }
        else {
            while(objects[size] instanceof String) {
                String line = (String) objects[size++];
                ++height;
                width = line.length();
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
        for(int slotId = 0; slotId < width * height; ++slotId) {
            char c = lineData.charAt(slotId);
            if (mapData.containsKey(c)) {
                ingredients.add(slotId, Ingredient.fromStacks(mapData.get(c).copy()));
            } else {
                ingredients.add(slotId, Ingredient.EMPTY);
            }
        }
        return new RecipeCarpentry(location, group, width, height, isGlobal, isShaped, ingredients, result);
    }

    @Override
    public boolean matches(@Nonnull InventoryCrafting inventoryCrafting, @Nullable World world) {
        if (isShaped) {
            for(int x = 0; x <= 4 - width; ++x) {
                for(int y = 0; y <= 4 - height; ++y) {
                    if (checkMatch(inventoryCrafting, x, y, true)) { return true; }
                    if (checkMatch(inventoryCrafting, x, y, false)) { return true; }
                }
            }
            return false;
        }
        List<ItemStack> inputs = new ArrayList<>();
        for(int slotId = 0; slotId < inventoryCrafting.getSizeInventory(); ++slotId) {
            ItemStack stackInGrid = inventoryCrafting.getStackInSlot(slotId);
            if (!stackInGrid.isEmpty()) { inputs.add(stackInGrid); }
        }
        return CustomRecipeMatcher.findMatches(inputs, ingredients.stream().filter(ing -> ing.getMatchingStacks().length !=0).collect(Collectors.toList()), ignoreDamage, ignoreNBT) != null;
    }

    @Override
    public @Nonnull ItemStack getCraftingResult(@Nonnull InventoryCrafting container) { return result.copy(); }

    @Override
    public boolean canFit(int widthIn, int heightIn) {
        if (isShaped) { return widthIn >= width && heightIn >= height; }
        return (long) widthIn * heightIn >= ingredients.stream().filter(ing -> ing.getMatchingStacks().length != 0).count();
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
    public void setIsGlobal(boolean isGlobalIn) {
        if (isGlobal != isGlobalIn && Util.instance.getSide() == Side.SERVER) {
            isGlobal = isGlobalIn;
            RecipeController.getInstance().updateToAll();
        }
    }

    @Override
    public boolean getIgnoreNBT() { return ignoreNBT; }

    @Override
    public void setIgnoreNBT(boolean ignoreNBTIn) {
        if (ignoreNBT != ignoreNBTIn && Util.instance.getSide() == Side.SERVER) {
            ignoreNBT = ignoreNBTIn;
            RecipeController.getInstance().updateToAll();
        }
    }

    @Override
    public boolean getIgnoreDamage() { return ignoreDamage; }

    @Override
    public void setIgnoreDamage(boolean ignoreDamageIn) {
        if (ignoreDamage != ignoreDamageIn && Util.instance.getSide() == Side.SERVER) {
            ignoreDamage = ignoreDamageIn;
            RecipeController.getInstance().updateToAll();
        }
    }

    @Override
    public int getWidth() { return width; }

    @Override
    public int getHeight() { return height; }

    @Override
    public void save() {
        if (Util.instance.getSide() == Side.SERVER) { RecipeController.getInstance().addAndSaveRecipe(this); }
    }

    @Override
    public void delete() {
        if (Util.instance.getSide() == Side.SERVER) { RecipeController.getInstance().delete(id); }
    }

    @Override
    public boolean isKnown() { return isKnown; }

    @Override
    public void setIsKnown(boolean isKnownIn) {
        if (isKnown != isKnownIn && Util.instance.getSide() == Side.SERVER) {
            isKnown = isKnownIn;
            RecipeController.getInstance().updateToAll();
        }
    }

    @Override
    public boolean showInRecipeBook() { return showInRecipeBook; }

    @Override
    public void setShowInRecipeBook(boolean showInRecipeBookIn) {
        if (showInRecipeBook != showInRecipeBookIn && Util.instance.getSide() == Side.SERVER) {
            showInRecipeBook = showInRecipeBookIn;
            RecipeController.getInstance().updateToAll();
        }
    }

    @Override
    public IAvailability getAvailability() { return availability; }

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
    public boolean isDynamic() { return !showInRecipeBook; }

    @Nonnull
    @Override
    public ResourceLocation getRegistryName() { return id; }

    @Override
    public @Nonnull ResourceLocation getMCId() { return id; }

    @Override
    public INbt getNbt() { return new NBTWrapper(saveTo()); }

    @Override
    public String getNpcGroup() { return getGroup(); }

    @Override
    public void setNbt(INbt nbt) {
        if (nbt != null && Util.instance.getSide() == Side.SERVER) {
            loadFrom(nbt.getMCNBT());
            RecipeController.getInstance().updateToAll();
        }
    }

    @Override
    public boolean isRecipeItemsEmpty() {
        if (!ingredients.isEmpty()) {
            for (Ingredient ingredient : ingredients) {
                for (ItemStack stack : ingredient.getMatchingStacks()) {
                    if (!stack.isEmpty()) { return false; }
                }
            }
        }
        return true;
    }

    @Override
    public @Nonnull NonNullList<Ingredient> getIngredients() { return ingredients; }

    @Override
    public Class<IRecipe> getRegistryType() { return IRecipe.class; }

    @Override
    public @Nonnull String getGroup() { return group; }

    @Override
    public boolean isShaped() { return isShaped; }

    @Override
    public void setIsShaped(boolean isShapedIn) {
        if (isShaped != isShapedIn && Util.instance.getSide() == Side.SERVER) {
            isShaped = isShapedIn;
            RecipeController.getInstance().updateToAll();
        }
    }

    @Override
    public void setResult(ItemStack newResult) {
        ItemStack resultIn = newResult == null ? ItemStack.EMPTY : newResult;
        if (!NoppesUtilPlayer.compareItems(result, resultIn, ignoreDamage, ignoreNBT) && Util.instance.getSide() == Side.SERVER) {
            result = resultIn;
            RecipeController.getInstance().updateToAll();
        }
    }

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
                ItemStack stackInGrid = inventoryCrafting.getStackInSlot(i + j * inventoryCrafting.getWidth());
                if (ingredient.getMatchingStacks().length != 0) {
                    if (stackInGrid.isEmpty()) { return false; }
                    ItemStack[] items = ingredient.getMatchingStacks();
                    boolean found = false;
                    for (int k = 0; k < items.length; k++) {
                        if (NoppesUtilPlayer.compareItems(items[0], stackInGrid, ignoreDamage, ignoreNBT) &&
                                items[0].getCount() <= stackInGrid.getCount()) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) { return false; }
                }
                else if (!stackInGrid.isEmpty()) { return false; }
            }
        }
        return true;
    }

    @Override
    public boolean isValid() {
        return !name.isEmpty() && !getResult().isEmpty() && !isRecipeItemsEmpty();
    }
    @Override
    public WrapperRecipe getWrapperRecipe() {
        WrapperRecipe wrapper = new WrapperRecipe();
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
        wrapper.availability = availability;

        wrapper.ingredients.clear();
        int size = isGlobal ? 3 : 4;
        for (int slotId = 0; slotId < size * size; slotId++) {
            int x = slotId % size;
            int y = (int) Math.floor((double) slotId / (double) size);
            if (isShaped) {
                if (x < width && y < height) { wrapper.ingredients.put(slotId, ingredients.get(x + y * width).getMatchingStacks()); }
                else { wrapper.ingredients.put(slotId, new ItemStack[0]); }
            } else {
                if (slotId < ingredients.size()) { wrapper.ingredients.put(slotId, ingredients.get(slotId).getMatchingStacks()); }
                else { wrapper.ingredients.put(slotId, new ItemStack[0]); }
            }
        }
        return wrapper;
    }

    public void setGroup(String newGroup) {
        if (newGroup != null && !group.equals(newGroup) && Util.instance.getSide() == Side.SERVER) {
            group = newGroup;
            RecipeController.getInstance().updateToAll();
        }
    }

    public void setId(@Nonnull ResourceLocation newId) { setRegistryName(newId); }

    @Override
    public IRecipe setRegistryName(ResourceLocation newId) {
        if (newId != null && !id.equals(newId) && Util.instance.getSide() == Side.SERVER) {
            id = GameData.checkPrefix(newId.toString(), true);
            RecipeController.getInstance().updateToAll();
        }
        return this;
    }

}
