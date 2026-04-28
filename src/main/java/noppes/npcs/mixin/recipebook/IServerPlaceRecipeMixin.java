package noppes.npcs.mixin.recipebook;

import net.minecraft.recipebook.ServerPlaceRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = ServerPlaceRecipe.class, priority = 502)
public interface IServerPlaceRecipeMixin {

    @Invoker boolean invokeTestClearGrid();

    @Invoker void invokeClearGrid();

}
