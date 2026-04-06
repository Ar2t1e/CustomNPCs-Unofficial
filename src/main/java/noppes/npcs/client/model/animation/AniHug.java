package noppes.npcs.client.model.animation;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class AniHug implements AnimationBase {

   public void animatePost(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, HumanoidModel<? extends LivingEntity> model, int animationStart) {
      float f6 = Mth.sin(model.attackTime * 3.141593F);
      float f7 = Mth.sin((1.0F - (1.0F - model.attackTime) * (1.0F - model.attackTime)) * 3.141593F);
      model.rightArm.zRot = 0.0F;
      model.leftArm.zRot = 0.0F;
      model.rightArm.yRot = -(0.1F - f6 * 0.6F);
      model.leftArm.yRot = 0.1F;
      model.rightArm.xRot = -1.570796F;
      model.leftArm.xRot = -1.570796F;
      model.rightArm.xRot -= f6 * 1.2F - f7 * 0.4F;
      model.rightArm.zRot += Mth.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
      model.leftArm.zRot -= Mth.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
      model.rightArm.xRot += Mth.sin(ageInTicks * 0.067F) * 0.05F;
      model.leftArm.xRot -= Mth.sin(ageInTicks * 0.067F) * 0.05F;
   }

   public void animatePre(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, HumanoidModel<? extends LivingEntity> model, int animationStart) {
   }

}
