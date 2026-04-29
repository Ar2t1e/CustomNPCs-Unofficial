package noppes.npcs.api.wrapper;

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
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.data.Availability;
import noppes.npcs.controllers.data.RecipeCarpentry;
import noppes.npcs.shared.common.util.LogWriter;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class WrapperRecipe {

    public final @Nonnull Map<Integer, ItemStack[]> ingredients = new TreeMap<>(); // -> ItemStack[].length == 0 ... 16 max (not null)
    public @Nonnull Availability availability = new Availability();
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

    public WrapperRecipe() {
        product = new ItemStack(Blocks.COBBLESTONE);
        ingredients.put(0, new ItemStack[] { new ItemStack(Blocks.COBBLESTONE) });
    }

    public NBTTagCompound getNbt() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("Id", id.toString());
        compound.setString("Name", id.getResourcePath());
        compound.setString("Group", group.getString());
        compound.setTag("Item", product.writeToNBT(new NBTTagCompound()));
        int size = isGlobal ? 3 : 4;
        int maxX = 0;
        int maxY = 0;
        int minX = -1;
        int minY = -1;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                int slotId = x + y * size;
                if (ingredients.get(slotId).length != 0) {
                    if (maxX < x) { maxX = x; }
                    if (maxY < y) { maxY = y; }
                    if (minX == -1 && minY == -1) { minX = x; minY = y; }
                }
            }
        }
        maxX += 1;
        maxY += 1;
        if (minX < 0) { minX = 0; }
        if (minY < 0) { minY = 0; }
        width = maxX - minX;
        height = maxY - minY;
        compound.setInteger("Width", width);
        compound.setInteger("Height", height);
        LogWriter.info("[DEBUG] WH "+width+", "+height);
        int nsPos = 0;
        NBTTagList list = new NBTTagList();
        for (int slotId = 0; slotId < size * size; slotId++) {
            NBTTagCompound nbt = new NBTTagCompound();
            NBTTagList ings = new NBTTagList();
            if (!isShaped) {
                if (ingredients.get(slotId) != null) {
                    for (ItemStack ing : ingredients.get(slotId)) { ings.appendTag(ing.writeToNBT(new NBTTagCompound())); }
                    if (!ings.hasNoTags()) {
                        nbt.setByte("Slot", (byte) (nsPos++));
                        nbt.setTag("Ingredients", ings);
                    }
                }
            }
            else {
                int x = slotId % size;
                int y = (int) Math.floor((double) slotId / (double) size);
                if (x >= minX && y >= minY && x < minX + width && y < minY + height) {
                    x -= minX;
                    y -= minY;
                    for (ItemStack ing : ingredients.get(slotId)) { ings.appendTag(ing.writeToNBT(new NBTTagCompound())); }
                    nbt.setByte("Slot", (byte) (x + y * width));
                    nbt.setTag("Ingredients", ings);
                }
            }
            if (nbt.hasKey("Slot", 1)) { list.appendTag(nbt); }
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

    public boolean isValid() {
        if (id.getResourcePath().isEmpty() || !id.getResourceDomain().equals(CustomNpcs.MODID) ||
                group.getString().isEmpty() || ingredients.isEmpty() || product.isEmpty()) {
            return false;
        }
        for (ItemStack[] array : new ArrayList<>(ingredients.values())) {
            if (array == null) { continue; }
            for (ItemStack stack : array) {
                if (stack != null && !stack.isEmpty()) { return true; }
            }
        }
        return false;
    }

    public static WrapperRecipe of(@Nonnull IRecipe recipe) {
        if (recipe instanceof RecipeCarpentry) { return ((RecipeCarpentry) recipe).getWrapperRecipe(); }
        WrapperRecipe wrapper = new WrapperRecipe();
        wrapper.parent = recipe;
        wrapper.isGlobal = true;
        wrapper.isKnown = false;
        wrapper.ignoreDamage = false;
        wrapper.ignoreNBT = false;
        wrapper.id = Objects.requireNonNull(recipe.getRegistryName());
        NonNullList<Ingredient> ings = recipe.getIngredients();
        if (recipe instanceof ShapedRecipes) {
            wrapper.isShaped = true;
            wrapper.width = ((ShapedRecipes) recipe).getRecipeWidth();
            wrapper.height = ((ShapedRecipes) recipe).getRecipeHeight();
            for (int slotId = 0; slotId < 9; slotId++) {
                int x = slotId % 3;
                int y = (int) Math.floor((double) slotId / 3.0d);
                if (x < wrapper.width && y < wrapper.height) { wrapper.ingredients.put(slotId, ings.get(x + y * wrapper.width).getMatchingStacks()); }
                else { wrapper.ingredients.put(slotId, new ItemStack[0]); }
            }
        }
        else {
            wrapper.isShaped = false;
            int size = ings.size();
            wrapper.width = Math.max(1, size / 2);
            wrapper.height = Math.max(1, size - wrapper.width);
            for (int slotId = 0; slotId < 9; slotId++) {
                if (slotId < size) { wrapper.ingredients.put(slotId, ings.get(slotId).getMatchingStacks()); }
                else { wrapper.ingredients.put(slotId, new ItemStack[0]); }
            }
        }
        wrapper.group = Component.literal(recipe.getGroup().isEmpty() ? wrapper.id.getResourceDomain() : recipe.getGroup()).withStyle(TextFormatting.GRAY);
        wrapper.product = recipe.getRecipeOutput();
        wrapper.availability.clear();
        return wrapper;
    }

    public Component getName() {
        Component name = Component.literal(id.getResourcePath());
        if (!id.getResourceDomain().equals(CustomNpcs.MODID)) { name = name.withStyle(TextFormatting.GRAY); }
        return name;
    }

}
