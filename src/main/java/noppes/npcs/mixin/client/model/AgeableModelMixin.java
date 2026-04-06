package noppes.npcs.mixin.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.AgeableListModel;
import noppes.npcs.client.renderer.RenderNPCInterface;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AgeableListModel.class, priority = 498)
public class AgeableModelMixin<T extends EntityNPCInterface> {

   @Unique
   private boolean cnpcs$isCanceled = false;

   @SuppressWarnings("unchecked")
   @Inject(
      at = {@At("HEAD")},
      method = {"renderToBuffer"},
      cancellable = true
   )
   private void renderToBuffer(PoseStack stack, VertexConsumer builder, int light, int overlay, float r, float g, float b, float a, CallbackInfo callbackInfo) {
      if (!cnpcs$isCanceled && RenderNPCInterface.currentNpc != null && RenderNPCInterface.currentNpc.display.getTint() < 0xFFFFFF) {
         cnpcs$isCanceled = true;
         int color = RenderNPCInterface.currentNpc.display.getTint();
         float red = (float)(color >> 16 & 255) / 255.0F;
         float green = (float)(color >> 8 & 255) / 255.0F;
         float blue = (float)(color & 255) / 255.0F;
         AgeableListModel<T> model = (AgeableListModel<T>) (Object) this;
         model.renderToBuffer(stack, builder, light, overlay, red, green, blue, a);
         callbackInfo.cancel();
      }
      else { cnpcs$isCanceled = false; }
   }

}
