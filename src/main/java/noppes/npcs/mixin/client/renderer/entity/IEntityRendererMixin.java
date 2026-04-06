package noppes.npcs.mixin.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = EntityRenderer.class, priority = 502)
public interface IEntityRendererMixin {

    @Accessor EntityRenderDispatcher getEntityRenderDispatcher();

}
