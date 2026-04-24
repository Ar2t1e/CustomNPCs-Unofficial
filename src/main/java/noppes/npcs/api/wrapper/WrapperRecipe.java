package noppes.npcs.api.wrapper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.Component;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.crafting.IShapedRecipe;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.handler.data.INpcRecipe;
import noppes.npcs.controllers.data.Availability;
import noppes.npcs.controllers.data.RecipeCarpentry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class WrapperRecipe {

    public final @Nonnull Map<Integer, ItemStack[]> ingredients = new TreeMap<>(); // -> ItemStack[].length == 0 ... 16 max (not null)
    public final @Nonnull Availability availability = new Availability();
    public @Nonnull ResourceLocation id = new ResourceLocation(CustomNpcs.MODID, "");
    public @Nonnull ItemStack product;
    public boolean isGlobal = true;
    public boolean isKnown = false;
    public boolean ignoreDamage = false;
    public boolean ignoreNBT = false;
    public boolean isShaped = true;
    public int width = 3;
    public int height = 3;
    public Component group = Component.empty();
    public IRecipe parent = null;

    public WrapperRecipe(@Nonnull ItemStack productIn) {
        product = productIn.isEmpty() ? new ItemStack(Blocks.COBBLESTONE) : productIn;
        ingredients.clear();
        ingredients.put(0, new ItemStack[] { new ItemStack(Blocks.COBBLESTONE) });
    }

    public void clear() {
        isKnown = false;
        ignoreDamage = false;
        ignoreNBT = false;
        isShaped = true;
        id = new ResourceLocation(CustomNpcs.MODID, "");
        width = 3;
        height = 3;
        group = Component.empty();
        ingredients.clear();
        ingredients.put(0, new ItemStack[]{ new ItemStack(Blocks.COBBLESTONE) });
        product = new ItemStack(Blocks.COBBLESTONE);
        availability.clear();
    }

    public NBTTagCompound getNbt() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("Id", id.toString());
        compound.setString("Name", id.getResourcePath());
        compound.setString("Group", group.getString());
        compound.setInteger("Width", width);
        compound.setInteger("Height", height);
        compound.setTag("Item", product.writeToNBT(new NBTTagCompound()));
        NBTTagList list = new NBTTagList();
        for (int slot = 0; slot < (isGlobal ? 9 : 16); slot++) {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setByte("Slot", (byte) slot);
            NBTTagList ings = new NBTTagList();
            if (ingredients.get(slot) != null) {
                for (ItemStack ing : ingredients.get(slot)) { ings.appendTag(ing.writeToNBT(new NBTTagCompound())); }
            }
            nbt.setTag("Ingredients", ings);
            list.appendTag(nbt);
        }
        compound.setTag("Materials", list);
        compound.setTag("Availability", availability.save(new NBTTagCompound()));
        compound.setBoolean("Global", isGlobal);
        compound.setBoolean("IgnoreDamage", ignoreDamage);
        compound.setBoolean("IgnoreNBT", ignoreNBT);
        compound.setBoolean("IsKnown", isKnown);
        compound.setBoolean("IsShaped", isShaped);
        compound.setBoolean("ShowInRecipeBook", true);
        return compound;
    }

    public boolean isValid(boolean ignoreIngredients) {
        if (id.getResourcePath().isEmpty() || !id.getResourceDomain().equals(CustomNpcs.MODID) ||
                !group.getString().isEmpty() || ingredients.isEmpty() || product.isEmpty()) { return false; }
        if (ignoreIngredients) { return true; }
        for (ItemStack[] array : new ArrayList<>(ingredients.values())) {
            if (array == null) { continue; }
            for (ItemStack stack : array) {
                if (stack != null && !stack.isEmpty()) { return true; }
            }
        }
        return false;
    }

    public void copyFrom(WrapperRecipe wrapper) {
        isGlobal = wrapper.isGlobal;
        isKnown = wrapper.isKnown;
        ignoreDamage = wrapper.ignoreDamage;
        ignoreNBT = wrapper.ignoreNBT;
        isShaped = wrapper.isShaped;
        id = wrapper.id;
        width = wrapper.width;
        height = wrapper.height;
        group = wrapper.group;
        ingredients.clear();
        ingredients.putAll(wrapper.ingredients);
        product = wrapper.product;
        availability.load(wrapper.availability.save(new NBTTagCompound()));
        parent = wrapper.parent;
    }

    public void copyFrom(EntityPlayer player, IRecipe recipe) {
        if (recipe instanceof RecipeCarpentry) {
            copyFrom(((RecipeCarpentry) recipe).getWrapperRecipe());
            return;
        }
        clear();
        parent = recipe;
        isGlobal = true;
        isKnown = false;
        ignoreDamage = false;
        ignoreNBT = false;
        id = Objects.requireNonNull(recipe.getRegistryName());
        int pos = 0;
        NonNullList<Ingredient> ings = recipe.getIngredients();
        if (recipe instanceof ShapedRecipes) {
            isShaped = true;
            width = ((ShapedRecipes) recipe).getRecipeWidth();
            height = ((ShapedRecipes) recipe).getRecipeHeight();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int index = y * width + x;
                    ItemStack[] items = ings.get(index).getMatchingStacks();
                    ItemStack[] array = new ItemStack[items.length];
                    for (int j = 0; j < items.length; j++) {
                        array[j] = items[j].copy();
                    }
                    int slotIndex = y * 3 + x;
                    ings.add(slotIndex, Ingredient.fromStacks(array));
                }
            }
            for (int slotIndex = 0; slotIndex < 9; slotIndex++) {
                if (slotIndex < ings.size()) {
                    ings.add(slotIndex, Ingredient.fromStacks(ItemStack.EMPTY));
                }
            }
        }
        else {
            isShaped = false;
            int size = ings.size();
            width = size / 2;
            height = size - width;
            for (Ingredient ingr : ings) {
                ItemStack[] items = ingr.getMatchingStacks();
                ItemStack[] array = new ItemStack[items.length];
                for (int j = 0; j < items.length; j++) {
                    array[j] = items[j].copy();
                }
                ingredients.put(pos, array);
                pos ++;
            }
        }
        group = Component.literal(recipe.getGroup().isEmpty() ? id.getResourceDomain() : recipe.getGroup()).withStyle(TextFormatting.GRAY);
        product = recipe.getRecipeOutput();
        availability.clear();
    }

    public Component getName() {
        Component name = Component.literal(id.getResourcePath());
        if (!id.getResourceDomain().equals(CustomNpcs.MODID)) { name = name.withStyle(TextFormatting.GRAY); }
        return name;
    }

}
