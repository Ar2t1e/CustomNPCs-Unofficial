package noppes.npcs.util;

import it.unimi.dsi.fastutil.ints.*;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class CustomStackedContents extends RecipeItemHelper {

    public final Int2IntMap contents = new Int2IntOpenHashMap();

    @Override
    public void accountStack(@Nonnull ItemStack stack) { accountStack(stack, -1); }

    @Override
    public void accountStack(@Nonnull ItemStack stack, int countIn) {
        if (!stack.isEmpty() && !stack.isItemDamaged() && !stack.isItemEnchanted() && !stack.hasDisplayName()) {
            put(pack(stack), countIn == -1 ? stack.getCount() : countIn);
        }
    }

    public boolean containsItem(int itemId) { return contents.get(itemId) > 0; }

    @Override
    public int tryTake(int itemId, int countIn) {
        int count = contents.get(itemId);
        if (count >= countIn) {
            contents.put(itemId, count - countIn);
            return itemId;
        }
        return 0;
    }

    private void put(int itemId, int countIn) { contents.put(itemId, contents.get(itemId) + countIn); }

    @Override
    public boolean canCraft(@Nonnull IRecipe recipeIn, @Nullable IntList ingredientList) { return canCraft(recipeIn, ingredientList, 1); }

    @Override
    public boolean canCraft(@Nonnull IRecipe recipeIn, @Nullable IntList ingredientList, int countIn) {
        return (new CustomRecipePicker(recipeIn)).tryPick(countIn, ingredientList);
    }

    @Override
    public int getBiggestCraftableStack(@Nonnull IRecipe recipeIn, @Nullable IntList ingredientList) {
        return getBiggestCraftableStack(recipeIn, Integer.MAX_VALUE, ingredientList);
    }

    @Override
    public int getBiggestCraftableStack(@Nonnull IRecipe recipeIn, int stackLimitCount, @Nullable IntList ingredientList) {
        return (new CustomRecipePicker(recipeIn)).tryPickAll(stackLimitCount, ingredientList);
    }

    @Override
    public void clear() { contents.clear(); }

    public class CustomRecipePicker {

        private final IRecipe recipe;
        private final List<Ingredient> ingredients = new ArrayList<>();
        private final int ingredientCount;
        private final int[] items;
        private final int itemCount;
        private final BitSet data;
        private final IntList path = new IntArrayList();

        public CustomRecipePicker(IRecipe iRecipe) {
            recipe = iRecipe;
            ingredients.addAll(iRecipe.getIngredients());
            ingredients.removeIf((ingredient) -> ingredient == Ingredient.EMPTY);
            ingredientCount = ingredients.size();
            items = getUniqueAvailIngredientItems();
            itemCount = items.length;
            data = new BitSet(ingredientCount + itemCount + ingredientCount + ingredientCount * itemCount);
            for (int i = 0; i < ingredients.size(); ++i) {
                IntList intlist = ingredients.get(i).getValidItemStacksPacked();
                for (int j = 0; j < itemCount; ++j) {
                    if (intlist.contains(items[j])) {
                        data.set(getIndex(true, j, i));
                    }
                }
            }
        }

        public boolean tryPick(int stackLimitCount, @Nullable IntList ingredientList) {
            if (stackLimitCount <= 0) { return true; }
            int i;
            for (i = 0; dfs(stackLimitCount); ++i) {
                tryTake(items[path.getInt(0)], stackLimitCount);
                int l = path.size() - 1;
                setSatisfied(path.getInt(l));
                for (int i1 = 0; i1 < l; ++i1) { toggleResidual((i1 & 1) == 0, path.getInt(i1), path.getInt(i1 + 1)); }
                path.clear();
                data.clear(0, ingredientCount + itemCount);
            }
            boolean flag = i == ingredientCount;
            boolean flag1 = flag && ingredientList != null;
            if (flag1) { ingredientList.clear(); }
            data.clear(0, ingredientCount + itemCount + ingredientCount);
            int l = 0;
            List<Ingredient> list = recipe.getIngredients();
            /*if (recipe instanceof NpcShapedRecipes) {
                Object[] objs = ((NpcShapedRecipes) recipe).getGrid();
                list = (NonNullList<Ingredient>) objs[2];
            }*/
            for (Ingredient ingredient : list) {
                if (flag1 && ingredient == Ingredient.EMPTY) { ingredientList.add(0); }
                else {
                    for (int j1 = 0; j1 < itemCount; ++j1) {
                        if (hasResidual(false, l, j1)) {
                            toggleResidual(true, j1, l);
                            put(items[j1], stackLimitCount);
                            if (flag1) { ingredientList.add(items[j1]); }
                        }
                    }
                    ++l;
                }
            }
            return flag;
        }

        private int[] getUniqueAvailIngredientItems() {
            IntCollection intcollection = new IntAVLTreeSet();
            for (Ingredient ingredient : ingredients) {
                intcollection.addAll(ingredient.getValidItemStacksPacked());
            }
            IntIterator intiterator = intcollection.iterator();
            while (intiterator.hasNext()) {
                if (!containsItem(intiterator.nextInt())) { intiterator.remove(); }
            }
            return intcollection.toIntArray();
        }

        private boolean dfs(int minCount) {
            int i = itemCount;
            for (int j = 0; j < i; ++j) {
                if (contents.get(items[j]) >= minCount) {
                    visit(false, j);
                    while (!path.isEmpty()) {
                        int k = path.size();
                        boolean notIngredientCount = (k & 1) == 1;
                        int l = path.getInt(k - 1);
                        if (!notIngredientCount && !isSatisfied(l)) { break; }
                        int i1 = notIngredientCount ? ingredientCount : i;
                        for (int j1 = 0; j1 < i1; ++j1) {
                            if (!hasVisited(notIngredientCount, j1) && hasConnection(notIngredientCount, l, j1) && hasResidual(notIngredientCount, l, j1)) {
                                visit(notIngredientCount, j1);
                                break;
                            }
                        }
                        int k1 = path.size();
                        if (k1 == k) { path.removeInt(k1 - 1); }
                    }
                    if (!path.isEmpty()) { return true; }
                }
            }
            return false;
        }

        private boolean isSatisfied(int itemId) { return data.get(getSatisfiedIndex(itemId)); }

        private void setSatisfied(int itemId) { data.set(getSatisfiedIndex(itemId)); }

        private int getSatisfiedIndex(int itemId) { return ingredientCount + itemCount + itemId; }

        private boolean hasConnection(boolean isNow, int pathNowInt, int itemId) {
            return data.get(getIndex(isNow, pathNowInt, itemId));
        }

        private boolean hasResidual(boolean isNow, int pathNowInt, int itemId) {
            return isNow != data.get(1 + getIndex(isNow, pathNowInt, itemId));
        }

        private void toggleResidual(boolean isNow, int pathNowInt, int pathNextInt) {
            data.flip(1 + getIndex(isNow, pathNowInt, pathNextInt));
        }

        private int getIndex(boolean isNow, int pathNowInt, int pathNextInt) {
            int k = isNow ? pathNowInt * ingredientCount + pathNextInt : pathNextInt * ingredientCount + pathNowInt;
            return ingredientCount + itemCount + ingredientCount + 2 * k;
        }

        private void visit(boolean notIngredientCount, int itemId) {
            data.set(getVisitedIndex(notIngredientCount, itemId));
            path.add(itemId);
        }

        private boolean hasVisited(boolean notIngredientCount, int itemId) {
            return data.get(getVisitedIndex(notIngredientCount, itemId));
        }

        private int getVisitedIndex(boolean notIngredientCount, int itemId) {
            return (notIngredientCount ? 0 : ingredientCount) + itemId;
        }

        public int tryPickAll(int stackLimitCount, @Nullable IntList list) {
            int i = 0;
            int min = getMinIngredientCount(stackLimitCount) + 1;
            while (true) {
                int count = (i + min) / 2;
                if (tryPick(count, null)) {
                    if (min - i <= 1) {
                        if (count > 0) { tryPick(count, null); }
                        return count;
                    }
                    i = count;
                }
                else { min = count; }
            }
        }

        private int getMinIngredientCount(int stackLimitCount) {
            int count = Integer.MAX_VALUE;
            for (Ingredient ingredient : ingredients) {
                int max = stackLimitCount;
                // Parent code changed here:
                IntList intlist = ingredient.getValidItemStacksPacked();
                for (int i = 0; i < intlist.size(); i++) {
                    if (!contents.containsKey(intlist.get(i))) { continue; }
                    ItemStack stack = ingredient.getMatchingStacks()[i];
                    max = Math.max(max, contents.get(intlist.get(i)) / stack.getCount());
                    if (stack.getMaxStackSize() < stackLimitCount) {
                        stackLimitCount = stack.getMaxStackSize();
                        max = stackLimitCount;
                        i = -1;
                    }
                    else if (stack.getCount() > 1) {
                        int maxStack = Math.max(1, (int) Math.floor((double) stack.getMaxStackSize() / (double) stack.getCount()));
                        if (max > maxStack) { max = maxStack; }
                    }
                }
                if (count > 0) { count = Math.min(count, max); }
            }
            return count;
        }

    }

}
