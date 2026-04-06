package noppes.npcs.mixin.client.renderer.entity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import noppes.npcs.client.renderer.RenderCustomNpc;
import noppes.npcs.entity.EntityCustomNpc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;

@Mixin(value = LivingEntityRenderer.class, priority = 498)
public class LivingRendererMixin<T extends EntityCustomNpc, M extends HumanoidModel<T>> {

   @SuppressWarnings("unchecked")
   @Inject(
      at = {@At("HEAD")},
      method = {"addLayer"}
   )
   private void npcs$addLayer(RenderLayer<T, M> layer, CallbackInfoReturnable<Boolean> cir) {
      if ((LivingEntityRenderer<T, M>) (Object) this instanceof RenderCustomNpc<T, M> render) {
         if (render.npcLayers == null) { render.npcLayers = new ArrayList<>(); }
         render.npcLayers.add(layer);
      }
   }

}
