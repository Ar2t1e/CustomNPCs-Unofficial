package noppes.npcs.mixin.client.renderer;

import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.ChestRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = BlockRendererDispatcher.class, priority = 502)
public interface IBlockRendererDispatcherMixin {

    @Accessor BlockModelRenderer getBlockModelRenderer();

    @Accessor ChestRenderer getChestRenderer();

}
