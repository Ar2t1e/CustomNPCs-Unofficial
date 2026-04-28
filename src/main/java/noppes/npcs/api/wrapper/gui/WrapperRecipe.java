package noppes.npcs.api.wrapper.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.block.Blocks;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.data.Availability;
import noppes.npcs.controllers.data.RecipeCarpentry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Map;
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
    public CraftingRecipe parent = null;

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

    public CompoundTag getNbt() {
        CompoundTag compound = new CompoundTag();
        compound.putString("Id", id.toString());
        compound.putString("Name", id.getPath());
        compound.putString("Group", group.getString());
        compound.putInt("Width", width);
        compound.putInt("Height", height);
        compound.put("Item", product.save(new CompoundTag()));
        // NBTTags.nbtIngredientList(recipeItems)
        ListTag list = new ListTag();
        for (int slot = 0; slot < (isGlobal ? 9 : 16); slot++) {
            CompoundTag nbt = new CompoundTag();
            nbt.putByte("Slot", (byte) slot);
            ListTag ings = new ListTag();
            if (ingredients.get(slot) != null) {
                for (ItemStack ing : ingredients.get(slot)) { ings.add(ing.save(new CompoundTag())); }
            }
            nbt.put("Ingredients", ings);
            list.add(nbt);
        }
        compound.put("Materials", list);
        compound.put("Availability", availability.save(new CompoundTag()));
        compound.putBoolean("Global", isGlobal);
        compound.putBoolean("IgnoreDamage", ignoreDamage);
        compound.putBoolean("IgnoreNBT", ignoreNBT);
        compound.putBoolean("IsKnown", isKnown);
        compound.putBoolean("IsShaped", isShaped);
        compound.putBoolean("ShowInRecipeBook", true);
        return compound;
    }

    public boolean isValid() {
        if (id.getPath().isEmpty() || !id.getNamespace().equals(CustomNpcs.MODID) ||
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
        availability.load(wrapper.availability.save(new CompoundTag()));
        parent = wrapper.parent;
    }

    public void copyFrom(Player player, CraftingRecipe recipe) {
        if (recipe instanceof RecipeCarpentry npcRecipe) {
            copyFrom(npcRecipe.getWrapperRecipe());
            return;
        }
        clear();
        parent = recipe;
        isGlobal = true;
        isKnown = false;
        ignoreDamage = false;
        ignoreNBT = false;
        id = recipe.getId();
        int pos = 0;
        NonNullList<Ingredient> ings = recipe.getIngredients();
        if (recipe instanceof ShapedRecipe sRecipe) {
            isShaped = true;
            width = sRecipe.getRecipeWidth();
            height = sRecipe.getRecipeHeight();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int index = y * width + x;
                    ItemStack[] items = ings.get(index).getItems();
                    ItemStack[] array = new ItemStack[items.length];
                    for (int j = 0; j < items.length; j++) {
                        array[j] = items[j].copy();
                    }
                    int slotIndex = y * 3 + x;
                    ings.add(slotIndex, Ingredient.of(array));
                }
            }
            for (int slotIndex = 0; slotIndex < 9; slotIndex++) {
                if (slotIndex < ings.size()) {
                    ings.add(slotIndex, Ingredient.of(ItemStack.EMPTY));
                }
            }
        }
        else {
            isShaped = false;
            int size = ings.size();
            width = size / 2;
            height = size - width;
            for (Ingredient ingr : ings) {
                ItemStack[] items = ingr.getItems();
                ItemStack[] array = new ItemStack[items.length];
                for (int j = 0; j < items.length; j++) {
                    array[j] = items[j].copy();
                }
                ingredients.put(pos, array);
                pos ++;
            }
        }
        group = Component.literal(recipe.getGroup().isEmpty() ? id.getNamespace() : recipe.getGroup()).withStyle(ChatFormatting.GRAY);
        product = recipe.getResultItem(player.level().registryAccess());
        availability.clear();
    }

    public Component getName() {
        MutableComponent name = Component.literal(id.getPath());
        if (!id.getNamespace().equals(CustomNpcs.MODID)) { name = name.withStyle(ChatFormatting.GRAY); }
        return name;
    }

}
