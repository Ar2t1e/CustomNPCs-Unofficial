package noppes.npcs.mixin.util;

import net.minecraft.util.ServerRecipeBookHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = ServerRecipeBookHelper.class, remap = false, priority = 502)
public interface IServerRecipeBookHelperMixin {

    @Invoker("func_194326_a") void invokeClearGrid();
    @Invoker("func_194328_c") boolean invokeTestClearGrid();

}
