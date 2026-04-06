package noppes.npcs.mixin.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(value = LivingEntityRenderer.class, priority = 502)
public interface ILivingRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayerParent<T, M> {

    @Invoker void callScale(T var1, PoseStack var2, float var3);

    @Invoker float callGetBob(T var1, float var2);

    @Accessor List<RenderLayer<T, M>> getLayers();

}
