package noppes.npcs.mixin.client.renderer;

import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.color.BlockColors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = BlockModelRenderer.class, priority = 502)
public interface IBlockModelRendererMixin {

    @Accessor BlockColors getBlockColors();

}
