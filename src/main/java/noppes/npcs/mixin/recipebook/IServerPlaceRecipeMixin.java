package noppes.npcs.mixin.recipebook;

import net.minecraft.recipebook.ServerPlaceRecipe;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Iterator;

@Mixin(value = ServerPlaceRecipe.class, priority = 502)
public interface IServerPlaceRecipeMixin {

    @Invoker boolean invokeTestClearGrid();

    @Invoker void invokeClearGrid();

    @Invoker <C extends Container> void invokeHandleRecipeClicked(Recipe<C> recipe, boolean isShiftPress);

    @Invoker int invokeGetStackSize(boolean isShiftPress, int maxStackSize, boolean canPlace);

    @Invoker void invokeAddItemToSlot(Iterator<?> intList, int k1, int stackSize, int k, int i1);

}
