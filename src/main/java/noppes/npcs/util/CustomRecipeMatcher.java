package noppes.npcs.util;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import noppes.npcs.NoppesUtilPlayer;

import java.util.*;

public class CustomRecipeMatcher {

    public static int[] findMatches(List<ItemStack> inputs, List<Ingredient> tests, boolean ignoreDamage, boolean ignoreNBT) {
        int elements = inputs.size();
        if (elements != tests.size()) { return null; }
        int[] ret = new int[elements];
        Arrays.fill(ret, -1);
        BitSet data = new BitSet((elements + 2) * elements);
        for (int x = 0; x < elements; x++) {
            int matched = 0;
            int offset = (x + 2) * elements;
            Ingredient ingredient = tests.get(x);
            for (int y = 0; y < elements; y++) {
                ItemStack stack = inputs.get(y);
                if (!data.get(y) && test(stack, ingredient, ignoreDamage, ignoreNBT)) {
                    data.set(offset + y);
                    matched++;
                }
            }
            if (matched == 0 || (matched == 1 && !claim(ret, data, x, elements))) { return null; }
        }
        if (data.nextClearBit(0) >= elements) { return ret; }
        if (backtrack(data, ret, 0, elements)) { return ret; }
        return null;
    }

    private static boolean test(ItemStack inputStack, Ingredient ingredient, boolean ignoreDamage, boolean ignoreNBT) {
        if (inputStack == null) { return false; }
        if (ingredient.getMatchingStacks().length == 0) { return inputStack.isEmpty(); }
        for(ItemStack itemstack : ingredient.getMatchingStacks()) {
            if (NoppesUtilPlayer.compareItems(itemstack, inputStack, ignoreDamage, ignoreNBT) &&
                    inputStack.getCount() >= itemstack.getCount()) { return true; }
        }
        return false;
    }

    private static boolean claim(int[] ret, BitSet data, int claimed, int elements) {
        Queue<Integer> pending = new LinkedList<>();
        pending.add(claimed);
        while (pending.peek() != null) {
            int test = pending.poll();
            int offset = (test + 2) * elements;
            int used = data.nextSetBit(offset) - offset;
            if (used >= elements || used < 0) {
                throw new IllegalStateException("What? We matched something, but it wasn't set in the range of this test! Test: " + test +  " Used: " + used);
            }
            data.set(used);
            data.set(elements + test);
            ret[used] = test;
            for (int x = 0; x < elements; x++) {
                offset = (x + 2) * elements;
                if (data.get(offset + used) && !data.get(elements + x)) {
                    data.clear(offset + used);
                    int count = 0;
                    for (int y = offset; y < offset + elements; y++) {
                        if (data.get(y)) { count++; }
                    }
                    if (count == 0) { return false; }
                    if (count == 1) { pending.add(x); }
                }
            }
        }
        return true;
    }

    private static boolean backtrack(BitSet data, int[] ret, int start, int elements) {
        int test = data.nextClearBit(elements + start) - elements;
        if (test >= elements) { return true; }
        if (test < 0) {
            throw new IllegalStateException("This should never happen, negative test in backtrack!");
        }
        int offset = (test + 2) * elements;
        for (int x = 0; x < elements; x++) {
            if (data.get(offset + x) && !data.get(x)) {
                data.set(x);
                if (backtrack(data, ret, test + 1, elements)) {
                    ret[x] = test;
                    return true;
                }
                data.clear(x);
            }
        }
        return false;
    }

}
