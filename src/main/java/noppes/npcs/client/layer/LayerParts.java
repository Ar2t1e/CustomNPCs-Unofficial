package noppes.npcs.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import noppes.npcs.client.parts.*;
import noppes.npcs.constants.BodyPart;
import noppes.npcs.constants.EnumParts;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.shared.common.util.NopVector3f;
import org.jetbrains.annotations.NotNull;

public class LayerParts<T extends EntityCustomNpc, M extends HumanoidModel<T>> extends RenderLayer<T, M> {

   public LayerParts(LivingEntityRenderer<T, M> render) { super(render); }

   @Override
   public void render(@NotNull PoseStack mStack, @NotNull MultiBufferSource typeBuffer, int lightMapUV, @NotNull EntityCustomNpc player, float limbSwing, float limbSwingAmount, float partialTicks, float age, float netHeadYaw, float headPitch) {
      ModelData data = ModelData.get(player);
      for (MpmPartData part : data.mpmParts) {
         MpmPart mp = part.getPart();
         if (mp != null && mp.renderType != PartRenderType.NONE && mp.isEnabled) {
            rotate(data, (MpmPartAbstractClient) mp, player, getParentModel(), limbSwing, limbSwingAmount, partialTicks, age, netHeadYaw, headPitch);
            renderPart(part, (MpmPartAbstractClient) mp, mStack, typeBuffer, lightMapUV, player, getParentModel(), data);
         }
      }
      data.startMoveAnimation = false;
      data.startAnimation = false;
   }

   public static void renderPart(MpmPartData data, MpmPartAbstractClient partC, PoseStack mStack, MultiBufferSource typeBuffer, int lightMapUV,
                                 EntityCustomNpc player, HumanoidModel<? extends LivingEntity> model, ModelData pdata) {
      mStack.pushPose();
      boolean shouldRender = true;
      if (partC.bodyPart == BodyPart.HEAD) { model.head.translateAndRotate(mStack); }
      if (partC.bodyPart == BodyPart.BODY) { model.body.translateAndRotate(mStack); }
      ModelPartWrapper rModelPart;
      ModelPartWrapper lModelPart;
      ModelPartConfig config;
      if (partC.bodyPart == BodyPart.LEGS) {
         rModelPart = partC.getPart("right_leg");
         lModelPart = partC.getPart("left_leg");
         if (rModelPart != null) {
            shouldRender = false;
            mStack.pushPose();
            config = pdata.getPartConfig(EnumParts.LEG_RIGHT);
            mStack.translate(0.0F, config.transY * 2.0F, 0.0F);
            mStack.scale(config.scaleX, config.scaleY, config.scaleZ);
            if (lModelPart != null) { lModelPart.setVisible(false); }
            rModelPart.setVisible(true);
            partC.render(data, mStack, typeBuffer, lightMapUV, player);
            mStack.popPose();
         }
         if (lModelPart != null) {
            shouldRender = false;
            mStack.pushPose();
            config = pdata.getPartConfig(EnumParts.LEG_LEFT);
            mStack.translate(0.0F, config.transY * 2.0F, 0.0F);
            mStack.scale(config.scaleX, config.scaleY, config.scaleZ);
            if (rModelPart != null) { rModelPart.setVisible(false); }
            lModelPart.setVisible(true);
            partC.render(data, mStack, typeBuffer, lightMapUV, player);
            mStack.popPose();
         }
         if (shouldRender) {
            config = pdata.getPartConfig(EnumParts.LEG_LEFT);
            mStack.translate(0.0F, config.transY * 2.0F, 0.0F);
            mStack.scale(config.scaleX, config.scaleY, config.scaleZ);
         }
      }
      if (partC.bodyPart == BodyPart.ARMS) {
         rModelPart = partC.getPart("right_arm");
         lModelPart = partC.getPart("left_arm");
         if (rModelPart != null) {
            shouldRender = false;
            mStack.pushPose();
            config = pdata.getPartConfig(EnumParts.ARM_RIGHT);
            mStack.translate(0.25F * (config.scaleX - 1.0F), config.transY + (1.0F - config.scaleY) * 0.125F, 0.0F);
            mStack.scale(config.scaleX, config.scaleY, config.scaleZ);
            if (lModelPart != null) { lModelPart.setVisible(false); }
            rModelPart.setVisible(true);
            partC.render(data, mStack, typeBuffer, lightMapUV, player);
            mStack.popPose();
         }
         if (lModelPart != null) {
            shouldRender = false;
            mStack.pushPose();
            config = pdata.getPartConfig(EnumParts.ARM_LEFT);
            mStack.translate(-0.25F * (config.scaleX - 1.0F), config.transY + (1.0F - config.scaleY) * 0.125F, 0.0F);
            mStack.scale(config.scaleX, config.scaleY, config.scaleZ);
            if (rModelPart != null) { rModelPart.setVisible(false); }
            lModelPart.setVisible(true);
            partC.render(data, mStack, typeBuffer, lightMapUV, player);
            mStack.popPose();
         }
         if (shouldRender) {
            config = pdata.getPartConfig(EnumParts.ARM_LEFT);
            mStack.translate(0.0F, config.transY + (1.0F - config.scaleY) * 0.125F, 0.0F);
            mStack.scale(config.scaleX, config.scaleY, config.scaleZ);
         }
      }
      if (shouldRender) { partC.render(data, mStack, typeBuffer, lightMapUV, player); }
      mStack.popPose();
   }

