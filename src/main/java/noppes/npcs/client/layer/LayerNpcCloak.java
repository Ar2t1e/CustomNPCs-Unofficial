package noppes.npcs.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import noppes.npcs.entity.EntityNPCInterface;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class LayerNpcCloak<T extends EntityNPCInterface, M extends HumanoidModel<T>> extends LayerInterface<T, M> {

   public LayerNpcCloak(LivingEntityRenderer<T, M> render) {
      super(render);
   }

   public void render(PoseStack mStack, MultiBufferSource typeBuffer, int lightMapUV, float limbSwing, float limbSwingAmount, float partialTicks, float age, float netHeadYaw, float headPitch) {
      if (npc.textureCloakLocation == null) {
         if (npc.display.getCapeTexture() == null || npc.display.getCapeTexture().isEmpty() || !(base instanceof PlayerModel)) { return; }
         npc.textureCloakLocation = ResourceLocation.tryParse(npc.display.getCapeTexture());
      }
      ItemStack chestStack = npc.getItemBySlot(EquipmentSlot.CHEST);
      if (!chestStack.is(Items.ELYTRA)) {
         mStack.pushPose();
         mStack.translate(0.0D, 0.0D, 0.125D);
         double d0 = Mth.lerp(partialTicks, npc.prevChasingPosX, npc.chasingPosX) - Mth.lerp(partialTicks, npc.xo, npc.getX());
         double d1 = Mth.lerp(partialTicks, npc.prevChasingPosY, npc.chasingPosY) - Mth.lerp(partialTicks, npc.yo, npc.getY());
         double d2 = Mth.lerp(partialTicks, npc.prevChasingPosZ, npc.chasingPosZ) - Mth.lerp(partialTicks, npc.zo, npc.getZ());
         float f = npc.yBodyRotO + (npc.yBodyRot - npc.yBodyRotO);
         double d3 = Mth.sin(f * 0.017453292F);
         double d4 = -Mth.cos(f * 0.017453292F);
         float f1 = (float)d1 * 10.0F;
         f1 = Mth.clamp(f1, -6.0F, 32.0F);
         float f2 = (float)(d0 * d3 + d2 * d4) * 100.0F;
         f2 = Mth.clamp(f2, 0.0F, 150.0F);
         float f3 = (float)(d0 * d4 - d2 * d3) * 100.0F;
         f3 = Mth.clamp(f3, -20.0F, 20.0F);
         if (f2 < 0.0F) { f2 = 0.0F; }

         f1 += Mth.sin(Mth.lerp(partialTicks, npc.walkDistO, npc.walkDist) * 6.0F) * 32.0F * partialTicks;
         if (npc.isCrouching()) { f1 += 25.0F; }

         mStack.mulPose(Axis.XP.rotationDegrees(6.0F + f2 / 2.0F + f1));
         mStack.mulPose(Axis.ZP.rotationDegrees(f3 / 2.0F));
         mStack.mulPose(Axis.YP.rotationDegrees(180.0F - f3 / 2.0F));
         ((PlayerModel<T>)base).renderCloak(mStack, typeBuffer.getBuffer(RenderType.entityTranslucent(npc.textureCloakLocation)), lightMapUV, OverlayTexture.NO_OVERLAY);
         mStack.popPose();
      }
   }

   public void rotate(PoseStack matrixStack, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
   }
}
