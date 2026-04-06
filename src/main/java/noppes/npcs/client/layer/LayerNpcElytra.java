package noppes.npcs.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import noppes.npcs.client.model.ModelNpcElytra;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;

public class LayerNpcElytra<T extends EntityNPCInterface, M extends HumanoidModel<T>> extends LayerInterface<T, M> {

    private static final ResourceLocation WINGS_LOCATION = new ResourceLocation("textures/entity/elytra.png");
    private final ModelNpcElytra<T> elytraModel;

    public LayerNpcElytra(Context manager, LivingEntityRenderer<T, M> render) {
        super(render);
        elytraModel = new ModelNpcElytra<>(manager.bakeLayer(ModelLayers.ELYTRA));
    }

    @Override
    @SuppressWarnings("all")
    public void render(PoseStack matrixStack, MultiBufferSource typeBuffer, int packedLightIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack itemstack = npc.getItemBySlot(EquipmentSlot.CHEST);
        if (shouldRender(itemstack, npc)) {
            ResourceLocation resourcelocation;
            if (npc instanceof EntityCustomNpc) {
                if (npc.display.getCapeTexture() != null && !npc.display.getCapeTexture().isEmpty() && base instanceof PlayerModel) {
                    resourcelocation = new ResourceLocation(npc.display.getCapeTexture());
                }
                else { resourcelocation = getElytraTexture(itemstack, npc); }
            } else {
                resourcelocation = getElytraTexture(itemstack, npc);
            }
            matrixStack.pushPose();
            matrixStack.translate(0.0F, 0.0F, 0.125F);
            getParentModel().copyPropertiesTo(elytraModel);
            elytraModel.setupAnim((T) npc, limbSwing, limbSwingAmount, partialTicks, netHeadYaw, headPitch);
            VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(typeBuffer, RenderType.armorCutoutNoCull(resourcelocation), false, itemstack.hasFoil());
            elytraModel.renderToBuffer(matrixStack, vertexconsumer, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStack.popPose();
        }

    }

    @SuppressWarnings("all")
    public boolean shouldRender(ItemStack stack, EntityCustomNpc entity) {
        return stack.getItem() == Items.ELYTRA;
    }

    @SuppressWarnings("all")
    public ResourceLocation getElytraTexture(ItemStack stack, EntityCustomNpc entity) {
        return WINGS_LOCATION;
    }

    @Override
    public void rotate(PoseStack matrixStack, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) { }

}
