package noppes.npcs.util;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.*;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.BitSet;
import java.util.List;

public class CustomStackedContents extends StackedContents {

    public final Int2IntMap contents = new Int2IntOpenHashMap(); // {itemId, count}

    @Override
    public void accountSimpleStack(@Nonnull ItemStack stack) {
        if (!stack.isDamaged() && !stack.isEnchanted() && !stack.hasCustomHoverName()) { accountStack(stack); }
    }

    @Override
    public void accountStack(@Nonnull ItemStack stack) { accountStack(stack, -1); }

    @Override
    public void accountStack(@Nonnull ItemStack stack, int countIn) {
        if (!stack.isEmpty()) {
            put(getStackingIndex(stack), countIn == -1 ? stack.getCount() : countIn);
        }
    }

    public boolean has(int itemId) { return contents.get(itemId) > 0; }

    public int take(int itemId, int countIn) {
        int count = contents.get(itemId);
        if (count >= countIn) {
            contents.put(itemId, count - countIn);
            return itemId;
        }
        return 0;
    }

    void put(int itemId, int countIn) { contents.put(itemId, contents.get(itemId) + countIn); }

    @Override
    public boolean canCraft(@Nonnull Recipe<?> recipeIn, @Nullable IntList ingredientList) {
        return canCraft(recipeIn, ingredientList, 1);
    }

    @Override
    public boolean canCraft(@Nonnull Recipe<?> recipeIn, @Nullable IntList ingredientList, int countIn) {
        return (new CustomRecipePicker(recipeIn)).tryPick(countIn, ingredientList);
    }

    @Override
    public int getBiggestCraftableStack(@Nonnull Recipe<?> recipeIn, @Nullable IntList ingredientList) {
        return getBiggestCraftableStack(recipeIn, Integer.MAX_VALUE, ingredientList);
    }

    @Override
    public int getBiggestCraftableStack(@Nonnull Recipe<?> recipeIn, int stackLimitCount, @Nullable IntList ingredientList) {
        return (new CustomRecipePicker(recipeIn)).tryPickAll(stackLimitCount, ingredientList);
    }

    @Override
    public void clear() { contents.clear(); }

    public class CustomRecipePicker {
        private final Recipe<?> recipe;
        private final List<Ingredient> ingredients = Lists.newArrayList();
        private final int ingredientCount;
        private final int[] items;
        private final int itemCount;
        private final BitSet data;
        private final IntList path = new IntArrayList();

        public CustomRecipePicker(Recipe<?> recipeIn) {
            recipe = recipeIn;
            ingredients.addAll(recipeIn.getIngredients());
            ingredients.removeIf(Ingredient::isEmpty);
            ingredientCount = ingredients.size();
            items = getUniqueAvailableIngredientItems();
            itemCount = items.length;
            data = new BitSet(ingredientCount + itemCount + ingredientCount + ingredientCount * itemCount);
            for(int i = 0; i < ingredients.size(); ++i) {
                IntList intlist = ingredients.get(i).getStackingIds();
                for(int j = 0; j < itemCount; ++j) {
                    if (intlist.contains(items[j])) { data.set(getIndex(true, j, i)); }
                }
            }
        }

        public boolean tryPick(int stackLimitCount, @Nullable IntList ingredientList) {
            if (stackLimitCount <= 0) { return true; }
            int i;
            for(i = 0; dfs(stackLimitCount); ++i) {
                take(items[path.getInt(0)], stackLimitCount);
                int j = path.size() - 1;
                setSatisfied(path.getInt(j));
                for(int k = 0; k < j; ++k) { toggleResidual((k & 1) == 0, path.getInt(k), path.getInt(k + 1)); }
                path.clear();
                data.clear(0, ingredientCount + itemCount);
            }
            boolean flag = i == ingredientCount;
            boolean flag1 = flag && ingredientList != null;
            if (flag1) { ingredientList.clear(); }
            data.clear(0, ingredientCount + itemCount + ingredientCount);
            int l = 0;
            for (Ingredient ingredient : recipe.getIngredients()) {
                if (flag1 && ingredient.isEmpty()) { ingredientList.add(0); }
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

        private int[] getUniqueAvailableIngredientItems() {
            IntCollection intcollection = new IntAVLTreeSet();
            for(Ingredient ingredient : ingredients) {
                intcollection.addAll(ingredient.getStackingIds());
            }
            IntIterator intiterator = intcollection.iterator();
            while(intiterator.hasNext()) {
                if (!has(intiterator.nextInt())) { intiterator.remove(); }
            }
            return intcollection.toIntArray();
        }

        private boolean dfs(int minCount) {
            int i = itemCount;
            for(int j = 0; j < i; ++j) {
                if (contents.get(items[j]) >= minCount) {
                    visit(false, j);
                    while(!path.isEmpty()) {
                        int k = path.size();
                        boolean notIngredientCount = (k & 1) == 1;
                        int l = path.getInt(k - 1);
                        if (!notIngredientCount && !isSatisfied(l)) {
                            break;
                        }
                        int i1 = notIngredientCount ? ingredientCount : i;
                        for(int j1 = 0; j1 < i1; ++j1) {
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
            int i = isNow ? pathNowInt * ingredientCount + pathNextInt : pathNextInt * ingredientCount + pathNowInt;
            return ingredientCount + itemCount + ingredientCount + 2 * i;
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

        public int tryPickAll(int stackLimitCount, @Nullable IntList ingredientList) {
            int totalCount = 0;
            int min = getMinIngredientCount(stackLimitCount) + 1;
            while(true) {
                int count = (totalCount + min) / 2;
                if (tryPick(count, null)) {
                    if (min - totalCount <= 1) {
                        if (count > 0) { tryPick(count, ingredientList); }
                        return count;
                    }
                    totalCount = count;
                }
                else { min = count; }
            }
        }

        private int getMinIngredientCount(int stackLimitCount) {
            int count = Integer.MAX_VALUE;
            for (Ingredient ingredient : ingredients) {
                int totalCount = stackLimitCount;
                // Parent code changed here:
                IntList intlist = ingredient.getStackingIds();
                for (int i = 0; i < intlist.size(); i++) {
                    if (!contents.containsKey(intlist.getInt(i))) { continue; }
                    ItemStack stack = ingredient.getItems()[i];
                    totalCount = Math.max(totalCount, contents.get(intlist.getInt(i)) / stack.getCount());
                    if (stack.getMaxStackSize() < stackLimitCount) {
                        stackLimitCount = stack.getMaxStackSize();
                        totalCount = stackLimitCount;
                        i = -1;
                    }
                    else if (stack.getCount() > 1) {
                        int maxStack = Math.max(1, (int) Math.floor((double) stack.getMaxStackSize() / (double) stack.getCount()));
                        if (totalCount > maxStack) { totalCount = maxStack; }
                    }
                }
                if (count > 0) { count = Math.min(count, totalCount); }
            }
            return count;
        }
    }

}
