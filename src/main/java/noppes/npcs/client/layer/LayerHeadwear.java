package noppes.npcs.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.model.ModelHeadwear;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.client.model.Model2DRenderer;

public class LayerHeadwear<T extends EntityNPCInterface, M extends HumanoidModel<T>>
        extends LayerInterface<T, M>
        implements LayerPreRender {

   private final ModelHeadwear headwear = new ModelHeadwear();

   public LayerHeadwear(LivingEntityRenderer<T, M> render) {
      super(render);
   }

   public void render(PoseStack mStack, MultiBufferSource typeBuffer, int lightMapUV, float limbSwing, float limbSwingAmount, float partialTicks, float age, float netHeadYaw, float headPitch) {
      if (CustomNpcs.HeadWearType == 1 && this.npc.textureLocation != null) {
         float red = 1.0F;
         float blue = 1.0F;
         float green = 1.0F;
         if (this.npc.hurtTime <= 0 && this.npc.deathTime <= 0) {
            int color = this.npc.display.getTint();
            red = (float)(color >> 16 & 255) / 255.0F;
            green = (float)(color >> 8 & 255) / 255.0F;
            blue = (float)(color & 255) / 255.0F;
         }

         this.base.head.translateAndRotate(mStack);
         Model2DRenderer.textureOverride = this.npc.textureLocation;
         VertexConsumer iVertex = typeBuffer.getBuffer(RenderType.entityTranslucent(this.npc.textureLocation));
         int m = OverlayTexture.pack(OverlayTexture.u(0.0F), OverlayTexture.v(npc.hurtTime > 0 || npc.deathTime > 0));
         this.headwear.render(mStack, iVertex, lightMapUV, m, red, green, blue, this.alpha());
         Model2DRenderer.textureOverride = null;
      }
   }

   public void rotate(PoseStack matrixStack, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
   }

   public void preRender(EntityCustomNpc npc) {
      this.base.hat.visible = this.base.head.visible && CustomNpcs.HeadWearType != 1;
      if (!this.base.hat.visible) {
         this.headwear.config = null;
      }

   }
}