   private void rotate(ModelData playerdata, MpmPartAbstractClient part, EntityCustomNpc player, HumanoidModel<? extends LivingEntity> base, float limbSwing, float limbSwingAmount, float partialTicks, float age, float ignoredNetHeadYaw, float ignoredHeadPitch) {
      part.animationData.animation(19, (int)age, partialTicks);
      if ((double)limbSwingAmount > 0.01D) {
         if (player.onGround()) {
            if (player.ais.getAnimation() == 7) { playerdata.setAnimation(7); }
            else { playerdata.setAnimation(15); }
         }
         else { playerdata.setAnimation(17); }
      }
      else if (player.ais.getAnimation() == 0) { playerdata.setAnimation(16); }
      else { playerdata.setAnimation(player.ais.getAnimation()); }
      int moveAnimation = playerdata.getMoveAnimation(player);
      if (playerdata.startMoveAnimation) { part.animationData.start(moveAnimation); }
      boolean didAnimation = false;
      if (playerdata.animation != 0) {
         if (playerdata.startAnimation) { part.animationData.start(playerdata.animation); }
         didAnimation = part.animationData.animation(playerdata.animation, (int)age, partialTicks);
      }
      if (didAnimation || moveAnimation != 16 && moveAnimation != 18) {
         part.animationData.animation(moveAnimation, Mth.cos(limbSwing * 0.6662F) * limbSwingAmount / 2.0F + 0.5F);
      }
      else {
         part.animationData.animation(moveAnimation, (int)age, partialTicks);
      }
      HumanoidModel<? extends LivingEntity> model;
      ModelPartWrapper modelPartL;
      if (part.animationType == PartBehaviorType.LEGS) {
         model = getParentModel();
         modelPartL = part.getPart("right_leg");
         if (modelPartL != null) {
            modelPartL.setRot(new NopVector3f(model.rightLeg.xRot, model.rightLeg.yRot, model.rightLeg.zRot));
            modelPartL.setPos(new NopVector3f(model.rightLeg.x, model.rightLeg.y, model.rightLeg.z));
         }
         modelPartL = part.getPart("left_leg");
         if (modelPartL != null) {
            modelPartL.setRot(new NopVector3f(model.leftLeg.xRot, model.leftLeg.yRot, model.leftLeg.zRot));
            modelPartL.setPos(new NopVector3f(model.leftLeg.x, model.leftLeg.y, model.leftLeg.z));
         }
      }
      if (part.animationType == PartBehaviorType.ARMS) {
         model = getParentModel();
         modelPartL = part.getPart("right_arm");
         if (modelPartL != null) {
            modelPartL.setRot(new NopVector3f(model.rightArm.xRot, model.rightArm.yRot, model.rightArm.zRot));
            modelPartL.setPos(new NopVector3f(model.rightArm.x, model.rightArm.y, model.rightArm.z));
         }
         modelPartL = part.getPart("left_arm");
         if (modelPartL != null) {
            modelPartL.setRot(new NopVector3f(model.leftArm.xRot, model.leftArm.yRot, model.leftArm.zRot));
            modelPartL.setPos(new NopVector3f(model.leftArm.x, model.leftArm.y, model.leftArm.z));
         }
      }
      if (part.animationType == PartBehaviorType.BEARD) {
         part.rot = part.rot.set(base.head.xRot < 0.0F ? 0.0F : -base.head.xRot, part.rot.y, part.rot.z);
      }
      if (part.animationType == PartBehaviorType.HAIR) {
         ModelPart head = base.head;
         if (head.xRot < 0.0F) {
            part.rot = part.rot.set(-head.xRot * 1.2F, part.rot.y, part.rot.z);
            if (head.xRot > -1.0F) { part.pos = part.pos.set(part.pos.x, -head.xRot * 1.5F, -head.xRot * 1.5F); }
         }
         else { part.pos = NopVector3f.ZERO; }
      }
      float rot;
      float motion;
      float speed;
      float y;
      ModelPartWrapper modelPart;
      if (part.animationType == PartBehaviorType.WINGS) {
         modelPart = part.getPart("right_wing");
         modelPartL = part.getPart("left_wing");
         if (player.level().isEmptyBlock(player.blockPosition().below())) {
            speed = Math.abs(Mth.sin(limbSwing * 0.033F + 3.1415927F) * 0.4F) * limbSwingAmount;
            y = 0.55F + 0.5F * speed;
            rot = motion = y * 0.5F * Mth.sin(age * 0.35F);
         } else {
            motion = Mth.cos(age * 0.09F) * 0.05F + 0.05F;
            rot = Mth.sin(age * 0.067F) * 0.05F;
         }
         modelPart.setRot(modelPart.oriRot.add(rot, rot, motion));
         modelPartL.setRot(modelPartL.oriRot.add(rot, -rot, -motion));
      }
      if (part.animationType == PartBehaviorType.WINGS2) {
         modelPart = part.getPart("right_wing");
         modelPartL = part.getPart("left_wing");
         if (player.level().isEmptyBlock(player.blockPosition().below())) {
            motion = Math.abs(Mth.sin(limbSwing * 0.033F + 3.1415927F) * 0.4F) * limbSwingAmount;
            speed = 0.55F + 0.5F * motion;
            y = Mth.sin(age * 0.35F);
            rot = y * 0.5F * speed;
         }
         else { rot = Mth.sin(age * 0.07F) * 0.44F; }
         modelPart.setRot(modelPart.oriRot.add(0.0F, rot, 0.0F));
         modelPartL.setRot(modelPartL.oriRot.add(0.0F, -rot, 0.0F));
      }
   }

}
