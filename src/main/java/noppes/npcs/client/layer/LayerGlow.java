package noppes.npcs.client.layer;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class LayerGlow<T extends EntityLivingBase> extends LayerInterface<T> {

    public LayerGlow(RenderLiving<?> renderIn) { super(renderIn); }

    @Override
    public void render(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (!npc.display.getOverlayTexture().isEmpty()) {
            if (npc.textureGlowLocation == null) {
                npc.textureGlowLocation = new ResourceLocation(npc.display.getOverlayTexture());
            }
            /*VertexConsumer iVertexBuilder;
            if (npc.display.isOverlayGlowing()) {
                iVertexBuilder = typeBuffer.getBuffer(RenderType.entityTranslucentEmissive(npc.textureGlowLocation));
            } else {
                iVertexBuilder = typeBuffer.getBuffer(RenderType.entityTranslucent(npc.textureGlowLocation));
            }
            model.render(matrixStackIn, iVertexBuilder, packedLightIn, LivingEntityRenderer.getOverlayCoords(npc, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);*/
        }
    }

    @Override
    public void rotate(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {

    }
}
