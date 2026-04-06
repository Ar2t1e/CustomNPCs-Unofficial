package noppes.npcs.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.client.renderer.RenderCustomNpc;
import noppes.npcs.entity.EntityCustomNpc;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class LayerGlow<T extends EntityCustomNpc, M extends HumanoidModel<T>> extends RenderLayer<T, M> {

   public LayerGlow(RenderCustomNpc<T, M> npcRenderer) {
      super(npcRenderer);
   }

   public void render(@Nonnull PoseStack matrixStackIn, @Nonnull MultiBufferSource typeBuffer, int packedLightIn, T npc, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
      if (!npc.display.getOverlayTexture().isEmpty()) {
         if (npc.textureGlowLocation == null) {
            npc.textureGlowLocation = new ResourceLocation(npc.display.getOverlayTexture());
         }
         VertexConsumer iVertexBuilder;
         if (npc.display.isOverlayGlowing()) {
            iVertexBuilder = typeBuffer.getBuffer(RenderType.entityTranslucentEmissive(npc.textureGlowLocation));
         } else {
            iVertexBuilder = typeBuffer.getBuffer(RenderType.entityTranslucent(npc.textureGlowLocation));
         }
         getParentModel().renderToBuffer(matrixStackIn, iVertexBuilder, packedLightIn, LivingEntityRenderer.getOverlayCoords(npc, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
      }
   }
}
