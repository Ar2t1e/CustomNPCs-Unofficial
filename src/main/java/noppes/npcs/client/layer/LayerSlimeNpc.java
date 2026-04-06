package noppes.npcs.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import noppes.npcs.client.model.ModelNpcSlime;
import noppes.npcs.entity.EntityNpcSlime;
import org.jetbrains.annotations.NotNull;

public class LayerSlimeNpc<T extends EntityNpcSlime, M extends ModelNpcSlime<T>> extends RenderLayer<T, M> {

   public final LivingEntityRenderer<T, M> renderer;
   public final EntityModel<T> slimeModel = new ModelNpcSlime<>(0);

   public LayerSlimeNpc(LivingEntityRenderer<T, M> renderer) {
      super(renderer);
      this.renderer = renderer;
   }

   public void render(@NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int packedLightIn, T living, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
      if (!living.isInvisible()) {
         this.getParentModel().copyPropertiesTo(this.slimeModel);
         this.slimeModel.prepareMobModel(living, limbSwing, limbSwingAmount, partialTicks);
         this.slimeModel.setupAnim(living, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
         VertexConsumer iVertexBuilder = bufferIn.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(living)));
         this.slimeModel.renderToBuffer(matrixStackIn, iVertexBuilder, packedLightIn, LivingEntityRenderer.getOverlayCoords(living, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
      }

   }
}
